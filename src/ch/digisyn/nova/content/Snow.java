package ch.digisyn.nova.content;


@SuppressWarnings("nls")
public class Snow extends Content {
	private static final int N = 50;
	private double[][] phase;
	private double[][] speed;
	private float[]    flock = new float[N*2];
	
	public Snow(int dimI, int dimJ, int dimK, int numFrames) {
		super("Snow", dimI, dimJ, dimK, numFrames);
		phase = new double[dimI][dimJ];
		speed = new double[dimI][dimJ];
		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++) {
				phase[i][j] = Math.random();
				speed[i][j] = Math.random();
			}
		int    m  = 8;
		double m1 = m -1 ;
		for(int i = 0; i < m; i++)
			flock[i] = (float)(Math.sin(Math.PI * i / m1) * Math.sin(Math.PI * i / m1));
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		int dimK1 = dimK - 1;
		for(int k = 0; k < dimK; k++) {
			for(int i = 0; i < dimI; i++)
				for(int j = 0; j < dimJ; j++) {
					int   idx = ((int) ((phase[i][j] * N + timeInSec * (0.3+speed[i][j])) * dimK) + k) % flock.length;
					float v   = flock[idx];
					setVoxel(rgbFrame, i, j, k, v, v, v);
					if(k == dimK1 && idx == N) {
						phase[i][j] = Math.random();
						speed[i][j] = Math.random();
					}
				}
		}
		return --frames > 0;
	}

}
