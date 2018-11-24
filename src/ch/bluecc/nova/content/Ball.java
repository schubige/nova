package ch.bluecc.nova.content;

public class Ball {

	public MVector pos, vel;
	public MVector minc, maxc;
	double radius;
	
	public Ball(double x, double y, double z) {
		this.pos = new MVector(x,y,z);
		this.vel = MVector.random3D();
		this.vel.y *= 3;
		this.radius = 1;
	}
	
	public void update(double t) {
		double chg = 0.03;
		this.pos.add(MVector.mult(this.vel, t));
		if (pos.x > maxc.x || pos.x < minc.x)
			vel.x *= -1;
		if (pos.y > maxc.y || pos.y < minc.y) {
			vel.y *= -1.01;
//			vel.x = vel.x + FRAND(-chg,chg);
//			vel.z = vel.z + FRAND(-chg,chg);
		}
		if (pos.z > maxc.z || pos.z < minc.z)
			vel.z *= -1;
	}
	
	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}
}
