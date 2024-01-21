package org.corebounce.nova.content;

import java.util.ArrayList;

import org.corebounce.nova.Content;

@SuppressWarnings("nls")
public class Boids extends Content {
	ArrayList<Boid> boids;
	private double prevTime = 0;
	int nx,ny,nz;
	private double startTime;

	public Boids(int dimI, int dimJ, int dimK, int numFrames) {
		super("Boids", dimI, dimJ, dimK, numFrames);
		nx = dimI;
		ny = dimJ;
		nz = dimK;
//		MVector nc = new MVector(0,0,0);
//		MVector xc = new MVector(dimI,dimJ,dimK);
//		
//		float[] orange = {1,0.5f,0};
//		float[] cyan   = {0,0.5f,1};
//		float[] pink   = {1,0,0.5f};
//		float[] violet = {0.5f,0,1};
//		float[] lime   = {0.5f,1,0};
//		float[] mint   = {0,1,0.5f};
//		
//		float[][] colors = new float[6][3];
//		colors[0] = orange;
//		colors[1] = cyan;
//		colors[2] = pink;
//		colors[3] = violet;
//		colors[4] = lime;
//		colors[5] = mint;
//		
//		boids = new ArrayList<Boid>();
//		for (int i = 0; i < NUM; i++) {
//			Boid b = new Boid(nc,xc);
//			b.color = colors[i%6];
//			boids.add(b);
//		}
	}
	
	@Override
	public void start() {
		startTime = -1;
		prevTime  = 0;
		
		int NUM = 30;
		MVector nc = new MVector(0,0,0);
		MVector xc = new MVector(dimI,dimJ,dimK);
		
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
		
		boids = new ArrayList<>();
		for (int i = 0; i < NUM; i++) {
			Boid b = new Boid(nc,xc);
			b.color = colors[i%6];
			boids.add(b);
		}
		
		super.start();
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		if(startTime < 0) startTime = timeInSec;
		timeInSec -= startTime;
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
			int lx = (int) Math.floor(b.pos.x - rad);
			if (lx<0) lx += nx;
			int ly = (int) Math.floor(b.pos.y - rad);
			if (ly<0) ly += ny;
			int lz = (int) Math.floor(b.pos.z - rad);
			if (lz<0) lz += nz;
			for (int x=lx; x<lx+rd2; x++) {
//				int xt = x % nx;
				int xt = Math.max(0, Math.min(x, nx-1));
				for (int y=ly; y<ly+rd2; y++) {
//					int yt = y % ny;
					int yt = Math.max(0, Math.min(y, ny-1));
					for (int z=lz; z<lz+rd2; z++) {
//						int zt = z % nz;
						int zt = Math.max(0, Math.min(z, nz-1));
//						float d = (float) MVector.dist(b.pos, new MVector(x,y,z));
//						d = 2f-d;
						float d = 1;
						setVoxel(rgbFrame, xt, yt, zt, d*b.color[0], d*b.color[1], d*b.color[2]);
					}
				}
			}
		}
		// fade out
		for (int i=0; i<rgbFrame.length; i++) {
			float d = rgbFrame[i];
			rgbFrame[i] = d*0.95f;
		}
		
		return --frames > 0;
	}
}
