package ch.digisyn.nova;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.util.ClassUtilities;

import ch.digisyn.nova.content.Content;
import ch.digisyn.nova.content.Movie;

public class NOVAControl implements ISyncListener, Runnable, IConstants {
	private static final int N_BUFS            = 1024;
	private static final int MODULE_QUEUE_SIZE = 4;
	private static final int FRAME_QUEUE_SIZE  = MODULE_QUEUE_SIZE + 4;
	private static final int DEFAULT_CONTENT   = 0;

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

	public NOVAControl(NOVAConfig config) throws IOException, InterruptedException {
		this.config     = config;
		this.selfIpAddr = Inet4Address.getLocalHost().getAddress();
		this.socket     = new DatagramSocket();
		this.selfIpPort = this.socket.getLocalPort();

		EnetInterface device = null;
		for(EnetInterface eif : EnetInterface.interfaces()) { 
			System.out.println("Interface:" + eif);
			if(useInterface(eif, false)) {
				System.out.println("Using " + eif);
				eif.open();
				device = eif;
				break;
			}
		}
		if(device == null)
			throw new IOException("No ethernet interface found.");

		this.device = device;

		int maxModule = 0;
		for(int m : config.getModules()) {
			novaIpAddr[m] = new byte[] {(byte)NOVA_IP_0, (byte)NOVA_IP_1, (byte)NOVA_IP_2, (byte)m};
			maxModule     = Math.max(maxModule, m);
		}

		for(int i = 0; i < FRAME_QUEUE_SIZE; i++)
			frameQ.add(new int[maxModule + 1][config.dimI() * config.dimJ() * config.dimK() * 3]);

		disp           = new Dispatcher(device, config);
		httpServer         = PROPS.getProperty("http") == null ? new HTTPServer(8181) : new HTTPServer(PROPS.getProperty("http"), 80);

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
		httpServer.start();

		tcpServer     = new TCPServer(PROPS.getProperty("tcp"), TCPServer.PORT);
		tcpServer.start();
		
		Thread sender = new Thread(this, "Voxel Streamer");
		sender.setPriority(Thread.MIN_PRIORITY);
		sender.start();

		for(;;) {
			try {
				getStatus();
				System.out.println("NOVA Status: " + config.numOperational() + " of " + config.numModules() + " operational");
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
				t.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length != 1) {
			System.out.println("usage: " + NOVAControl.class.getName() + " <config_file>");
			System.exit(0);
		}
		if(System.getProperty("java.vm.name").contains("64")) {
			System.out.println(NOVAControl.class.getName() + " requires a 32 Bit VM, current VM is '" + System.getProperty("java.vm.name") + "'");
			System.exit(0);
		}
		
		PROPS = new Properties();
		PROPS.load(new FileReader(args[0]));

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

		NOVAConfig novaConfig = new NOVAConfig(config);

		int numFrames = 0;
		try {
			numFrames = Integer.parseInt(PROPS.getProperty("duration")) * 25;
		} catch(Throwable t) {}
		
		for(String content : PROPS.getProperty("content").split("[,]")) {
			try {
				@SuppressWarnings("unchecked")
				Class<Content> cls = (Class<Content>) Class.forName("ch.digisyn.nova.content." + content);
				Content c = cls.getConstructor(ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int, ClassUtilities.CLS_int).
						newInstance(novaConfig.dimI(), novaConfig.dimJ(), novaConfig.dimK(), numFrames);
				for(Content ci : c.getContents()) {
					System.out.println("Adding content '" + ci + "'");
					contents.add(ci);
				}
			} catch(Throwable t) {
				System.out.println("Could not load content '" + content + "'");
			}
		}

		try {
			brightness = Double.parseDouble(PROPS.getProperty("brightness"));
		}catch (Throwable t) {}

		new NOVAControl(novaConfig);
	}

	private static double        red        = 1.0;
	private static double        green      = 1.0;
	private static double        blue       = 1.0;
	private static double        brightness = 0.5;
	private static double        speed      = 0;
	private static int           content    = DEFAULT_CONTENT;
	private static AtomicBoolean reset      = new AtomicBoolean();

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
	}

	public static void setGreen(double value) {
		green = value;
	}

	public static void setBlue(double value) {
		blue = value;
	}

	public static void setBrightness(double value) {
		brightness = value;
	}

	public static void setSpeed(double value) {
		speed = value;
	}

	public static void setContent(int value) {
		try{contents.get(content).stop();}catch(Throwable t) {}
		content = value % contents.size();
		System.out.println("setContent(" + content + ")" + contents.get(content));
		try{contents.get(content).start();}catch(Throwable t) {}
	}

	public boolean isOn() {
		return syncGen != null;
	}

	public void novaOn() throws IOException, InterruptedException {
		if(!(isOn()) && device != null) {
			System.out.println("NOVA ON");
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

	void reset() throws IOException, InterruptedException {
		System.out.print("Resetting Modules:");
		for(int m : config.getModules())
			System.out.print(" " + m);
		System.out.println();		
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
		System.out.println("Reset done.");
	}

	public void novaOff() {
		if(isOn()) {
			System.out.println("NOVA OFF");
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
				txQ.add(frame);
				if(!(continueWithContent))
					setContent(getContent() + 1);
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

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
				System.out.println("Frame queue underrun");
				return;
			}

			for(int m : config.getModules()) {
				PacketUtils.rgb(packet, IConstants.UDP_PAYLOAD_OFF, seqNum + MODULE_QUEUE_SIZE, frame[m]);
				send(packet, m);
			}

			frameQ.add(frame);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

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
				System.out.println("No modules found, retry " + (1 + i));
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public static boolean useInterface(EnetInterface eif, boolean forSync) {
		String eth = PROPS.getProperty(forSync ? "sync" : "nova");
		if(eth == null)
			eth = "eth0";

		return eif.toString().contains("Intel") || eif.toString().contains(eth);	
	}

}
