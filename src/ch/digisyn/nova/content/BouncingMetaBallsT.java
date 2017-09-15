package ch.digisyn.nova.content;

@SuppressWarnings("nls")
public class BouncingMetaBallsT extends Content {

	private static final int NUM = 8; // number of ball centers
	private double[][] positions;
	private double[][] speeds;
	private double[] radii;
	private int[] dim;
	private double prevTime = 0;
	private double startTime;

	public BouncingMetaBallsT(int dimI, int dimJ, int dimK, int numFrames) {
		super("BouncingMetaBallsT", dimI, dimJ, dimK, numFrames);
		this.positions = new double[NUM][3];
		this.speeds = new double[NUM][3];
		dim = new int[3];
		dim[0] = dimI;
		dim[1] = dimJ;
		dim[2] = dimK;
		this.radii = new double[NUM];
//		for (int i = 0; i < NUM; i++) {
//			for (int j = 0; j < 3; j++) {
//				this.positions[i][j] = FRAND(0, dim[j]);
//				this.speeds[i][j] = FRAND(-0.2, 0.2);
//				if (j==1) this.speeds[i][j] *= 3;
//			}
//			this.radii[i] = FRAND(1.2, 2.2);
//		}
	}
	
	@Override
	public void start() {
		startTime = -1.0;
		for (int i = 0; i < NUM; i++) {
			for (int j = 0; j < 3; j++) {
				this.positions[i][j] = FRAND(0, dim[j]);
				this.speeds[i][j] = FRAND(-0.2, 0.2);
				if (j==1) this.speeds[i][j] *= 3;
			}
			this.radii[i] = FRAND(1.2, 2.2);
		}
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		// update positions
		if(startTime < 0) startTime = timeInSec;
		timeInSec -= startTime;
		double d = timeInSec - prevTime;
		prevTime = timeInSec;
		for (int i = 0; i < NUM; i++) {
			for (int j = 0; j < 3; j++) {
				this.positions[i][j] += d*3 * this.speeds[i][j];
				if (this.positions[i][j] < 0 || this.positions[i][j] > dim[j]) {
					this.speeds[i][j] *= -1;
				}
			}
		}

		// update voxel colors
		for (int z = 0; z < dimK; z++) {
			for (int y = 0; y < dimJ; y++) {
				for (int x = 0; x < dimI; x++) {
					double sum = 0;
					for (int i = 0; i < NUM; i++) {
						double rad = this.radii[i];
						double dsq = Math.pow((this.positions[i][0] - x), 2) + Math.pow((this.positions[i][1] - y), 2)
								+ Math.pow((this.positions[i][2] - z), 2);
						dsq = Math.sqrt(dsq);
						sum += 110 * rad / dsq;
					}
					double r,g,b;
					double v = CROP_INTERVAL(sum, 0, 360);
					double V = 1 / (1 + Math.exp(v - 140)); // sigmoid
//					v = v>120?1:0;
					v = 1-V;

					setVoxel(rgbFrame, x, y, z, (float) v, 0, (float) (v*0.5));
				}
			}
		}
		return --frames > 0;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}

	public static double CROP_INTERVAL(double val, double low, double high) {
		return val < low ? low : (val > high ? high : val);
	}
}
