package ch.digisyn.nova.content;

import java.util.ArrayList;

public class Boid {
	public MVector pos, vel, acc;
	public MVector ali, coh, sep;
	private double maxSpeed = 2;
	public Boid() {
		pos = new MVector(FRAND(3,47), FRAND(2,8), FRAND(2,8));
		vel = MVector.random3D();
		acc = new MVector();
		ali = new MVector();
		coh = new MVector();
		sep = new MVector();
	}
	
	public void move() {
		this.vel.add(acc);
		this.vel.limit(maxSpeed);
		this.pos.add(this.vel);
		this.acc.mult(0);
	}
	
	public void flock(ArrayList<Boid> boids) {
		ali = this.alignment(boids);
		coh = this.cohesion(boids);
		sep = this.separation(boids);
		this.acc.add(MVector.mult(ali,1));
		this.acc.add(MVector.mult(coh,3));
		this.acc.add(MVector.mult(sep,1));
	}
	
	private MVector separation(ArrayList<Boid> boids) {
		// TODO Auto-generated method stub
		return null;
	}

	private MVector cohesion(ArrayList<Boid> boids) {
		// TODO Auto-generated method stub
		return null;
	}

	private MVector alignment(ArrayList<Boid> boids) {
		// TODO Auto-generated method stub
		return null;
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
