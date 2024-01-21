package org.corebounce.nova.content;

import java.util.ArrayList;
import java.util.Arrays;

import org.corebounce.nova.Content;

@SuppressWarnings("nls")
public class Pong2 extends Content {

	public ArrayList<Ball> balls;
	int nx, ny, nz;
	MVector nc, xc;
	private double startTime;

	public Pong2(int dimI, int dimJ, int dimK, int numFrames) {
		super("Pong2", dimI, dimJ, dimK, numFrames);
		nx = dimI;
		ny = dimJ;
		nz = dimK;

//		nc = new MVector(0, 0, 0);
//		xc = new MVector(nx, ny, nz);

//		balls = new ArrayList<Ball>();
//		for (int i = 0; i < 1; i++) {
//			Ball b = new Ball(nx / 2.0, ny / 2.0, nz / 2.0);
//			b.minc = nc;
//			b.maxc = xc;
//			balls.add(b);
//		}
	}

	double prevTime = 0;
	int secs = 0;

	@Override
	public void start() {
		startTime = -1;
		secs      = 0;
		prevTime  = 0;
		
		nc = new MVector(0, 0, 0);
		xc = new MVector(nx, ny, nz);
		balls = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			Ball b = new Ball(nx / 2.0, ny / 2.0, nz / 2.0);
			b.minc = nc;
			b.maxc = xc;
			balls.add(b);
		}
		
		super.start();
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		
		if(startTime < 0) startTime = timeInSec;
		
		timeInSec -= startTime;
		
		if ((int)timeInSec != secs) {
			secs++;
			if (secs % 30 == 0 && balls.size()<10) {
				Ball b = new Ball(nx / 2.0, ny / 2.0, nz / 2.0);
				b.minc = nc;
				b.maxc = xc;
				balls.add(b);
			}
		}
		
		Arrays.fill(rgbFrame, 0);
		double d = timeInSec - prevTime;
		prevTime = timeInSec;

		for (Ball b : balls) {
			b.update(d*1.3);
		}

		for (Ball b : balls) {
			int rad = 1;
			int rd2 = rad * 2;
			int lx = (int) Math.floor(b.pos.x - rad);
			if (lx < 0)
				lx = 0; //+= nx;
			int ly = (int) Math.floor(b.pos.y - rad);
			if (ly < 0)
				ly = 0; //+= ny;
			int lz = (int) Math.floor(b.pos.z - rad);
			if (lz < 0)
				lz = 0; //+= nz;
			for (int x = lx; x < lx + rd2; x++) {
				// int xt = x % nx;
				int xt = Math.max(0, Math.min(x, nx - 1));
				for (int y = ly; y < ly + rd2; y++) {
					// int yt = y % ny;
					int yt = Math.max(0, Math.min(y, ny - 1));
					for (int z = lz; z < lz + rd2; z++) {
						// int zt = z % nz;
						int zt = Math.max(0, Math.min(z, nz - 1));
						// float dst = (float) MVector.dist(ball, new MVector(x,
						// y, z));
						// dst = 2 - dst;
						// if (dst>1) dst = 1;
						// float d = 1;
						// dst = 1;
						setVoxel(rgbFrame, xt, yt, zt, 0, 0.6f, 1);
					}
				}
			}
		}

		Ball p1 = balls.get(0);
		Ball p2 = balls.get(0);
		for (Ball b : balls) {
			if (b.pos.y < p1.pos.y)
				p1 = b;
			if (b.pos.y > p2.pos.y)
				p2 = b;
		}

		int xn = (int) Math.max(0, p1.pos.x - 2);
		int xx = (int) Math.min(xn + 4, nx);
		int zn = (int) Math.max(0, p1.pos.z - 2);
		int zx = (int) Math.min(zn + 4, nz);
		for (int tx = xn; tx < xx; tx++) {
			for (int tz = zn; tz < zx; tz++) {
				setVoxel(rgbFrame, tx, 0, tz, 1, 1, 1);
				// setVoxel(rgbFrame, tx, ny - 1, tz, 1, 1, 1);
			}
		}

		xn = (int) Math.max(0, p2.pos.x - 2);
		xx = (int) Math.min(xn + 4, nx);
		zn = (int) Math.max(0, p2.pos.z - 2);
		zx = (int) Math.min(zn + 4, nz);
		for (int tx = xn; tx < xx; tx++) {
			for (int tz = zn; tz < zx; tz++) {
				// setVoxel(rgbFrame, tx, 0, tz, 1, 1, 1);
				setVoxel(rgbFrame, tx, ny - 1, tz, 1, 1, 1);
			}
		}

		return --frames > 0;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
