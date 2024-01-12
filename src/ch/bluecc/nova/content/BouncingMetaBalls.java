package ch.bluecc.nova.content;

import ch.bluecc.nova.Content;

@SuppressWarnings("nls")
public class BouncingMetaBalls extends Content {

	private static final int NUM = 8; // number of ball centers
	private double[][] positions;
	private double[][] speeds;
	private double[] radii;
	private int[] dim;
	private double prevTime = 0;
	private double startTime;

	public BouncingMetaBalls(int dimI, int dimJ, int dimK, int numFrames) {
		super("BouncingMetaBalls", dimI, dimJ, dimK, numFrames);
//		this.positions = new double[NUM][3];
//		this.speeds = new double[NUM][3];
		dim = new int[3];
		dim[0] = dimI;
		dim[1] = dimJ;
		dim[2] = dimK;
//		this.radii = new double[NUM];
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
		startTime = -1;
		prevTime  = 0;
		
		this.positions = new double[NUM][3];
		this.speeds = new double[NUM][3];
		this.radii = new double[NUM];

		for (int i = 0; i < NUM; i++) {
			for (int j = 0; j < 3; j++) {
				this.positions[i][j] = FRAND(0, dim[j]);
				this.speeds[i][j] = FRAND(-0.2, 0.2);
				if (j==1) this.speeds[i][j] *= 3;
			}
			this.radii[i] = FRAND(1.2, 2.2);
		}
		
		super.start();
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
				this.positions[i][j] += d*2 * this.speeds[i][j];
				if (this.positions[i][j] < 0 || this.positions[i][j] > dim[j]) {
					this.speeds[i][j] *= -1;
				}
//				this.speeds[i][j] += FRAND(-0.02, 0.02);
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
					double S = 1;
					// double V = sum > 80 ? 1 : 0;
					// activation functions
					// double V = (Math.tanh(sum-80)+1)*0.5; // tanh
					double V = 1 / (1 + Math.exp(sum - 140)); // sigmoid
					V = 1 - V;
					double C = V * S;
					double H = CROP_INTERVAL(sum, 0, 360);
					double X = C * (1 - Math.abs(sum / 60.0 % 2 - 1));

					// HSV to RGB conversion for fancy rainbow colors
					double r, g, b;
					if (H < 60) {
						r = C;
						g = X;
						b = 0;
					} else if (H < 120) {
						r = X;
						g = C;
						b = 0;
					} else if (H < 180) {
						r = 0;
						g = C;
						b = X;
					} else if (H < 240) {
						r = 0;
						g = X;
						b = C;
					} else if (H < 300) {
						r = X;
						g = 0;
						b = C;
					} else {
						r = C;
						g = 0;
						b = X;
					}

					setVoxel(rgbFrame, x, y, z, (float) r, (float) g, (float) b);
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
