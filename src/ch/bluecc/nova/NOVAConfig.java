package ch.bluecc.nova;

import java.util.Properties;

import org.corebounce.util.Log;

@SuppressWarnings("nls")
public final class NOVAConfig {
	private final int[][]      modules;
	private final int[]        modulesFlat;
	private final DMUXStatus[] stat = new DMUXStatus[101];
	private final int          dimI;
	private final int          dimJ;
	private final boolean      flipK;
	private final int[]        frameOffsets = new int[256];

	
	public NOVAConfig(Properties properties) {
		int moduleDimI = 0;
		int moduleDimJ = 0;
		int[][] config = new int[100][100];
		for (int i = 0; i < config.length; i++) {
			for (int j = 0; j < config[i].length; j++) {
				String addr = properties.getProperty(NOVAControl.PROPERTY_KEY_ADDRESS + i + "_" + j);
				if (addr != null) {
					config[i][j] = Integer.parseInt(addr.trim());
					moduleDimI = Math.max(moduleDimI, i + 1);
					moduleDimJ = Math.max(moduleDimJ, j + 1);
				}
			}
		}
		// if no addresses are given, default to addr_0_0 = 1
		if (moduleDimI == 0 || moduleDimJ == 0) {
			moduleDimI = 1;
			moduleDimJ = 1;
			config[0][0] = 1;
		}

		modules = new int[moduleDimI][moduleDimJ];
		for (int i = 0; i < moduleDimI; i++)
			for (int j = 0; j < moduleDimJ; j++)
				modules[i][j] = config[i][j];

		dimI = modules.length * moduleDimI();

		int[] tmp = new int[100];
		int count = 0;
		int maxJ = 0;
		for (int[] row : modules)
			maxJ = Math.max(maxJ, row.length);

		for (int j = 0; j < maxJ; j++) {
			for (int i = 0; i < modules.length; i++) {
				int m = modules[i][j];
				if (m > 0 && m < 101) {
					tmp[count++] = m;
					frameOffsets[m] = calcFrameOffset(m);
				}
			}
		}

		this.dimJ = maxJ * moduleDimJ();

		this.modulesFlat = new int[count];
		System.arraycopy(tmp, 0, this.modulesFlat, 0, count);

		if (this.modulesFlat.length == 0)
			throw new IllegalArgumentException("At least one module must be configured");

		flipK = properties.getProperty(NOVAControl.PROPERTY_KEY_FLIP, "f").toLowerCase().startsWith("t");

		Log.info("Configured dimI=" + dimI + ", dimJ=" + dimJ + ", dimK=" + moduleDimK() + (flipK ? " (upside down)" : ""));
	}

	private int calcFrameOffset(int m) {
        for(int i = 0; i < this.modules.length ; i++) {
            for(int j = 0; j < this.modules[i].length ; j++) {
                 if (this.modules[i][j] == m)
                    return 3 * dimK() * (i * moduleDimI()  + j * moduleDimJ() * dimI());
            }
         }

         throw new IllegalArgumentException("Invalid module number for getFrameOffset()");
	}
	
	public int numOperational() {
		int result = 0;
		for(int m : modulesFlat)
			if(stat[m] != null && stat[m].isOperational())
				result++;
		return result;
	}
	
	public boolean isOperational() {
		return numOperational() > modulesFlat.length / 2;
	}

	public int[] getModules() {
		return modulesFlat;
	}

	public int dimI() {
		return dimI;
	}

	public int dimJ() {
		return dimJ;
	}
	
	public int dimK() {
		return moduleDimK();
	}

	public void setStatus(DMUXStatus status) {
		stat[status.ipAddr & 0xFF] = status;
	}

	public int moduleDimI() {
		return 5;
	}

	public int moduleDimJ() {
		return 5;
	}

	public int moduleDimK() {
		return 10;
	}

	public int numModules() {
		return modulesFlat.length;
	}

	public int getFrameOffset(int m) {
		return frameOffsets[m];
	}

	public boolean flipK() {
		return flipK;
	}
}
