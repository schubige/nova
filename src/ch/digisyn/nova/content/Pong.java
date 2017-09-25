package ch.digisyn.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Pong extends Content {

	public MVector ball;
	public MVector speed;
	public double radius;
	int nx,ny,nz;
	private double startTime;

	public Pong(int dimI, int dimJ, int dimK, int numFrames) {
		super("Pong", dimI, dimJ, dimK, numFrames);
		nx = dimI;
		ny = dimJ;
		nz = dimK;

//		ball = new MVector(nx/2,ny/2,nz/2);
//		double sy = FRAND(0.4,0.7);
//		radius = 1.5;
//		if (Math.random()>0.5) sy *= -1;
//		speed = new MVector(FRAND(-0.2, 0.2), sy, FRAND(-0.2, 0.2));
	}

	double prevTime = 0;

	@Override
	public void start() {
		startTime = -1;
		prevTime  = 0;
		
		ball = new MVector(nx/2,ny/2,nz/2);
		double sy = FRAND(0.4,0.7);
		if (Math.random()>0.5) sy *= -1;
		speed = new MVector(FRAND(-0.2, 0.2), sy, FRAND(-0.2, 0.2));
		radius = 1.5;
		
		super.start();
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		
		if(startTime < 0) startTime = timeInSec;
		timeInSec -= startTime;
		
		Arrays.fill(rgbFrame, 0);
		double d = timeInSec - prevTime;
		prevTime = timeInSec;
		ball.add(MVector.mult(speed, d*7));
		if (ball.x-radius < 0 || ball.x+radius > nx)
			speed.x = speed.x * -1;
		if (ball.y-radius < 0 || ball.y+radius > ny) {
			speed.y = speed.y * -1.01;
			double chg = 0.03;
			speed.x = speed.x + FRAND(-chg,chg);
			speed.z = speed.z + FRAND(-chg,chg);
		}
		if (ball.z-radius < 0 || ball.z+radius > nz)
			speed.z = speed.z * -1;
				
		int rad = 1;
		int rd2 = rad * 2;
		int lx = (int) Math.floor(ball.x - rad);
		if (lx < 0)
			lx += nx;
		int ly = (int) Math.floor(ball.y - rad);
		if (ly < 0)
			ly += ny;
		int lz = (int) Math.floor(ball.z - rad);
		if (lz < 0)
			lz += nz;
		for (int x=lx; x<lx+rd2; x++) {
//			int xt = x % nx;
			int xt = Math.max(0, Math.min(x, nx-1));
			for (int y=ly; y<ly+rd2; y++) {
//				int yt = y % ny;
				int yt = Math.max(0, Math.min(y, ny-1));
				for (int z=lz; z<lz+rd2; z++) {
//					int zt = z % nz;
					int zt = Math.max(0, Math.min(z, nz-1));
//					float dst = (float) MVector.dist(ball, new MVector(x, y, z));
//					dst = 2 - dst;
//					if (dst>1) dst = 1;
					// float d = 1;
//					dst = 1;
					setVoxel(rgbFrame, xt, yt, zt, 0, 0.6f, 1);
				}
			}
		}
		
		
		
		int xn = (int) Math.max(0, ball.x-2);
		int xx = (int) Math.min(xn+4, nx);
		int zn = (int) Math.max(0, ball.z-2);
		int zx = (int) Math.min(zn+4, nz);
		for (int tx = xn; tx<xx; tx++) {
			for (int tz = zn; tz<zx; tz++) {
				setVoxel(rgbFrame, tx,  0, tz, 1, 1, 1);
				setVoxel(rgbFrame, tx, ny-1, tz, 1, 1, 1);
			}
		}

		return --frames > 0;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
