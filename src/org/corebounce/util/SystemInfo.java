package org.corebounce.util;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapException;

@SuppressWarnings("nls")
public class SystemInfo {
	/**
	 * Get a list with all mac addresses connected to a hardware device (independent of connected state) 
	 * on the system.
	 * 
	 * @return string of the form "MAC0;MAC1;MAC2..."
	 */
	public static String getMACAddresses() throws PcapException {
		StringBuilder result = new StringBuilder();
		for (var device : Pcap.findAllDevs()) {
			result.append(TextUtilities.toHex(device.hardwareAddress().get())).append(';');
		}
		return result.toString();
	}
}
