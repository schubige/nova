package org.corebounce.net;

import org.corebounce.util.SystemInfo;

public class NetworkUtilities {
	private static byte[][] macAddresses;

	public synchronized final static byte[][] getMacAddress() {
		if(macAddresses == null) {
			String[] macs = SystemInfo.getMACAddresses().split("[:;,]");
			macAddresses  = new byte[macs.length][];
			int idx = 0;
			for(String mac : macs) {
				macAddresses[idx] = new byte[mac.length() / 2];
				for(int i = 0; i < mac.length(); i += 2)
					macAddresses[idx][i / 2] = (byte) Integer.parseInt(mac.substring(i, i + 2), 16);
				idx++;
			}
		}
		return macAddresses;
	}
}
