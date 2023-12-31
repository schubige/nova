package ch.bluecc.nova;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corebounce.io.Utilities;
import org.corebounce.util.Log;
import org.jnetpcap.ByteBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;

@SuppressWarnings("nls")
public class EnetInterface implements IConstants {
	private static EnetInterface[]            interfaces;
	private final PcapIf                      device;
	private       Pcap                        pcap;
	private final byte[]                      addr;
	private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
	private final AtomicBoolean               close = new AtomicBoolean();
	private static final int                  SEND_DELAY = 0;

	static {
		try {
			String osname = System.getProperty("os.name").toLowerCase();
			String arch   = System.getProperty("os.arch").toLowerCase();
			File   tmp    = File.createTempFile("NOVAatHOME", "lck");
			System.out.println("os.name:" + osname);
			System.out.println("os.arch:" + arch);
			System.out.println("tmp:" + tmp.getAbsolutePath());
			File soDst;
			if(osname.contains("windows")) {
				soDst = new File(tmp.getParentFile(), "jnetpcap.dll");
				Utilities.copy(EnetInterface.class.getResourceAsStream("/native/" + soDst.getName()), new FileOutputStream(soDst));
			} else if(osname.contains("mac os")) {
				soDst  = new File(tmp.getParentFile(), "libjnetpcap.jnilib");
				Utilities.copy(EnetInterface.class.getResourceAsStream("/native" + (arch.contains("64") ? "/x64/" : "/x86/") + soDst.getName()), new FileOutputStream(soDst));
			} else {
				soDst  = new File(tmp.getParentFile(), "libjnetpcap.so");
				Utilities.copy(EnetInterface.class.getResourceAsStream("/native" + (arch.contains("64") ? 
						"/x64/" : (arch.contains("arm") ? "/arm/" : "/x86/")) 
						+ soDst.getName()), new FileOutputStream(soDst));
			}
			if(!(soDst.exists()))
				throw new FileNotFoundException(soDst.getAbsolutePath());
			else
				Log.info("Loading native lib from '" + soDst + "'");
			soDst.setExecutable(true);
			soDst.deleteOnExit();
			tmp.delete();
			System.load(soDst.getAbsolutePath());
		} catch(Throwable t) {
			Log.severe(t);
		}
	}

	public EnetInterface(PcapIf device) throws IOException {
		this.device = device;
		this.addr   = device == null ? new byte[6] : device.getHardwareAddress();
	}

	public void open() throws IOException {
		int snaplen          = 64 * 1024;  
		int flags            = Pcap.MODE_PROMISCUOUS;  
		int timeout          = 1;   
		StringBuilder errbuf = new StringBuilder();
		if(device != null) {
			pcap                 = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
			Log.warning(errbuf.toString());
			if(pcap == null) throw new IOException("Could not open " + this);

			PcapBpfProgram filter = new PcapBpfProgram();
			String expression = "ether proto " + PROT_SYNC + " and ether dst " + toEnet(addr) + " or ether broadcast";
			if(pcap.compile(filter, expression, 1, 0) != Pcap.OK)
				throw new IOException(pcap.getErr() + ":" + expression);
			pcap.setFilter(filter);
		}
		RxThread rx = new RxThread();
		rx.setDaemon(true);
		rx.start();
	}

	private static final String HEXTAB = "0123456789ABCDEF";

	static final EnetInterface DUMMY = createDummy();

	private String toEnet(byte[] addr) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < 6; i++) {
			if(i != 0)
				result.append('-');
			result.append(HEXTAB.charAt((addr[i] >> 4) & 0xF));
			result.append(HEXTAB.charAt((addr[i]) & 0xF));
		}
		return result.toString();
	}

	private static EnetInterface createDummy() {
		try {
			return new EnetInterface(null);
		} catch(Throwable t) {
			Log.severe(t);
		}
		return null;
	}

	class RxThread extends Thread implements ByteBufferHandler<LinkedBlockingQueue<byte[]>> {		
		@Override
		public void run() {
			if(pcap != null) {
				while(!(close.get()))
					pcap.dispatch(-1, this, queue);
				pcap.close();
			}
			pcap = null;
			close.set(false);
		}

		@Override
		public void nextPacket(PcapHeader header, ByteBuffer buffer, LinkedBlockingQueue<byte[]> queue) {
			byte[] packet = new byte[buffer.limit()];
			buffer.get(packet);
			queue.offer(packet);
		}
	}

	public void close() throws InterruptedException {
		close.set(true);
		while(!(close.get())) {
			Thread.sleep(5);
		}
	}

	@SuppressWarnings("unused")
	public void send(byte[] packet) throws IOException {
		if(pcap != null) {
			ByteBuffer b = ByteBuffer.wrap(packet);
			if (pcap.sendPacket(b) != Pcap.OK) 
				Log.severe(pcap.getErr());  
		}
		if(SEND_DELAY > 0) {
			try {
				Thread.sleep(SEND_DELAY);
			} catch(Throwable t) {
				throw new IOException(t);
			}
		}
	}

	public byte[] recieve() throws InterruptedException {
		return queue.take();
	}

	@Override
	public String toString() {
		return device == null ? "dummy device" : (device.getDescription() + "/" + device.getName());
	}

	static EnetInterface[] interfaces() throws IOException {
		if(interfaces != null) return interfaces;

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  

		try {
			int r = Pcap.findAllDevs(alldevs, errbuf);  
			if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
				Log.severe("Can't read list of devices, error is " + errbuf.toString());  
				interfaces = new EnetInterface[0];
			}  else {
				interfaces = new EnetInterface[alldevs.size()];
				int i = 0;
				for(PcapIf pif : alldevs)
					interfaces[i++] = new EnetInterface(pif);
			}
		} catch(Throwable t) {
			interfaces = new EnetInterface[] {EnetInterface.DUMMY};
			Log.severe(t);
		}
		return interfaces;
	}

	public byte[] getAddr() {
		return addr;
	}
}
