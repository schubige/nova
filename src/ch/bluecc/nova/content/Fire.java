package ch.bluecc.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Fire extends Content {	
	
	private void sphere(final float[] rgbFrame, final float sx, final float sy, final float sz, final float r) {
		final int minX = Math.max(0,    (int) Math.floor(sx-r));
		final int maxX = Math.min(dimI, (int) Math.ceil(sx+r));
		final int minY = Math.max(0,    (int) Math.floor(sy-r));
		final int maxY = Math.min(dimJ, (int) Math.ceil(sy+r));
		final float r2 = r * r;
		
		for(int x = minX; x < maxX; x++) {
			final float dx = x - sx;
			for(int y = minY; y < maxY; y++) {
				final float dy = y - sy;
				for(int z = 0; z < dimK; z++) {
					final float dz = z - sz;
					final float d = dx * dx + dy * dy + dz * dz;
					final float b = (r2- d) / r2;
					if(b > 0f)
						addVoxelClamp(rgbFrame, x, y, z, b, b, b*b);						
				}	
			}			
		}
	}

	final static int       N_SPHERES = 8;
	final static float     JITTER    = 0.5f; 
	final float[]          spheres   = new float[5 * N_SPHERES];
	final java.util.Random rnd       = new java.util.Random();
	
	private void initSphere(int i, double timeInSec) {
		spheres[i*5+0] = rnd.nextFloat() * dimI;
		spheres[i*5+1] = rnd.nextFloat() * dimJ;
		spheres[i*5+3] = Math.max(1f, rnd.nextFloat() * dimK * 0.3f);
		spheres[i*5+4] = (float)(2.0 + timeInSec+rnd.nextDouble()*5);
		spheres[i*5+2] = (float) (timeInSec - spheres[i*5+4]);
	}
	
	public Fire(int dimI, int dimJ, int dimK, int numFrames) {
		super("Fire", dimI, dimJ, dimK, numFrames);
		
		for(int i = 0; i < N_SPHERES; i++)
			initSphere(i, 0);
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		final float dimKf = dimK;
		for(int i = 0; i < N_SPHERES; i++) {
			sphere(rgbFrame, spheres[i*5+0], spheres[i*5+1], spheres[i*5+2], spheres[i*5+3] * (0.5f + ((spheres[i*5+2]-dimKf)/(-2f*dimKf))));
			if(spheres[i*5+2] > dimKf)
				initSphere(i, timeInSec);
			else
				spheres[i*5+2] = (float) (timeInSec - spheres[i*5+4]);
		}
		sphere(rgbFrame, (dimI - JITTER) / 2f + rnd.nextFloat() * JITTER, (dimJ - JITTER) / 2f + rnd.nextFloat() * JITTER,  0, (dimI + dimJ) / 3f);
		return --frames > 0;
	}	
}
