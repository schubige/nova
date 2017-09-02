package ch.digisyn.nova.content;

import java.util.ArrayList;

public class Boid {
	public MVector pos, vel, acc;
	public MVector ali, coh, sep;
	public MVector minc, maxc;
	private double maxSpeed = 1.8;
	private double neighborhoodRadius = 2;
	private double maxSteerForce = 0.05;
	public float[] color;

	public Boid() {
		pos = new MVector(FRAND(3, 47), FRAND(2, 8), FRAND(2, 8));
		vel = MVector.random3D();
		acc = new MVector();
		ali = new MVector();
		coh = new MVector();
		sep = new MVector();
	}
	
	public Boid(MVector nc, MVector xc) {
		pos = new MVector(FRAND(nc.x,xc.x), FRAND(nc.y,xc.y), FRAND(nc.z,xc.z));
		vel = MVector.random3D();
		vel.mult(0.7);
		acc = new MVector();
		ali = new MVector();
		coh = new MVector();
		sep = new MVector();
		minc = nc;
		maxc = xc;
	}

	public void move(double t) {
		this.vel.add(acc);
		this.vel.limit(maxSpeed);
		this.pos.add(MVector.mult(this.vel, t));
		this.acc.mult(0);
		
		if (pos.x > maxc.x) pos.x = minc.x;
		if (pos.x < minc.x) pos.x = maxc.x;
		if (pos.y > maxc.y) pos.y = minc.y;
		if (pos.y < minc.y) pos.y = maxc.y;
		if (pos.z > maxc.z) pos.z = minc.z;
		if (pos.z < minc.z) pos.z = maxc.z;
	}

	public void flock(ArrayList<Boid> boids) {
		ali = this.alignment(boids);
		coh = this.cohesion(boids);
		sep = this.separation(boids);
		this.acc.add(MVector.mult(ali, 1));
		this.acc.add(MVector.mult(coh, 2));
		this.acc.add(MVector.mult(sep, 2));
	}

	private MVector separation(ArrayList<Boid> boids) {
		MVector posSum = new MVector();
		MVector repulse;
		for (Boid b : boids) {
			double d = MVector.dist(this.pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				repulse = MVector.sub(pos, b.pos);
				repulse = repulse.norm();
				repulse.div(d);
				posSum.add(repulse);
			}
		}
		return posSum;
	}

	private MVector cohesion(ArrayList<Boid> boids) {
		MVector posSum = new MVector(0, 0, 0);
		MVector steer = new MVector(0, 0, 0);
		int count = 0;
		for (Boid b : boids) {
			double d = MVector.dist(this.pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				posSum.add(b.pos);
				count++;
			}
		}
		if (count > 0) {
			posSum.div((float) count);
		}
		steer = MVector.sub(posSum, pos);
		steer.limit(maxSteerForce);
		return steer;
	}

	private MVector alignment(ArrayList<Boid> boids) {
		MVector velSum = new MVector(0, 0, 0);
		int count = 0;
		for (Boid b : boids) {
			double d = MVector.dist(pos, b.pos);
			if (d > 0 && d <= neighborhoodRadius) {
				velSum.add(b.vel);
				count++;
			}
		}
		if (count > 0) {
			velSum.div((float) count);
			velSum.limit(maxSteerForce);
		}
		return velSum;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
