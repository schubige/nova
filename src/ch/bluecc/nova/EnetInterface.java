package ch.bluecc.nova;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corebounce.util.Log;
import org.jnetpcap.BpFilter;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapException;
import org.jnetpcap.PcapHandler.OfArray;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;

@SuppressWarnings("nls")
public class EnetInterface implements IConstants {
	private final PcapIf                      device;
	private       Pcap                        pcap;
	private final byte[]                      addr;
	private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
	private final AtomicBoolean               close = new AtomicBoolean();
	private static final int                  SEND_DELAY = 0;

	public EnetInterface(PcapIf device) throws IOException {
		this.device = device;
		this.addr   = device.hardwareAddress().orElse(new byte[6]);
	}

	public void open() throws IOException, PcapException {
		int snaplen          = 64 * 1024;  
		int timeout          = 1;   
		if(device != null) {
			pcap = Pcap.openLive(device, snaplen, true, timeout, TimeUnit.MILLISECONDS);
			if (pcap == null)
				throw new IOException("Could not open " + this);

			String expression = "ether proto " + PROT_SYNC + " and ether dst " + toEnet(addr) + " or ether broadcast";
			BpFilter filter = pcap.compile(expression, false);
			pcap.setFilter(filter);
		}
		new RxThread();
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

	private class RxThread implements OfArray<LinkedBlockingQueue<byte[]>> {
		RxThread() {
			Thread t = new Thread(this::dispatch);
			t.setDaemon(true);
			t.start();
		}

		void dispatch() {
			if (pcap != null) {
				while (!(close.get())) {
					try {
						pcap.dispatch(1, this, queue);
					} catch (PcapException e) {
						Log.severe(e);
					}
				}
				pcap.close();
			}
			pcap = null;
			close.set(false);
		}

		@Override
		public void handleArray(LinkedBlockingQueue<byte[]> user, PcapHeader header, byte[] packet) {
			queue.offer(packet.clone());
		}		
	}

	public void close() throws InterruptedException {
		close.set(true);
		while(!(close.get())) {
			Thread.sleep(5);
		}
	}

	@SuppressWarnings("unused")
	public void send(byte[] packet) throws IOException, PcapException {
		if(pcap != null) {
			ByteBuffer b = ByteBuffer.wrap(packet);
			pcap.sendPacket(packet);
		}
		if(SEND_DELAY > 0) {
			try {
				Thread.sleep(SEND_DELAY);
			} catch(Throwable t) {
				throw new IOException(t);
			}
		}
	}

	public byte[] receive() throws InterruptedException {
		return queue.take();
	}

	public String getName() {
		return device.name();
	}
	
	public byte[] getAddr() {
		return addr;
	}

	@Override
	public String toString() {
		return getName();
	}

	static EnetInterface getInterface(String name) throws IOException, PcapException {
		for (var device : Pcap.findAllDevs()) {
			if (device.name().equals(name))
				return new EnetInterface(device);
		}
		throw new IllegalArgumentException("No such interface: " + name);
	}
}
