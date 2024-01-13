/* 
 * UUIDGen.java
 * 
 * Created on 09.08.2003.
 *
 * eaio: UUID - an implementation of the UUID specification
 * Copyright (c) 2003-2007 Johann Burkard (jb@eaio.com) http://eaio.com.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package org.corebounce.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.corebounce.collections.CanonicalSynchronizedSet;
import org.corebounce.net.NetworkUtilities;

@SuppressWarnings("nls")
public final class UUIDGen {
	private static final CanonicalSynchronizedSet<UUID> intern     = new CanonicalSynchronizedSet<>();
	private static final Map<String, UUID>              chars2uuid = new ConcurrentHashMap<>(); 

	public static final UUID INVALID_UUID = newUUID(0, 0);

	/**
	 * No instances needed.
	 */
	private UUIDGen() {}

	/**
	 * The last time value. Used to remove duplicate UUIDs.
	 */
	private static long lastTime = Long.MIN_VALUE;

	/**
	 * The current clock and node value.
	 */
	private static long clockSeqAndNode = 0x8000000000000000L;

	static {
		try {
			byte[] local = NetworkUtilities.getMacAddress()[0];
			if(local == null) {
				for(InterfaceAddress addr : NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses()) {
					if(!isPrivate(addr.getAddress()))
						local = addr.getAddress().getAddress();
				}
			} else {
				int len = local.length;
				if(local.length >= 6) {
					clockSeqAndNode |= local[--len]               & 0xFF;
					clockSeqAndNode |= (local[--len] << 8)        & 0xFF00;
					clockSeqAndNode |= (local[--len] << 16)       & 0xFF0000;
					clockSeqAndNode |= (local[--len] << 24)       & 0xFF000000L;
					clockSeqAndNode |= ((long)local[--len] << 32) & 0xFF00000000L;
					clockSeqAndNode |= ((long)local[--len] << 48) & 0xFF0000000000L;
				} else if(local.length >= 4) {
					clockSeqAndNode |= local[--len]         & 0xFF;
					clockSeqAndNode |= (local[--len] << 8)  & 0xFF00;
					clockSeqAndNode |= (local[--len] << 16) & 0xFF0000;
					clockSeqAndNode |= (local[--len] << 24) & 0xFF000000L;
				} else {
					clockSeqAndNode |= (long) (Math.random() * 0x7FFFFFFFFFFFL);				
				}
			}
		} catch (Throwable ex) {
			// Skip the clock sequence generation process and use random instead.
			clockSeqAndNode |= (long) (Math.random() * 0x7FFFFFFF);
		}

		clockSeqAndNode |= (long) (Math.random() * 0x3FFF) << 48;
	}

	private static int[][] PRIVATE_ADDRS;

	private synchronized static void initPrivateAddrs() {
		if(PRIVATE_ADDRS == null)
			PRIVATE_ADDRS = new int[][] {
				{10},
				{192, 168},
				{172, 16},
				{172, 17},
				{172, 18},
				{172, 19},
				{172, 20},
				{172, 21},
				{172, 22},
				{172, 23},
				{172, 24},
				{172, 25},
				{172, 26},
				{172, 27},
				{172, 28},
				{172, 29},
				{172, 30},
				{172, 31},
		};
	}

	public final static boolean isPrivate(InetAddress addr) {
		initPrivateAddrs();

		byte[] addrb = addr.getAddress();
		for(int i = PRIVATE_ADDRS.length; --i >= 0; ) {
			boolean valid = true;
			for(int j = 0; j < PRIVATE_ADDRS[i].length; j++)
				if(PRIVATE_ADDRS[i][j] != (addrb[j] & 0xFF)) {
					valid = false;
					break;
				}
			if(valid) return true;
		}
		return false;
	}

	/**
	 * Generates a new time field. Each time field is unique and larger than the
	 * previously generated time field.
	 * 
	 * @return a new time value
	 * @see UUID#getTime()
	 */
	public static synchronized long newTime() {

		long time;

		// UTC time

		long timeMillis = System.nanoTime() + 0x01B21DD213814000L;

		if (timeMillis > lastTime) {
			lastTime = timeMillis;
		}
		else {
			timeMillis = ++lastTime;
		}

		// time low

		time = timeMillis << 32;

		// time mid

		time |= (timeMillis & 0xFFFF00000000L) >> 16;

		// time hi and version

		time |= 0x1000 | ((timeMillis >> 48) & 0x0FFF); // version 1

		return time;

	}

	public static UUID newUUID() {
		return newUUID(newTime(), clockSeqAndNode);
	}

	public static UUID createUUIDfromInts(int[] uuid, int off, boolean treatZeroAsNull) {
		long msb = ((long)uuid[off + 0]) << 32 | (uuid[off + 1] & 0xFFFFFFFFL);
		long lsb = ((long)uuid[off + 2]) << 32 | (uuid[off + 3] & 0xFFFFFFFFL);
		if(treatZeroAsNull)
			return msb == 0L && lsb == 0L ? null : newUUID(msb, lsb); 
		return newUUID(msb, lsb);
	}

	public static UUID[] createUUIDsfromInts(int[] uuids, boolean treatZeroAsNull) {
		UUID[] result = new UUID[uuids.length / 4];
		for(int i = 0; i < uuids.length; i +=4)
			result[i >> 2] = createUUIDfromInts(uuids, i, treatZeroAsNull);
		return result;
	}

	public static UUID createUUIDfromInts(int msb0, int msb1, int lsb2, int lsb3, boolean treatZeroAsNull) {
		long msb = ((long)msb0) << 32 | (msb1 & 0xFFFFFFFFL);
		long lsb = ((long)lsb2) << 32 | (lsb3 & 0xFFFFFFFFL);
		if(treatZeroAsNull)
			return msb == 0L && lsb == 0L ? null : newUUID(msb, lsb); 
		return newUUID(msb, lsb);
	}

	public static UUID createUUIDfromBytes(byte[] uuid) {
		long msb = IntegerUtilities.bytesToLong(uuid, 0);
		long lsb = IntegerUtilities.bytesToLong(uuid, 4);
		return newUUID(msb, lsb);
	}

	public static UUID newUUID(long mostSigBits, long leastSigBits) {
		return intern(new UUID(mostSigBits, leastSigBits));
	}

	public static byte[] asBytes(UUID uuid) {
		byte[] result = new byte[16];
		result[0x0] = (byte)(uuid.getMostSignificantBits() >> 56);
		result[0x1] = (byte)(uuid.getMostSignificantBits() >> 48);
		result[0x2] = (byte)(uuid.getMostSignificantBits() >> 40);
		result[0x3] = (byte)(uuid.getMostSignificantBits() >> 32);
		result[0x4] = (byte)(uuid.getMostSignificantBits() >> 24);
		result[0x5] = (byte)(uuid.getMostSignificantBits() >> 16);
		result[0x6] = (byte)(uuid.getMostSignificantBits() >> 8);
		result[0x7] = (byte)(uuid.getMostSignificantBits());

		result[0x8] = (byte)(uuid.getLeastSignificantBits() >> 56);
		result[0x9] = (byte)(uuid.getLeastSignificantBits() >> 48);
		result[0xA] = (byte)(uuid.getLeastSignificantBits() >> 40);
		result[0xB] = (byte)(uuid.getLeastSignificantBits() >> 32);
		result[0xC] = (byte)(uuid.getLeastSignificantBits() >> 24);
		result[0xD] = (byte)(uuid.getLeastSignificantBits() >> 16);
		result[0xE] = (byte)(uuid.getLeastSignificantBits() >> 8);
		result[0xF] = (byte)(uuid.getLeastSignificantBits());

		return result;
	}


	public static int[] asInts(UUID uuid, int[] ints, int off) {
		if(uuid != null) {
			ints[off + 0] = (int)(uuid.getMostSignificantBits() >> 32);
			ints[off + 1] = (int)uuid.getMostSignificantBits();
			ints[off + 2] = (int)(uuid.getLeastSignificantBits() >> 32); 
			ints[off + 3] = (int)uuid.getLeastSignificantBits();
		} else {
			ints[off + 0] = 0;
			ints[off + 1] = 0;
			ints[off + 2] = 0; 
			ints[off + 3] = 0;
		}
		return ints;
	}


	public static int[] asInts(UUID ... uuids) {
		int[] result = new int[uuids.length * 4];
		for(int i = 0; i < uuids.length; i++)
			asInts(uuids[i], result, i * 4);
		return result;
	}

	private static UUID intern(UUID uuid) {
		return intern.intern(uuid);
	}

	public static String asBinaryString(UUID uuid) {
		char[] result = new char[22];

		int idx = 0;
		for(int i = 60; i >= 0; i -= 6) {
			result[idx++] = (char)(((uuid.getMostSignificantBits()  >> i) & 0x3F) + 33);
			result[idx++] = (char)(((uuid.getLeastSignificantBits() >> i) & 0x3F) + 33);
		}

		return new String(result);
	}

	public static UUID createUUIDfromChars(String chars) {
		UUID result = chars2uuid.get(chars);
		if(result == null) {
			if(chars.length() == 8) {
				long msb = 
					(  long)(chars.charAt(0) & 0xFFFF) << 48 
					| (long)(chars.charAt(1) & 0xFFFF) << 32 
					| (long)(chars.charAt(2) & 0xFFFF) << 16 
					|       (chars.charAt(3) & 0xFFFF); 
				long lsb = 
					(  long)(chars.charAt(4) & 0xFFFF) << 48 
					| (long)(chars.charAt(5) & 0xFFFF) << 32 
					| (long)(chars.charAt(6) & 0xFFFF) << 16 
					|       (chars.charAt(7) & 0xFFFF); 
				return newUUID(msb, lsb);
			} else if(chars.length() == 22) {
				long msb = 0;
				long lsb = 0;
				int  idx = 0;

				for(int i = 0; i < 11; i++) {
					msb <<= 6;
					msb |= (chars.charAt(idx++) - 33) & 0x3F;
					lsb <<= 6;
					lsb |= (chars.charAt(idx++) - 33) & 0x3F;
				}

				result = newUUID(msb, lsb);

				chars2uuid.put(chars, result);
			} else
				throw new IllegalArgumentException("Can't decode:" + chars);
		}
		return result;
	}
}
