package ch.digisyn.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Pong extends Content {

	public MVector ball;
	public MVector speed;
	public double radius;

	public Pong(int dimI, int dimJ, int dimK, int numFrames) {
		super("Pong", dimI, dimJ, dimK, numFrames);

		ball = new MVector(5, 25, 5);
		double sy = FRAND(0.4,0.7);
		radius = 1.5;
		if (Math.random()>0.5) sy *= -1;
		speed = new MVector(FRAND(-0.2, 0.2), sy, FRAND(-0.2, 0.2));
	}

	double prevTime = 0;

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0);
		double d = timeInSec - prevTime;
		prevTime = timeInSec;
		ball.add(MVector.mult(speed, d*5));
		if (ball.x-radius < 0 || ball.x+radius > 10)
			speed.x = speed.x * -1;
		if (ball.y-radius < 0 || ball.y+radius > 50)
			speed.y = speed.y * -1;
		if (ball.z-radius < 0 || ball.z+radius > 10)
			speed.z = speed.z * -1;
		
		speed.y = speed.y + FRAND(-0.01,0.01);
		speed.z = speed.z + FRAND(-0.01,0.01);
		
		int rad = 2;
		int rd2 = rad * 2;
		int lx = (int) Math.floor(ball.x - radius);
		if (lx < rad)
			lx += 8;
		int ly = (int) Math.floor(ball.y - radius);
		if (ly < rad)
			ly += 48;
		int lz = (int) Math.floor(ball.z - radius);
		if (lz < rad)
			lz += 8;
		for (int x = lx; x < lx + rd2; x++) {
			int xt = x % 10;
			for (int y = ly; y < ly + rd2; y++) {
				int yt = y % 50;
				for (int z = lz; z < lz + rd2; z++) {
					int zt = z % 10;
					float dst = (float) MVector.dist(ball, new MVector(x, y, z));
					dst = 2 - dst;
					if (dst>1) dst = 1;
					// float d = 1;
//					dst = 1;
					setVoxel(rgbFrame, xt, yt, zt, dst, dst, dst);
				}
			}
		}
		
		
		
		int xn = (int) Math.max(0, ball.x-2);
		int xx = (int) Math.min(xn+4, 10);
		int zn = (int) Math.max(0, ball.z-2);
		int zx = (int) Math.min(zn+4, 10);
		for (int tx = xn; tx<xx; tx++) {
			for (int tz = zn; tz<zx; tz++) {
				setVoxel(rgbFrame, tx,  0, tz, 1, 1, 1);
				setVoxel(rgbFrame, tx, 49, tz, 1, 1, 1);
			}
		}

		return --frames > 0;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
