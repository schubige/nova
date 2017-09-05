package ch.digisyn.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Pong extends Content {

	MVector ball;
	MVector speed;
	double radius;

	public Pong(int dimI, int dimJ, int dimK, int numFrames) {
		super("BoidsNr", dimI, dimJ, dimK, numFrames);

		ball = new MVector(25, 5, 5);
		double sx = FRAND(0.4,0.7);
		radius = 1;
		if (Math.random()>0.5) sx *= -1;
		speed = new MVector(sx, FRAND(-0.2, 0.2), FRAND(-0.2, 0.2));
	}

	double prevTime = 0;

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0);
		double d = timeInSec - prevTime;
		prevTime = timeInSec;
		ball.add(MVector.mult(speed, d*5));
		if (ball.x-radius < 0 || ball.x+radius > 50)
			speed.x = speed.x * -1;
		if (ball.y-radius < 0 || ball.y+radius > 10)
			speed.y = speed.y * -1;
		if (ball.z-radius < 0 || ball.z+radius > 10)
			speed.z = speed.z * -1;
		
		speed.y = speed.y + FRAND(-0.01,0.01);
		speed.z = speed.z + FRAND(-0.01,0.01);
		
		int rad = 1;
		int rd2 = rad * 2;
		int lx = (int) Math.floor(ball.x - rad);
		if (lx < rad)
			lx += 48;
		int ly = (int) Math.floor(ball.y - rad);
		if (ly < rad)
			ly += 8;
		int lz = (int) Math.floor(ball.z - rad);
		if (lz < rad)
			lz += 8;
		for (int x = lx; x < lx + rd2; x++) {
			int xt = x % 50;
			for (int y = ly; y < ly + rd2; y++) {
				int yt = y % 10;
				for (int z = lz; z < lz + rd2; z++) {
					int zt = z % 10;
					float dst = (float) MVector.dist(ball, new MVector(x, y, z));
					dst = 2 - dst;
					// float d = 1;
//					dst = 1;
					setVoxel(rgbFrame, xt, yt, zt, dst, dst, dst);
				}
			}
		}
		
		
		
		int yn = (int) Math.max(0, ball.y-2);
		int yx = (int) Math.min(yn+4, 10);
		int zn = (int) Math.max(0, ball.z-2);
		int zx = (int) Math.min(zn+4, 10);
		for (int ty = yn; ty<yx; ty++) {
			for (int tz = zn; tz<zx; tz++) {
				setVoxel(rgbFrame,  0, ty, tz, 1, 1, 1);
				setVoxel(rgbFrame, 49, ty, tz, 1, 1, 1);
			}
		}

		// fade out
//		for (int i = 0; i < rgbFrame.length; i++) {
//			float d = rgbFrame[i];
//			rgbFrame[i] = d * 0.98f;
//		}

		return --frames > 0;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
