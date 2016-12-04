package ch.digisyn.nova;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corebounce.io.Utilities;
import org.jnetpcap.ByteBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;

public class EnetInterface implements IConstants {
	private static EnetInterface[]            interfaces;
	private final PcapIf                      device;
	private       Pcap                        pcap;
	private final byte[]                      addr;
	private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
	private final AtomicBoolean               close = new AtomicBoolean();
	private static final int                  SEND_DELAY = 1;

	static {
		/*
		try {
			File tmp = File.createTempFile("NOVAatHOME", "lck");
			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
				File dllDst = new File(tmp.getParentFile(), "jnetpcap.dll");
				Utilities.copy(EnetInterface.class.getResourceAsStream("/native/" + dllDst.getName()), new FileOutputStream(dllDst));
				dllDst.setExecutable(true);
				dllDst.deleteOnExit();
				System.load(dllDst.getAbsolutePath());				
			} else {
				File soDst  = new File(tmp.getParentFile(), "libjnetpcap.so");
				Utilities.copy(EnetInterface.class.getResourceAsStream("/native/" + soDst.getName()), new FileOutputStream(soDst));
				soDst.deleteOnExit();
				soDst.setExecutable(true);
				System.load(soDst.getAbsolutePath());				
			}
			tmp.delete();
		} catch(Throwable t) {
			t.printStackTrace();
		}
		*/
	}

	public EnetInterface(PcapIf device) throws IOException {
		this.device = device;
		this.addr   = device.getHardwareAddress();
	}

	public void open() throws IOException {
		int snaplen          = 64 * 1024;  
		int flags            = Pcap.MODE_PROMISCUOUS;  
		int timeout          = 1;   
		StringBuilder errbuf = new StringBuilder();
		pcap                 = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		System.out.println(errbuf);
		if(pcap == null) throw new IOException("Could not open " + this);

		PcapBpfProgram filter = new PcapBpfProgram();
		String expression = "ether proto " + PROT_SYNC + " and ether dst " + toEnet(addr) + " or ether broadcast";
		if(pcap.compile(filter, expression, 1, 0) != Pcap.OK)
			throw new IOException(pcap.getErr() + ":" + expression);
		pcap.setFilter(filter);

		RxThread rx = new RxThread();
		rx.setDaemon(true);
		rx.start();
	}

	private static final String HEXTAB = "0123456789ABCDEF";
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

	class RxThread extends Thread implements ByteBufferHandler<LinkedBlockingQueue<byte[]>> {		
		@Override
		public void run() {
			while(!(close.get()))
				pcap.dispatch(-1, this, queue);
			pcap.close();
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

	public void send(byte[] packet) throws IOException {
		if(pcap != null) {
			ByteBuffer b = ByteBuffer.wrap(packet);  
			if (pcap.sendPacket(b) != Pcap.OK) 
				System.err.println(pcap.getErr());  
		}
		try {
			Thread.sleep(SEND_DELAY);
		} catch(Throwable t) {
			throw new IOException(t);
		}
	}

	public byte[] recieve() throws InterruptedException {
		return queue.take();
	}

	@Override
	public String toString() {
		return device.getDescription() + "/" + device.getName();
	}

	static EnetInterface[] interfaces() throws IOException {
		if(interfaces != null) return interfaces;

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  

		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			interfaces = new EnetInterface[0];
		}  else {
			interfaces = new EnetInterface[alldevs.size()];
			int i = 0;
			for(PcapIf pif : alldevs)
				interfaces[i++] = new EnetInterface(pif);
		}
		return interfaces;
	}

	public byte[] getAddr() {
		return addr;
	}
}
