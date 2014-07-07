package ch.digisyn.nova.content;

import java.util.Arrays;

public class Fire extends Content {
	double[][] off;
	
	public Fire(int dimI, int dimJ, int dimK, int numFrames) {
		super("Fire", dimI, dimJ, dimK, numFrames);
		off = new double[dimI][dimJ];
		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++)
				off[i][j] = Math.random() * 2 * Math.PI;
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++) {
				double v    = Math.sin(timeInSec * 0.2 + off[i][j]);
				int    klim = (int) (dimK * v * v);
				for(int k = 0; k < klim; k++) {
					float b = (float)(dimK - k) / dimK;
					setVoxel(rgbFrame, i, j, k, b, b, 0);
				}
			}
		return --frames > 0;
	}

}
