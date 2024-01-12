package ch.bluecc.nova.content;

import org.corebounce.util.Log;

import ch.bluecc.nova.Content;

@SuppressWarnings("nls")
public class CellularAutomaton extends Content {
	float[] frame = new float[0];
	public double startTime;
	public int nx;
	public int ny;
	public int nz;
	int[] rule = { 0, 1, 0, 1, 1, 1, 1, 0, 0, 0 };
	int[][][] old_matrix; // , new_matrix;
	int ix = 0;

	public CellularAutomaton(int dimI, int dimJ, int dimK, int numFrames) {
		super("CA_Alexander", dimI, dimJ, dimK, numFrames);
		this.nx = dimI;
		this.ny = dimJ;
		this.nz = dimK;

		this.old_matrix = new int[this.nx][this.ny][this.nz];
		for (int x = 0; x < this.nx; x++) {
			for (int y = 0; y < this.ny; y++) {
				for (int z = 0; z < this.nz; z++) {
					old_matrix[x][y][z] = Math.random() > 0.5 ? 1 : 0;
//					old_matrix[x][y][z] = ((z+y) % 10 == 0) ? 1 : 0;
					if (x > 0)
						old_matrix[x][y][z] = 0;
				}
			}
		}
		// this.new_matrix = new int[this.nx][this.ny][this.nz];
		// for (int x = 0; x < this.ny; x++) {
		// for (int y = 0; y < this.ny; y++) {
		// for (int z = 0; z < this.nz; z++) {
		// new_matrix[x][y][z] = 0;
		// }
		// }
		// }
	}

	@Override
	public void start() {
		startTime = -1;
		super.start();
	}

	@Override
	public void stop() {
		Log.info("Stopping " + this);
	}

	int count = 0;
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		if (startTime < 0)
			startTime = timeInSec;
		timeInSec -= startTime;

		// here it goes
		for (int x = 0; x < this.nx; x++) {
			for (int y = 0; y < this.ny; y++) {
				for (int z = 0; z < this.nz; z++) {
					int n = this.old_matrix[x][y][z];
					setVoxel(rgbFrame, x, y, z, n, n, n);
				}
			}
		}

		int delta = (int) Math.floor(timeInSec);
		if (delta/2!=count) {
//		if (delta % 3 == 2) {
			System.out.println("next step now");
			step(this.ix);
			this.ix = (this.ix + 1) % this.nx;
			count = delta/2;
		}

//		for (int i = 0; i < rgbFrame.length; i++) {
//			float d = rgbFrame[i];
//			rgbFrame[i] = d * 0.99f;
//		}

		return --frames > 0;
	}

	private void step(int idx) {
		int nx = (idx+1)%this.nx;
		for (int y = 0; y < this.ny; y++) {
			for (int z = 0; z < this.nz; z++) {
				int sum = 0;
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						int py = (y + this.ny + i) % this.ny;
						int pz = (z + this.nz + j) % this.nz;
						sum += this.old_matrix[idx][py][pz];
					}
				}
				this.old_matrix[nx][y][z] = this.rule[sum];
			}
		}
	}
}
