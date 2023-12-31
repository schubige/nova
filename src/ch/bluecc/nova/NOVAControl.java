package ch.bluecc.nova;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corebounce.util.Log;

import ch.bluecc.nova.content.Content;
import ch.bluecc.nova.content.Movie;

import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.util.ClassUtilities;

public class NOVAControl implements ISyncListener, Runnable, IConstants {
	private static final int     N_BUFS            = 1024;
	private static final int     MODULE_QUEUE_SIZE = 4;
	private static final int     FRAME_QUEUE_SIZE  = MODULE_QUEUE_SIZE + 4;
	private static final int     DEFAULT_CONTENT   = 0;
	private static final String  SP_ROOT_DIR       = "rootDir"; //$NON-NLS-1$
	private static double        red               = 1.0;
	private static double        green             = 1.0;
	private static double        blue              = 1.0;
	private static double        brightness        = 0.5;
	private static double        speed             = 0;
	private static int           content           = DEFAULT_CONTENT;
	private static AtomicBoolean reset             = new AtomicBoolean();
	private static String        CONTROL_PARAMS    = "controlParams.properties"; //$NON-NLS-1$

	private final HTTPServer                   httpServer;
	private final TCPServer                    tcpServer;
	private final ParamHandler                 paramHandler;
	private final UIHandler                    uiHandler;
	private SyncGenerator                      syncGen;
	private final DatagramSocket               socket;
	private final NOVAConfig                   config;
	private final byte[][]                     packets    = new byte[N_BUFS][1100 + IConstants.UDP_PAYLOAD_OFF];
	private int                                bufPtr;
	private final byte[]                       selfIpAddr;
	private final int                          selfIpPort;
	private final byte[][]                     novaIpAddr = new byte[101][];
	private final LinkedBlockingQueue<int[][]> frameQ     = new LinkedBlockingQueue<int[][]>();
	private final LinkedBlockingQueue<int[][]> txQ        = new LinkedBlockingQueue<int[][]>();
	private final EnetInterface                device;
	private final Dispatcher                   disp;
	static final  List<Content>                contents   = new ArrayList<Content>();
	static        Properties                   PROPS      = System.getProperties();

	@SuppressWarnings("nls")
	public NOVAControl(NOVAConfig config) throws IOException, InterruptedException {
		this.config     = config;
		this.selfIpAddr = Inet4Address.getLocalHost().getAddress();
		this.socket     = new DatagramSocket();
		this.selfIpPort = this.socket.getLocalPort();

		for(EnetInterface eif : EnetInterface.interfaces()) 
			Log.info("Interface:" + eif);

		EnetInterface device = null;
		for(EnetInterface eif : EnetInterface.interfaces()) {
			if(useInterface(eif, false)) {
				Log.info("Using " + eif);
				eif.open();
				device = eif;
				break;
			}
		}

		if(device == null) {
			throw new IOException("No ethernet interface found.");
		}

		this.device = device;

		int maxModule = 0;
		for(int m : config.getModules()) {
			novaIpAddr[m] = new byte[] {(byte)NOVA_IP_0, (byte)NOVA_IP_1, (byte)NOVA_IP_2, (byte)m};
			maxModule     = Math.max(maxModule, m);
		}

		for(int i = 0; i < FRAME_QUEUE_SIZE; i++)
			frameQ.add(new int[maxModule + 1][config.dimI() * config.dimJ() * config.dimK() * 3]);

		disp           = new Dispatcher(device, config);
		httpServer     = new HTTPServer(null, 80);

		uiHandler      = new UIHandler(httpServer);
		paramHandler   = new ParamHandler(httpServer);

		httpServer.setDefaultHandler(uiHandler);
		httpServer.addHandler(uiHandler,    "/");
		httpServer.addHandler(new RedirectHandler(httpServer), "/ncsi.txt");
		httpServer.addHandler(uiHandler,    "/index.html");
		httpServer.addHandler(paramHandler, "/nova/red");
		httpServer.addHandler(paramHandler, "/nova/green");		
		httpServer.addHandler(paramHandler, "/nova/blue");
		httpServer.addHandler(paramHandler, "/nova/brightness");
		httpServer.addHandler(paramHandler, "/nova/speed");
		httpServer.addHandler(paramHandler, "/nova/content");
		httpServer.addHandler(paramHandler, "/nova/color");
		httpServer.addHandler(paramHandler, "/nova/reset");
		httpServer.addHandler(paramHandler, "/nova/reload");
		httpServer.start();

		tcpServer     = new TCPServer(localIp(PROPS.getProperty("tcp")), TCPServer.PORT);
		tcpServer.start();

		Thread sender = new Thread(this, "Voxel Streamer");
		sender.setPriority(Thread.MIN_PRIORITY);
		sender.start();

		for(;;) {
			try {
				getStatus();
				Log.info("NOVA Status: " + config.numOperational() + " of " + config.numModules() + " operational");
				if(config.isOperational()) {
					if(!(isOn()))
						novaOn();
				} else {
					if(isOn()) {
						novaOff();
						System.exit(0);
					}
				}
				Thread.sleep(isOn() ? 10000 : 1000);
			} catch(Throwable t) {
				Log.severe(t);
			}
		}
	}

	@SuppressWarnings("nls")
	private String localIp(String ip) {
		if(ip == null) {
			try {
				ip = "";
				byte[] addr = InetAddress.getLocalHost().getAddress();
				for(int i = 0; i < addr.length; i++) {
					if(i > 0) ip += ".";
					ip += (addr[i] & 0xFF);
				}
				if(addr.length != 4) throw new UnknownHostException("Not an IPv4 address:" + ip);
			} catch (UnknownHostException e) {
				Log.severe(e);
				ip = "127.0.01";
			}
		}
		return ip; 
	}

	@SuppressWarnings({ "nls", "unchecked" })
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length != 1) {
			System.out.println("usage: " + NOVAControl.class.getName() + " <config_file>");
			System.exit(0);
		}
		if(System.getProperty("os.name").toLowerCase().contains("windows") && System.getProperty("java.vm.name").contains("64")) {
			Log.severe(NOVAControl.class.getName() + " requires a 32 Bit VM, current VM is '" + System.getProperty("java.vm.name") + "'");
		}
		
		PROPS = new Properties();
		File  propFile = new File(args[0]).getAbsoluteFile();
		PROPS.load(new FileReader(propFile));
		PROPS.put(SP_ROOT_DIR, propFile.getParent());
		

		if(PROPS.getProperty("movies") != null) {
			File movies = new File(PROPS.getProperty("movies"));
			if(movies.exists() && movies.isDirectory()) {
				Movie.ROOT_DIR = movies;
			}
		}

		int dimI = 0;
		int dimJ = 0;
		int[][] tmp = new int[100][100];
		for(int i = 0; i < tmp.length; i++)
			for(int j = 0; j < tmp[i].length; j++) {
				String addr = PROPS.getProperty("addr_" + i + "_" + j);
				if(addr != null) {
					tmp[i][j] = Integer.parseInt(addr.trim());
					dimI = Math.max(dimI, i + 1);
					dimJ = Math.max(dimJ, j + 1);
				}
			}

		int config[][] = new int[dimI][dimJ];
		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++)
				config[i][j] = tmp[i][j];

		NOVAConfig novaConfig = new NOVAConfig(config, PROPS.getProperty("flip", "f").toLowerCase().startsWith("t"));
		
		int numFrames = 0;
		try {
			numFrames = Integer.parseInt(PROPS.getProperty("duration")) * 25;
		} catch(Throwable t) {}

		for(String content : PROPS.getProperty("content").split("[,]")) {
			try {
				if("AUTO".equals(content)) {
					File clsFolder = new File(ClassUtilities.getClassLocation(Content.class).toURI());
					clsFolder = new File(clsFolder, "ch/bluecc/nova/content");
					Log.info("Loading content from '" + clsFolder + "'");
					for(File f : clsFolder.listFiles()) {
						if(!(f.getName().endsWith(".class")))
							continue;
						try {
							Class<Content> cls = (Class<Content>) Class.forName("ch.bluecc.nova.content." + f.getName().substring(0, f.getName().indexOf('.')));							
							if(!(cls.getSuperclass().equals(Content.class)))
								continue;
							
							Content c = cls.getConstructor(ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int).
									newInstance(novaConfig.dimI(), novaConfig.dimJ(), novaConfig.dimK(), numFrames);
							for(Content ci : c.getContents()) {
								Log.info("Adding content '" + ci + "'");
								contents.add(ci);
							}
						} catch(Throwable t) {
							Log.warning(t, "Could not load content '" + content + "'");
						}							
					}
				} else {
					Class<Content> cls = (Class<Content>) Class.forName("ch.bluecc.nova.content." + content);
					Content c = cls.getConstructor(ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int).
							newInstance(novaConfig.dimI(), novaConfig.dimJ(), novaConfig.dimK(), numFrames);
					for(Content ci : c.getContents()) {
						Log.info("Adding content '" + ci + "'");
						contents.add(ci);
					}					
				}
			} catch(Throwable t) {
				Log.warning(t, "Could not load content '" + content + "'");
			}
		}

		try {
			brightness = Double.parseDouble(PROPS.getProperty("brightness"));
		} catch (Throwable t) {}

		readControlParams();
		
		new NOVAControl(novaConfig);
	}
	
	@SuppressWarnings("nls")
	static private void readControlParams() {
		Properties props = new Properties();
		try {
			FileReader in = new FileReader(new File(PROPS.getProperty(SP_ROOT_DIR, "."), CONTROL_PARAMS));
			props.load(in);
			in.close();
			
			setRed       (Double.parseDouble(props.getProperty("red",        "" + red)));
			setGreen     (Double.parseDouble(props.getProperty("green",      "" + green)));
			setBlue      (Double.parseDouble(props.getProperty("blue",       "" + blue)));
			setBrightness(Double.parseDouble(props.getProperty("brightness", "" + brightness)));
			setSpeed     (Double.parseDouble(props.getProperty("speed",      "" + speed)));
			setContent   (Integer.parseInt(  props.getProperty("content",    "" + content)));
		} catch(Throwable t) {
			Log.severe(t);
		}
	}
	
	@SuppressWarnings("nls")
	static private void writeControlParams() {
		Log.info("writeControlParams()");
		Properties props = new Properties();
		props.put("red",        "" + getRed());
		props.put("green",      "" + getGreen());
		props.put("blue",       "" + getBlue());
		props.put("brightness", "" + getBrightness());
		props.put("speed",      "" + getSpeed());
		props.put("content",    "" + getContent());
		try {
			FileWriter out = new FileWriter(new File(PROPS.getProperty(SP_ROOT_DIR, "."), "controlParams.properties"));
			props.store(out, "NOVA control parameters");
			out.close();
		} catch(Throwable t) {
			Log.severe(t);
		}
	}

	public static void resetNOVA() {
		reset.set(true);
	}

	public static int getIntRed() {
		return (int) (red * brightness * brightness * 1023.0);
	}

	public static int getIntGreen() {
		return (int) (green * brightness * brightness * 1023.0);
	}

	public static int getIntBlue() {
		return (int) (blue * brightness * brightness * 1023.0);
	}

	public static double getRed() {
		return red;
	}

	public static double getGreen() {
		return green;
	}

	public static double getBlue() {
		return blue;
	}

	public static double getBrightness() {
		return brightness;
	}

	public static double getSpeed() {
		return speed;
	}

	public static int getContent() {
		return content;
	}

	public static void setRed(double value) {
		red = value;
		writeControlParams();
	}

	public static void setGreen(double value) {
		green = value;
		writeControlParams();
	}

	public static void setBlue(double value) {
		blue = value;
		writeControlParams();
	}

	public static void setBrightness(double value) {
		brightness = value;
		writeControlParams();
	}

	public static void setSpeed(double value) {
		speed = value;
		writeControlParams();
	}

	@SuppressWarnings("nls")
	public static void setContent(int value) {
		try{contents.get(content).stop();}catch(Throwable t) {}
		content = value % contents.size();
		Log.info("setContent(" + content + ")" + contents.get(content));
		try{contents.get(content).start();}catch(Throwable t) {}
		writeControlParams();
	}

	public boolean isOn() {
		return syncGen != null;
	}

	@SuppressWarnings("nls")
	public void novaOn() throws IOException, InterruptedException {
		if(!(isOn()) && device != null) {
			Log.info("NOVA ON");
			reset();
			syncGen = new SyncGenerator(device, disp);
			for(int i = -MODULE_QUEUE_SIZE; i < 0; i++)
				sync(i);
			syncGen.startSync();
			syncGen.setListener(this);
		}
	}

	private synchronized byte[] packet() {
		return packets[bufPtr++ % N_BUFS];
	}

	private void send(byte[] packet, int module) throws IOException {
		AddressUtils.MMUX(packet, 0, module);
		System.arraycopy(device.getAddr(), 0, packet, 6, 6);
		PacketUtils.UDP(packet, 12, selfIpAddr, selfIpPort, novaIpAddr[module], 3210, 1100);
		device.send(packet);
	}

	@SuppressWarnings("nls")
	void reset() throws IOException, InterruptedException {
		StringBuilder msg = new StringBuilder("Resetting Modules:");
		for(int m : config.getModules())
			msg.append(" ").append(m);
		msg.append('\n');
		Log.info(msg.toString());
		for(int i = 0; i < 4; i++) {
			for(int m : config.getModules()) {
				byte[] packet = packet();
				PacketUtils.reset(packet, IConstants.UDP_PAYLOAD_OFF);
				send(packet, m);
			}
			Thread.sleep(200);
		}
		for(int m : config.getModules()) {
			byte[] packet = packet();
			PacketUtils.autoid(packet, IConstants.UDP_PAYLOAD_OFF);
			send(packet, m);
		}
		Thread.sleep(1000);
		Log.info("Reset done.");
	}

	@SuppressWarnings("nls")
	public void novaOff() {
		if(isOn()) {
			Log.info("NOVA OFF");
			syncGen.setListener(null);
			syncGen.dispose();
			syncGen = null;
		}
	}

	@Override
	public void run() {
		final float[] fframe = new float[config.dimI() * config.dimJ() * config.dimK() * 3]; 

		for(double time = 0; ; time += 0.04 * Math.pow(2, getSpeed())) {
			try {
				final int[][] frame = frameQ.take();
				final float   r     = getIntRed();
				final float   g     = getIntGreen();
				final float   b     = getIntBlue();
				boolean continueWithContent = contents.get(getContent()).fillFrame(fframe, time);

				for(int m : config.getModules()) {
					int off = config.getFrameOffset(m);
					int idx = 0;

					final int[] pixels = frame[m];
					if(config.flipK()) {
						for(int i = config.moduleDimI(); i-- > 0; ) {
							for(int j = config.moduleDimJ(); j-- > 0; ) {
								for(int k = 0; k < config.moduleDimK(); k++) {
									final int x = off + 3 * (j * config.dimI() * config.dimK() + i * config.dimK() + ((config.dimK()-1) - k));
									final float fr = fframe[x];
									final float fg = fframe[x+1];
									final float fb = fframe[x+2];

									pixels[idx++] = (((int)(r * fr * fr) << 20) & 0x3FF00000)
											|   (((int)(g * fg * fg) << 10) & 0x000FFC00) 
											|   ((int) (b * fb * fb)        & 0x000003FF);
								}
							}
						}
					} else {
						for(int i = config.moduleDimI(); i-- > 0; ) {
							for(int j = config.moduleDimJ(); j-- > 0; ) {
								for(int k = 0; k < config.moduleDimK(); k++) {
									final int x = off + 3 * (j * config.dimI() * config.dimK() + i * config.dimK() + k);
									final float fr = fframe[x];
									final float fg = fframe[x+1];
									final float fb = fframe[x+2];

									pixels[idx++] = (((int)(r * fr * fr) << 20) & 0x3FF00000)
											|   (((int)(g * fg * fg) << 10) & 0x000FFC00) 
											|   ((int) (b * fb * fb)        & 0x000003FF);
								}
							}
						}
					}
				}
				txQ.add(frame);
				if(!(continueWithContent)) {
					setContent(getContent() + 1);
				}
			} catch(Throwable t) {
				Log.severe(t);
			}
		}
	}

	@SuppressWarnings("nls")
	@Override
	public void sync(int seqNum) {
		try {
			if(reset.getAndSet(false)) {
				novaOff();
				novaOn();
				return;
			}

			final int[][] frame  = txQ.poll();
			final byte[]  packet = packet();

			if(frame == null) {
				Log.info("Frame queue underrun");
				return;
			}

			for(int m : config.getModules()) {
				PacketUtils.rgb(packet, IConstants.UDP_PAYLOAD_OFF, seqNum + MODULE_QUEUE_SIZE, frame[m]);
				send(packet, m);
			}

			frameQ.add(frame);
		} catch(Throwable t) {
			Log.severe(t);
		}
	}

	@SuppressWarnings("nls")
	public void getStatus() {
		byte[] packet = new byte[ADDR_LEN + PROT_LEN + DATA_LEN];
		AddressUtils.BROADCAST(packet, 0);
		AddressUtils.SELF(device, packet, 6);
		PacketUtils.status(packet, ADDR_LEN, 0);

		for(int i = 0; i < 5; i++) {
			try {
				device.send(packet);
				Thread.sleep(500);
				if(config.numOperational() > 0)
					return;
				Log.info("No modules found, retry " + (1 + i));
			} catch(Throwable t) {
				Log.severe(t);
			}
		}
	}

	@SuppressWarnings("nls")
	public static boolean useInterface(EnetInterface eif, boolean forSync) {
		if(eif == EnetInterface.DUMMY) return true;
		String eth = PROPS.getProperty(forSync ? "sync" : "nova");
		if(eth == null)
			eth = "eth0";

		return eif.toString().contains("Intel") || eif.toString().contains(eth);	
	}

}
