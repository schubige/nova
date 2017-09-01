package ch.digisyn.nova.content;

import java.util.ArrayList;

@SuppressWarnings("nls")
public class Boids extends Content {
	ArrayList<Boid> boids;
	private double prevTime = 0;
	public Boids(int dimI, int dimJ, int dimK, int numFrames) {
		super("Boids", dimI, dimJ, dimK, numFrames);
		int NUM = 20;
		MVector nc = new MVector(0,0,0);
		MVector xc = new MVector(50,10,10);
		boids = new ArrayList<Boid>();
		for (int i = 0; i < NUM; i++) {
			boids.add(new Boid(nc,xc));
		}
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		double delta = timeInSec - prevTime;
		prevTime = timeInSec;
		// update positions
		for (Boid b : boids) {
			b.flock(boids);
			b.move(delta);
		}
		// draw boids
		int rad = 1;
		int rd2 = rad * 2;
		for (Boid b : boids) {
			int lx = (int) Math.round(b.pos.x - rad);
			if (lx<0) lx += 50;
			int ly = (int) Math.round(b.pos.y - rad);
			if (ly<0) ly += 10;
			int lz = (int) Math.round(b.pos.z - rad);
			if (lz<0) lz += 10;
			for (int x=lx; x<lx+rd2; x++) {
				int xt = x % 50;
				for (int y=ly; y<ly+rd2; y++) {
					int yt = y % 10;
					for (int z=lz; z<lz+rd2; z++) {
						int zt = z % 10;
						double d = MVector.dist(b.pos, new MVector(x,y,z));
						d = 2-d;
						setVoxel(rgbFrame, xt, yt, zt, 1,1,1);
					}
				}
			}
		}
		// fade out
		for (int i=0; i<rgbFrame.length; i++) {
			float d = rgbFrame[i];
			rgbFrame[i] = d*0.9f;
		}
		
		return --frames > 0;
	}
}
