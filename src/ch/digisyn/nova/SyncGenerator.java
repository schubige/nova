package ch.digisyn.nova;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.corebounce.util.Strings;

@SuppressWarnings("nls")
public class SyncGenerator implements IConstants {
	private static final boolean DBG = false;
	
	private final  AtomicInteger seqNum     = new AtomicInteger();
	private final  Thread        sync;
	private final  AtomicBoolean running    = new AtomicBoolean();
	private static final long    PLL_TIME   = 20L * 1000L * 1000L;
	private final  EnetInterface device;
	private final  AtomicBoolean disposed   = new AtomicBoolean();
	private        ISyncListener listener;
	private        long          last;
	private final  Dispatcher    disp;
	private static final boolean NOSLEEP    = System.getProperty("nosleep") != null;
	
	public SyncGenerator(EnetInterface device, Dispatcher disp) {
		this.device = device;
		this.disp   = disp;
		disp.setSyncGen(this);
		sync = new Thread() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				try {
					byte[] packet = new byte[ADDR_LEN + PROT_LEN + DATA_LEN];
					AddressUtils.BROADCAST(packet, 0);
					AddressUtils.SYNC(packet, 6);
					last = System.nanoTime();
					while(!(disposed.get())) {
						long now   = System.nanoTime();
						long ticks = ((now -last) + (PLL_TIME / 2)) / PLL_TIME;
						last = now;
						long next = now + (PLL_TIME - (now % PLL_TIME));
						if(!(NOSLEEP) && next - now > 25000L)
							Thread.sleep(10, 0);
						while(System.nanoTime() < next) {}
						synchronized(running) {
							if(running.get())
								PacketUtils.sync(packet, ADDR_LEN, seqNum.get());
							else
								PacketUtils.pll(packet, ADDR_LEN, seqNum.get());
							SyncGenerator.this.device.send(packet);
							if(DBG && ticks > 1)
								System.out.println("Sync miss");
							while(ticks-- > 0) {
								seqNum.incrementAndGet();
								if((seqNum.get() & 1) == 1 && listener != null && running.get())
									listener.sync(seqNum.get() >> 1);
							}
						}
					}
				} catch(Throwable t) {
					t.printStackTrace();
				}
				running.set(false);
			}
		};
		sync.setPriority(Thread.MAX_PRIORITY);
		sync.setDaemon(true);
		sync.start();
	}

	public void on() {
		
	}
	
	public void off() {
		
	}
	
	public void setListener(ISyncListener listener) {
		this.listener = listener;
	}
	
	public void dispose() {
		disp.setSyncGen(null);
		disposed.set(true);
	}

	void startSync() {
		System.out.println("SyncGen: start");
		try {
			while((seqNum.get() & 1) == 1)
				Thread.sleep(1);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		running.set(true);
	}

	void stopSync() {
		System.out.println("SyncGen: stop");
		try {
			while((seqNum.get() & 1) == 1)
				Thread.sleep(1);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		running.set(false);
	}

	public void handle(int cmd, byte[] status) throws IOException {
		switch(cmd) {
		case CMD_START:
			System.out.println("\nSTART " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			running.set(true);
			seqNum.set(0);
			PacketUtils.statusReply(status, ADDR_LEN, STAT_RUN, 511);
			device.send(status);
			break;
		case CMD_STOP:
			System.out.println("\nSTOP " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			running.set(false);
			seqNum.set(0);
			PacketUtils.statusReply(status, ADDR_LEN, STAT_STOP, 0);
			device.send(status);
			break;
		case CMD_STATUS:
			System.out.println("\nSTATUS " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			PacketUtils.statusReply(status, ADDR_LEN, running.get() ? STAT_RUN : STAT_STOP, seqNum.get());
			device.send(status);
			break;
		}
	}
}
