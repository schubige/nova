package org.corebounce.util;

public class IntegerUtilities {
	public static final int[] EMPTY_ARRAY = new int[0];

	public static final int lsbToU32(int val) {
		return bits0_7(val);
	}

	public static final int msbToU32(int val) {
		return bits8_15(val);
	}

	public static final int byteByteToU32(int msb, int lsb) {
		return ((msb & 0xFF) << 8) | (lsb & 0xFF); 
	}

	public static final int bits0_7(int val) {
		return val& 0xFF;
	}

	public static final int bits8_15(int val) {
		return (val >> 8) & 0xFF;
	}

	public static final int bits16_23(int val) {
		return (val >> 16) & 0xFF;
	}

	public static final int bits24_31(int val) {
		return (val >> 24) & 0xFF;
	}

	public static int le2beU32(int val) {
		return ((val & 0x000000ff) << 24) | ((val & 0x0000ff00) << 8) | ((val & 0x00ff0000) >> 8) | ((val & 0xff000000) >>> 24);
	}

	public static int bytesToInt(byte[] bytes) {
		int result = (bytes[0] & 0xFF) << 24;
		result |= (bytes[1] & 0xFF) << 16;
		result |= (bytes[2] & 0xFF) << 8;
		result |= (bytes[3] & 0xFF);
		return result;
	}

	public static int bytesToInt(int msb, int mbh, int mbl, int lsb) {
		int result = (msb & 0xFF) << 24;
		result |= (mbh & 0xFF) << 16;
		result |= (mbl & 0xFF) << 8;
		result |= (lsb & 0xFF);
		return result;
	}

	public static long bytesToLong(byte[] bytes, int off) {
		long result = ((long)(bytes[off + 0] & 0xFF)) << 56;
		result     |= ((long)(bytes[off + 1] & 0xFF)) << 48;
		result     |= ((long)(bytes[off + 2] & 0xFF)) << 40;
		result     |= ((long)(bytes[off + 3] & 0xFF)) << 32;
		result     |= (      bytes[off + 4] & 0xFF)   << 24;
		result     |= (      bytes[off + 5] & 0xFF)   << 16;
		result     |= (      bytes[off + 6] & 0xFF)   << 8;
		result     |= (      bytes[off + 7] & 0xFF);
		return result;
	}

	public static int compare(int i1, int i2) {
		return i1 > i2 ? 1 : i1 < i2 ? -1 : 0;
	}

}
