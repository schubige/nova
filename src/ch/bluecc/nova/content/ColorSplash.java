package ch.bluecc.nova.content;

import java.util.Arrays;

public class ColorSplash extends Content {
	static final int       N_SPHERES    = 5;
	float[]                spheres      = new float[N_SPHERES * 9];
	final java.util.Random rnd          = new java.util.Random();
	final static double    PI2          = Math.PI * 2.0;
	
	private void sphere(final float[] rgbFrame, final int sphereIdx) {
		final float r    = spheres[sphereIdx*9+3];
		final int   minX = Math.max(0,    (int) Math.floor(spheres[sphereIdx*9+0]-r));
		final int   maxX = Math.min(dimI, (int) Math.ceil (spheres[sphereIdx*9+0]+r));
		final int   minY = Math.max(0,    (int) Math.floor(spheres[sphereIdx*9+1]-r));
		final int   maxY = Math.min(dimJ, (int) Math.ceil (spheres[sphereIdx*9+1]+r));
		final float r2   = r * r;
		
		for(int x = minX; x < maxX; x++) {
			final float dx = x - spheres[sphereIdx*9+0];
			for(int y = minY; y < maxY; y++) {
				final float dy = y - spheres[sphereIdx*9+1];
				for(int z = 0; z < dimK; z++) {
					final float dz = z - spheres[sphereIdx*9+2];
					final float d = dx * dx + dy * dy + dz * dz;
					final float b = (r2- d) / r2;
					if(b > 0f)
						addVoxelClamp(rgbFrame, x, y, z, b * spheres[sphereIdx*9+6], b * spheres[sphereIdx*9+7], b * b * spheres[sphereIdx*9+8]);						
				}	
			}			
		}
	}

	public ColorSplash(int dimI, int dimJ, int dimK, int numFrames) {
		super("ColorSplash", dimI, dimJ, dimK, numFrames); //$NON-NLS-1$
		
		for(int i = 0; i < N_SPHERES; i++)
			initSphere(i, true);
	}

	private void initSphere(int sphereIdx, boolean firstTime) {
		spheres[sphereIdx*9+0] = rnd.nextFloat() * dimI;       // X
		spheres[sphereIdx*9+1] = rnd.nextFloat() * dimJ;       // Y
		spheres[sphereIdx*9+2] = rnd.nextFloat() * dimK;       // Z
		spheres[sphereIdx*9+3] = 0;                            // R
		if(firstTime)
			spheres[sphereIdx*9+4] = rnd.nextFloat() * (float)PI2; // time offset
		spheres[sphereIdx*9+5] = rnd.nextFloat() + 0.1f;       // R max
		setRGBfromHSV(rnd.nextFloat(), 1f, 1f, spheres, sphereIdx * 9 + 6);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		
		for(int ballIdx = 0; ballIdx < N_SPHERES; ballIdx++) {
			final double  t = timeInSec + spheres[ballIdx*9+4];
			// final float   w = (float)(dimK * (Math.sin(t)/(t%PI2)));
			final float   w = (float)(dimK * Math.sin(t));
			spheres[ballIdx*9+3] = spheres[ballIdx*9+5] * w;
			if(spheres[ballIdx*9+3] > 0)
				sphere(rgbFrame, ballIdx);
			else if(spheres[ballIdx*9+3] < 0)
				initSphere(ballIdx, false);
		}
		return --frames > 0;
	}

}
