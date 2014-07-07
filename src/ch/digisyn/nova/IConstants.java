package ch.digisyn.nova;

public interface IConstants {
	int  UDP_PAYLOAD_OFF = 42;
	
	int NOVA_IP_0 = 192;
	int NOVA_IP_1 = 168;
	int NOVA_IP_2 = 1;
	
	String NOVA_IP_PREFIX_STR = NOVA_IP_0 + "." + NOVA_IP_1 + "." + NOVA_IP_2 + ".";
	
	byte SYNC_ADR0   = (byte)0x00;
	byte SYNC_ADR1   = (byte)0x20;
	byte SYNC_ADR2   = (byte)0xE3;
	byte SYNC_ADR3   = (byte)0x10;
	byte SYNC_ADR4   = (byte)0x01;
	byte SYNC_ADR5   = (byte)0x00;
	
	byte DMUX_ADR0   = (byte)0x00;
	byte DMUX_ADR1   = (byte)0x20;
	byte DMUX_ADR2   = (byte)0xE3;
	byte DMUX_ADR3   = (byte)0x10;
	byte DMUX_ADR4   = (byte)0x00;
	
	int  PROT_IP     = 0x800;
	int  PROT_SYNC   = 0x810;
	byte ADDR_LEN    = 12;
	byte PROT_LEN    = 2;
	int  DATA_LEN    = 46;
	
	byte CMD_SYNC     = 0x00;
	byte CMD_PLL      = 0x01;
	byte CMD_START    = 0x02;
	byte CMD_STOP     = 0x03;
	byte CMD_STATUS   = 0x04;
	
	byte CMD_RESET      = 0x00;
	byte CMD_RGB        = 0x02;
	byte CMD_DOT_CORR   = 0x04;
	byte CMD_COLOR_CORR = 0x08;
	byte CMD_BRIGHTNESS = 0x10;
	byte CMD_OPMODE     = 0x40;
	byte CMD_AUTOID     = 0x70;
	
	byte STAT_NULL    = 0x00;
	byte STAT_STOP    = 0x00;
	byte STAT_RUN     = 0x01;
	
	int FSS_POWER_OK3             = 0x80;
	int FSS_POWER_OK2             = 0x40;
	int FSS_POWER_OK1             = 0x20;
	int FSS_POWER_ERASE_TIMEOUT   = 0x10;
	int FSS_POWER_PROGRAM_TIMEOUT = 0x08;
	int FSS_POWER_ERASE_PENDING   = 0x04;
	int FSS_POWER_PROGRAM_PENDING = 0x02;
	int FSS_POWER_RESTART_PENDING = 0x01;
}
