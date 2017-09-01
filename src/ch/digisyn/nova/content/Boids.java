package ch.digisyn.nova.content;

import java.util.ArrayList;

@SuppressWarnings("nls")
public class Boids extends Content {
	ArrayList<Boid> boids;
	public Boids(int dimI, int dimJ, int dimK, int numFrames) {
		super("Boids", dimI, dimJ, dimK, numFrames);
		int NUM = 8;
		boids = new ArrayList<Boid>();
		for (int i = 0; i < NUM; i++) {
			boids.add(new Boid());
		}
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		for (Boid b : boids) {
			b.flock(boids);
			b.move();
		}
		return --frames > 0;
	}
}
