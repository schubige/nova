package ch.bluecc.nova.content;

import java.util.Arrays;

public class ColorSplash extends Content {
	static final int       N_BALLS      = 5;
	float[]                balls        = new float[N_BALLS * 9];
	final java.util.Random rnd          = new java.util.Random();
	final static double    PI2          = Math.PI * 2.0;
	
	private void sphere(final float[] rgbFrame, final int ballIdx) {
		final float r    = balls[ballIdx*9+3];
		final int   minX = Math.max(0,    (int) Math.floor(balls[ballIdx*9+0]-r));
		final int   maxX = Math.min(dimI, (int) Math.ceil (balls[ballIdx*9+0]+r));
		final int   minY = Math.max(0,    (int) Math.floor(balls[ballIdx*9+1]-r));
		final int   maxY = Math.min(dimJ, (int) Math.ceil (balls[ballIdx*9+1]+r));
		final float r2   = r * r;
		
		for(int x = minX; x < maxX; x++) {
			final float dx = x - balls[ballIdx*9+0];
			for(int y = minY; y < maxY; y++) {
				final float dy = y - balls[ballIdx*9+1];
				for(int z = 0; z < dimK; z++) {
					final float dz = z - balls[ballIdx*9+2];
					final float d = dx * dx + dy * dy + dz * dz;
					final float b = (r2- d) / r2;
					if(b > 0f)
						addVoxelClamp(rgbFrame, x, y, z, b * balls[ballIdx*9+6], b * balls[ballIdx*9+7], b * b * balls[ballIdx*9+8]);						
				}	
			}			
		}
	}

	public ColorSplash(int dimI, int dimJ, int dimK, int numFrames) {
		super("ColorSplash", dimI, dimJ, dimK, numFrames); //$NON-NLS-1$
		
		for(int i = 0; i < N_BALLS; i++)
			initBall(i, true);
	}

	private void initBall(int ballIdx, boolean firstTime) {
		balls[ballIdx*9+0] = rnd.nextFloat() * dimI;       // X
		balls[ballIdx*9+1] = rnd.nextFloat() * dimJ;       // Y
		balls[ballIdx*9+2] = rnd.nextFloat() * dimK;       // Z
		balls[ballIdx*9+3] = 0;                            // R
		if(firstTime)
			balls[ballIdx*9+4] = rnd.nextFloat() * (float)PI2; // time offset
		balls[ballIdx*9+5] = rnd.nextFloat() + 0.1f;       // R max
		setRGBfromHSV(rnd.nextFloat(), 1f, 1f, balls, ballIdx * 9 + 6);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		
		for(int ballIdx = 0; ballIdx < N_BALLS; ballIdx++) {
			final double  t = timeInSec + balls[ballIdx*9+4];
			// final float   w = (float)(dimK * (Math.sin(t)/(t%PI2)));
			final float   w = (float)(dimK * Math.sin(t));
			balls[ballIdx*9+3] = balls[ballIdx*9+5] * w;
			if(balls[ballIdx*9+3] > 0)
				sphere(rgbFrame, ballIdx);
			else if(balls[ballIdx*9+3] < 0)
				initBall(ballIdx, false);
		}
		return --frames > 0;
	}

}
