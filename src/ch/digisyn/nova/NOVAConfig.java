package ch.digisyn.nova;

@SuppressWarnings("nls")
public final class NOVAConfig {
	private final int[][]      modules;
	private final int[]        modulesFlat;
	private final DMUXStatus[] stat = new DMUXStatus[101];
	private final int          dimI;
	private final int          dimJ;
	private final boolean      flipK;
	
	public NOVAConfig(int[][] modules, boolean flipK) {
		this.modules  = modules;
		this.dimJ     = modules.length * moduleDimJ();
		this.flipK    = flipK;
		int[] tmp     = new int[100];
		int   count   = 0;
		int   maxI    = 0;
		for(int[] row : modules) {
			maxI = Math.max(maxI, row.length);
			for(int m : row)
				if(m > 0 && m < 101)
					tmp[count++] = m;
		}
		this.dimI        = maxI * moduleDimI();
		this.modulesFlat = new int[count];
		System.arraycopy(tmp, 0, this.modulesFlat, 0, count);
		if(this.modulesFlat.length == 0) throw new IllegalArgumentException("At least one module must be configured");
		System.out.println("Configured " + dimI + "x" + dimJ + (flipK ? " flipped" : ""));
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
        for(int i = 0; i < this.modules.length ; i++) {
           for(int j = 0; j < this.modules[i].length ; j++) {
                if (this.modules[i][j] == m)
                   return 3 * dimK() * (i * moduleDimI()  + j * moduleDimJ() * dimI());
           }
        }

        throw new IllegalArgumentException("Invalid module number for getFrameOffset()");
	}

	public boolean flipK() {
		return flipK;
	}
}
