/*
 * Created on Feb 12, 2004
 */
package org.corebounce.net;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Implementation of iiuf.net, Utilities
 * 
 * (c) 2004 by corebounce association
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class AddressUtilities {
	private static final int[][] PRIVATE_ADDRS = { { 10 }, { 192, 168 },
			{ 172, 16 }, { 172, 17 }, { 172, 18 }, { 172, 19 }, { 172, 20 },
			{ 172, 21 }, { 172, 22 }, { 172, 23 }, { 172, 24 }, { 172, 25 },
			{ 172, 26 }, { 172, 27 }, { 172, 28 }, { 172, 29 }, { 172, 30 },
			{ 172, 31 }, };

	
	public static InetAddress getDefaultInterface() throws UnknownHostException, SocketException {
		boolean ipv6  = false;
		
	    InetAddress addr = getFirstNonLoopback(!ipv6);
	    if (addr != null) return addr;
	    return getLocalHost(!ipv6);
	}
	
	
	private static InetAddress getFirstNonLoopback(boolean ipv4only) throws SocketException {
		InetAddress[] addrs = AddressUtilities.getLocalAddresses(ipv4only);
		InetAddress result = null;
		for (int i = 0; i < addrs.length; i++)
			if (!addrs[i].isLoopbackAddress()) {
				result = addrs[i];
				if (isPrivate(result))
					break;
			}
		return result;
	}

	private static InetAddress getLocalHost(boolean ipv4only) throws UnknownHostException, SocketException {
		InetAddress result = InetAddress.getLocalHost();
		if (ipv4only && !(result instanceof Inet4Address)) {
			InetAddress[] addrs = AddressUtilities.getLocalAddresses(ipv4only);
			for (int i = 0; i < addrs.length; i++) {
				result = addrs[i];
				if (isPrivate(result))
					break;
			}
		}
		return result;
	}

	private static InetAddress[] getLocalAddresses(boolean ipv4only) throws SocketException {
		ArrayList<InetAddress> result = new ArrayList<>();
		for (Enumeration<NetworkInterface> e = NetworkInterface
				.getNetworkInterfaces(); e.hasMoreElements();) {
			NetworkInterface nif = e.nextElement();
			for (Enumeration<InetAddress> en = nif.getInetAddresses(); en
					.hasMoreElements();) {
				InetAddress addr = en.nextElement();
				if (ipv4only && !(addr instanceof Inet4Address))
					continue;
				result.add(addr);
			}
		}
		return result.toArray(new InetAddress[result.size()]);
	}

	private static boolean isPrivate(InetAddress addr) {
		byte[] addrb = addr.getAddress();
		for (int i = PRIVATE_ADDRS.length; --i >= 0;) {
			boolean valid = true;
			for (int j = 0; j < PRIVATE_ADDRS[i].length; j++)
				if (PRIVATE_ADDRS[i][j] != (addrb[j] & 0xFF)) {
					valid = false;
					break;
				}
			if (valid)
				return true;
		}
		return false;
	}

	public static String URLEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	public static String URLDecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}
}
