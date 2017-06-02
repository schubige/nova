package org.corebounce.util;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

@SuppressWarnings("nls")
public class SystemInfo {
	/**
	 * Get a list with all mac addresses connected to a hardware device (independent of connected state) 
	 * on the system.
	 * 
	 * @return string of the form "MAC0;MAC1;MAC2..."
	 */
	public static String getMACAddresses() {
		StringBuilder result = new StringBuilder();
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  

		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			return "";
		} 
		try {
		for(PcapIf pif : alldevs)
			result.append(TextUtilities.toHex(pif.getHardwareAddress())).append(';');
		} catch(Throwable t) {}
		return result.toString();
	}


}
