package ch.digisyn.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Fourier extends Content {
	
	int nx, ny, nz;
	public Fourier(int dimI, int dimJ, int dimK, int numFrames) {
		super("Fourier", dimI, dimJ, dimK, numFrames);
		nx = dimI;
		ny = dimJ;
		nz = dimK;
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		for (int x = 0; x<nx; x++) {
			for (int y = 0; y<ny; y++) {
				for (int z = 0; z<nz; z++) {
					float v = (float) (Math.sin(timeInSec*x) * Math.sin(timeInSec*y) * Math.sin(timeInSec*z));
					
					v = (v+1)*0.5f;
					setVoxel(rgbFrame, x, y, z, v, v, v);
				}
			}
		}
//		float v= (float)Math.sin(timeInSec);
//		Arrays.fill(rgbFrame, v * v);
		return --frames > 0;
	}

}
