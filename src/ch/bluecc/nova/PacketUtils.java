package ch.bluecc.nova;

import java.util.Arrays;

public class PacketUtils implements IConstants {
	static final int IP_VERSION          = 0x40;
	
	static final int NetworkControl      = 0xE0;
	static final int InternetworkControl = 0xC0;
	static final int CRITIC_ECP          = 0xA0;
	static final int FlashOverride       = 0x80;
	static final int Flash               = 0x60;
	static final int Immediate           = 0x40; 
	static final int Priority            = 0x20;
	static final int Routine             = 0x00;

	static final int LOW_DELAY           = 0x10; 
	static final int HIGH_THROUGHPUT     = 0x80;
	static final int HIGH_RELIABILITY    = 0x40;

	public static void UDP(byte[] packet, int off, byte[] src, int srcPort, byte[] dst, int dstPort, int payloadLength) {
		packet[off++] = (byte)(PROT_IP >> 8);
		packet[off++] = (byte)PROT_IP;
		
		// ip
		int header = off;
		packet[off++] =  IP_VERSION | 0x05;
		packet[off++] = (byte)CRITIC_ECP | LOW_DELAY | HIGH_RELIABILITY;
		packet[off++] = (byte)((payloadLength + 28) >> 8);
		packet[off++] = (byte)(payloadLength + 28);
		packet[off++] = 0x32;
		packet[off++] = 0x1c;
		packet[off++] = 0x40;
		packet[off++] = 0x00;
		packet[off++] = (byte)0x80;
		packet[off++] = 0x11;
		packet[off++] = 0;
		packet[off++] = 0;
		System.arraycopy(src, 0, packet, off, 4); off += 4;
		System.arraycopy(dst, 0, packet, off, 4); off += 4;
		
		long csum = ipcsum(packet, header, 20);
		packet[header + 10] = (byte)(csum >> 8);
		packet[header + 11] = (byte)csum;

		// udp
		
		packet[off++] = (byte)(srcPort >> 8);
		packet[off++] = (byte)srcPort;
		packet[off++] = (byte)(dstPort >> 8);
		packet[off++] = (byte)dstPort;
		packet[off++] = (byte)((payloadLength + 8) >> 8);
		packet[off++] = (byte)(payloadLength + 8);
		packet[off++] = 0; //cs0
		packet[off++] = 0; //cs1
	}

	private static long ipcsum(byte[] buf, int off, int length) {
		int i = 0;

		long sum = 0;
		long data;

		while (length > 1) {
			data = (((buf[off + i] << 8) & 0xFF00) | ((buf[off + i + 1]) & 0xFF));
			sum += data;
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
			i += 2;
			length -= 2;
		}

		if (length > 0) {
			sum += (buf[off + i] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}

		sum = ~sum;
		sum = sum & 0xFFFF;
		return sum;	
	}

	public static void novaEnetPacket(byte[] packet, int off, byte command, byte status, int syncNum) {
		packet[off++] = (byte)(PROT_SYNC >> 8);
		packet[off++] = (byte)PROT_SYNC;
		packet[off++] = (byte)(DATA_LEN >> 8);
		packet[off++] = (byte)DATA_LEN;
		packet[off++] = command;
		packet[off++] = status;
		packet[off++] = (byte)(syncNum >> 1);
		packet[off++] = (byte)(syncNum & 1);
		Arrays.fill(packet, off, off + 40, (byte)0);
	}

	public static void pll(byte[] packet, int off, int syncNum) {
		novaEnetPacket(packet, off, CMD_PLL, STAT_STOP, syncNum);
	}

	public static void sync(byte[] packet, int off, int syncNum) {
		novaEnetPacket(packet, off, CMD_SYNC, STAT_RUN, syncNum);
	}

	public static void status(byte[] packet, int off, int syncNum) {
		novaEnetPacket(packet, off, CMD_STATUS, STAT_NULL, 0);
	}

	public static void start(byte[] packet, int off) {
		novaEnetPacket(packet, off, CMD_START, STAT_NULL, 0);
	}

	public static void stop(byte[] packet, int off) {
		novaEnetPacket(packet, off, CMD_STOP, STAT_NULL, 0);
	}

	public static void statusReply(byte[] packet, int off, byte status, int syncNum) {
		novaEnetPacket(packet, off, CMD_STATUS, status, syncNum);
	}

	public static void chainData(byte[] packet, int off, byte command, int seqNum, int chain, int[] pixelData, int pixelDataOff) {
		packet[off++] = (byte) 0xC0;
		packet[off++] = (byte) command;
		packet[off++] = (byte) seqNum;
		packet[off++] = (byte) chain;

		for(int i = 0; i < 10; i++) {

			final int pdata = pixelData[pixelDataOff + i];
			packet[off++] = (byte)(pdata >> 24);
			packet[off++] = (byte)(pdata >> 16);
			packet[off++] = (byte)(pdata >> 8);
			packet[off++] = (byte)(pdata);
		}
	}

	public static void novaUDP(byte[] packet, int off, byte command, int frameNum, int[] pixelData) {
		for(int c = 0; c < 25; c++)
			chainData(packet, off + c * 44, command, frameNum, c, pixelData, c * 10);
	}

	private static final int[] BLACK = new int[25 * 10];

	public static void reset(byte[] packet, int off) {
		novaUDP(packet, off, CMD_RESET, 0, BLACK);
	}

	public static void autoid(byte[] packet, int off) {
		novaUDP(packet, off, CMD_AUTOID, 0, BLACK);
	}

	private static int[] OP_MODE_BUF = new int [25 * 10];
	public static void opMode(byte[] packet, int off, int mode) {
		Arrays.fill(OP_MODE_BUF, mode);
		novaUDP(packet, off, CMD_OPMODE, 0, OP_MODE_BUF);
	}

	public static void rgb(byte[] packet, int off, int frameNo, int[] pixelData) {
		novaUDP(packet, off, CMD_RGB, frameNo, pixelData);
	}

	public static boolean isNOVAEnet(byte[] packet) {
		return packet[12] == (byte)(PROT_SYNC >> 8) && packet[13] == (byte)PROT_SYNC;
	}
}
