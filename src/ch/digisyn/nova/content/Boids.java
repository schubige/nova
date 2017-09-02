package ch.digisyn.nova.content;

import java.util.ArrayList;

@SuppressWarnings("nls")
public class Boids extends Content {
	ArrayList<Boid> boids;
	private double prevTime = 0;
	public Boids(int dimI, int dimJ, int dimK, int numFrames) {
		super("Boids", dimI, dimJ, dimK, numFrames);
		int NUM = 10;
		MVector nc = new MVector(0,0,0);
		MVector xc = new MVector(50,10,10);
		
		float[] orange = {1,0.5f,0};
		float[] cyan   = {0,0.5f,1};
		float[] pink   = {1,0,0.5f};
		float[] violet = {0.5f,0,1};
		float[] lime   = {0.5f,1,0};
		float[] mint   = {0,1,0.5f};
		
		float[][] colors = new float[6][3];
		colors[0] = orange;
		colors[1] = cyan;
		colors[2] = pink;
		colors[3] = violet;
		colors[4] = lime;
		colors[5] = mint;
		
		boids = new ArrayList<Boid>();
		for (int i = 0; i < NUM; i++) {
			Boid b = new Boid(nc,xc);
			b.color = colors[i%6];
			boids.add(b);
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
		int rad = 2;
		int rd2 = rad * 2;
		for (Boid b : boids) {
			int lx = (int) Math.floor(b.pos.x - rad);
			if (lx<0) lx += 50;
			int ly = (int) Math.floor(b.pos.y - rad);
			if (ly<0) ly += 10;
			int lz = (int) Math.floor(b.pos.z - rad);
			if (lz<0) lz += 10;
			for (int x=lx; x<lx+rd2; x++) {
				int xt = x % 50;
				for (int y=ly; y<ly+rd2; y++) {
					int yt = y % 10;
					for (int z=lz; z<lz+rd2; z++) {
						int zt = z % 10;
						float d = (float) MVector.dist(b.pos, new MVector(x,y,z));
						d = 2-d;
						setVoxel(rgbFrame, xt, yt, zt, d*b.color[0], d*b.color[1], d*b.color[2]);
					}
				}
			}
		}
		// fade out
		for (int i=0; i<rgbFrame.length; i++) {
			float d = rgbFrame[i];
			rgbFrame[i] = d*0.5f;
		}
		
		return --frames > 0;
	}
}
