package ch.digisyn.nova.content;


public class Cylinder extends Content {
	private double      dimI1;
	private double      dimJ1;
	private float[][][] buffer;

	public Cylinder(int dimI, int dimJ, int dimK, int numFrames) {
		super("Cylinder", dimI, dimJ, dimK, numFrames);
		dimI1 = dimI / 2.0;
		dimJ1 = dimJ / 2.0;
		buffer = new float[dimI][dimJ][dimK];
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		double twist = 0.1;
		for(int k = 0; k < dimK; k++) {
			final double t = (timeInSec + k * twist) * Math.PI;
			double sin = Math.sin(t);
			double cos = Math.cos(t);
			int x  = (int)(dimI1 + (sin * dimI1));
			int y  = (int)(dimJ1 + (cos * dimJ1));
			int x0 = (int)(dimI1 + (sin * (dimI1-1)));
			int y0 = (int)(dimJ1 + (cos * (dimJ1-1)));
			for(int i = 0; i < dimI; i++) {
				for(int j = 0; j < dimJ; j++) {
					if(x == i && y == j || x0 == i && y0 == j)
						buffer[i][j][k] =  1f;
					else
						buffer[i][j][k] *= 0.95f;
					final float b = buffer[i][j][k];
					setVoxel(rgbFrame, i, j, k, b, b, b);
				}
			}
		}
		return --frames > 0;
	}

}
