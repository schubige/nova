package ch.bluecc.nova;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.corebounce.util.Log;
import org.corebounce.util.Strings;
import org.jnetpcap.PcapException;

@SuppressWarnings("nls")
public class SyncGenerator implements IConstants {
	private static final boolean DBG = false;

	private static final long    PLL_TIME   = 20L * 1000L * 1000L;
	private static final boolean NOSLEEP    = true;
	
	private final  EnetInterface device;
	private final  Dispatcher    dispatcher;
	private final  AtomicInteger seqNum     = new AtomicInteger();
	private final  AtomicBoolean running    = new AtomicBoolean();
	private final  AtomicBoolean disposed   = new AtomicBoolean();
	private        ISyncListener listener;
	
	public SyncGenerator(EnetInterface device, Dispatcher dispatcher) {
		this.device = device;
		this.dispatcher = dispatcher;
		dispatcher.setSyncGen(this);
		Thread thread = new Thread(this::syncTask);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.setDaemon(true);
		thread.start();
	}

	public void on() {
	}
	
	public void off() {
	}
	
	public void setListener(ISyncListener listener) {
		this.listener = listener;
	}
	
	public void dispose() {
		dispatcher.setSyncGen(null);
		disposed.set(true);
	}

	void startSync() {
		Log.info("SyncGen: start");
		try {
			while((seqNum.get() & 1) == 1)
				Thread.sleep(1);
		} catch(Throwable t) {
			Log.severe(t);
		}
		running.set(true);
	}

	void stopSync() {
		Log.info("SyncGen: stop");
		try {
			while((seqNum.get() & 1) == 1)
				Thread.sleep(1);
		} catch(Throwable t) {
			Log.severe(t);
		}
		running.set(false);
	}

	public void handle(int cmd, byte[] status) throws IOException, PcapException {
		switch(cmd) {
		case CMD_START:
			Log.info("\nSTART " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			running.set(true);
			seqNum.set(0);
			PacketUtils.statusReply(status, ADDR_LEN, STAT_RUN, 511);
			device.send(status);
			break;
		case CMD_STOP:
			Log.info("\nSTOP " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			running.set(false);
			seqNum.set(0);
			PacketUtils.statusReply(status, ADDR_LEN, STAT_STOP, 0);
			device.send(status);
			break;
		case CMD_STATUS:
			Log.info("\nSTATUS " + running + " " + Strings.toHex((byte)(seqNum.get() >> 1)));
			PacketUtils.statusReply(status, ADDR_LEN, running.get() ? STAT_RUN : STAT_STOP, seqNum.get());
			device.send(status);
			break;
		}
	}
	
	@SuppressWarnings("unused")
	private void syncTask() {
		try {
			byte[] packet = new byte[ADDR_LEN + PROT_LEN + DATA_LEN];
			AddressUtils.BROADCAST(packet, 0);
			AddressUtils.SYNC(packet, 6);
			long last = System.nanoTime();
			while(!(disposed.get())) {
				long now   = System.nanoTime();
				long ticks = ((now - last) + (PLL_TIME / 2)) / PLL_TIME;
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
						Log.info("Sync miss: " + ticks);
					while(ticks-- > 0) {
						seqNum.incrementAndGet();
						if((seqNum.get() & 1) == 1 && listener != null && running.get())
							listener.sync(seqNum.get() >> 1);
					}
				}
			}
		} catch(Throwable t) {
			Log.severe(t);
		}
		running.set(false);
	}
}
