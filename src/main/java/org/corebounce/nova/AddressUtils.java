package org.corebounce.nova;

public class AddressUtils implements IConstants {

	public static boolean isDstBroadcast(byte[] packet) {
		return packet[0] == -1 && packet[1] == -1 && packet[2] == -1 && packet[3] == -1 && packet[4] == -1 && packet[5] == -1;
 	}

	public static void MMUX(byte[] packet, int off, int module) {
		packet[off++] = DMUX_ADR0;
		packet[off++] = DMUX_ADR1;
		packet[off++] = DMUX_ADR2;
		packet[off++] = DMUX_ADR3;
		packet[off++] = DMUX_ADR4;
		packet[off++] = (byte)module;
	}

	public static void SYNC(byte[] packet, int off) {
		packet[off++] = SYNC_ADR0;
		packet[off++] = SYNC_ADR1;
		packet[off++] = SYNC_ADR2;
		packet[off++] = SYNC_ADR3;
		packet[off++] = SYNC_ADR4;
		packet[off++] = SYNC_ADR5;
	}

	public static void BROADCAST(byte[] packet, int off) {
		packet[off++] = (byte)0xFF;
		packet[off++] = (byte)0xFF;
		packet[off++] = (byte)0xFF;
		packet[off++] = (byte)0xFF;
		packet[off++] = (byte)0xFF;
		packet[off++] = (byte)0xFF;
	}

	public static void SELF(EnetInterface eif, byte[] packet, int off) {
		System.arraycopy(eif.getAddr(), 0, packet, off, 6);
	}
}
