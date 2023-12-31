package ch.bluecc.nova;

import org.corebounce.util.Log;

public class Dispatcher extends Thread implements IConstants {
	final EnetInterface device;
	final NOVAConfig    config;
	SyncGenerator       sync;
	
	public Dispatcher(EnetInterface device, NOVAConfig config) {
		this.device = device;
		this.config = config;
		setPriority(Thread.MIN_PRIORITY);
		start();
	}
	
	public void setSyncGen(SyncGenerator sync) {
		this.sync = sync;
	}
	
	@Override
	public void run() {
		byte[] status = new byte[ADDR_LEN + PROT_LEN + DATA_LEN];
		AddressUtils.SYNC(status, 6);
		for(;;) {
			try {
				byte[] packet = device.recieve();
				if(!(PacketUtils.isNOVAEnet(packet)))   continue;
				if(AddressUtils.isDstBroadcast(packet)) continue;
				synchronized(this) {
					System.arraycopy(packet, 6, status, 0, 6);
					switch(packet[ADDR_LEN + PROT_LEN + 2]) {
					case CMD_START:
						if(sync != null)
						sync.handle(CMD_START, status);
						break;
					case CMD_STOP:
						if(sync != null)
						sync.handle(CMD_STOP, status);
						break;
					case CMD_STATUS:
						if(packet[20] == (byte)NOVA_IP_0) {
							if(config != null)
								config.setStatus(new DMUXStatus(packet, ADDR_LEN + PROT_LEN));
						} else if(sync != null)
							sync.handle(CMD_STATUS, status);
						break;
					}
				}
			} catch(Throwable t) {
				Log.severe(t);
			}
		}
	}
}
