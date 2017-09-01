package ch.digisyn.nova.content;

public class MVector {
	public double x, y, z;

	public MVector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public MVector(double _x, double _y, double _z) {
		this.x = _x;
		this.y = _y;
		this.z = _z;
	}

	public void add(MVector a) {
		this.x += a.x;
		this.y += a.y;
		this.z += a.z;
	}

	public void sub(MVector a) {
		this.x -= a.x;
		this.y -= a.y;
		this.z -= a.z;
	}

	public void mult(double f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
	}

	public void div(double f) {
		this.x /= f;
		this.y /= f;
		this.z /= f;
	}

	public void set(double _x, double _y, double _z) {
		this.x = _x;
		this.y = _y;
		this.z = _z;
	}

	public double[] get() {
		double[] out = { this.x, this.y, this.z };
		return out;
	}

	public double mag() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}

	public double dist(MVector t) {
		return Math.sqrt(distSq(t));
	}

	public double distSq(MVector t) {
		double dx = this.x - t.x;
		double dy = this.y - t.y;
		double dz = this.z - t.z;
		return Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2);
	}

	public MVector norm() {
		double mg = mag();
		return new MVector(this.x / mg, this.y / mg, this.z / mg);
	}

	public static MVector random3D() {
		double u = Math.random();
		double v = Math.random();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = Math.sin(phi) * Math.cos(theta);
		double y = Math.sin(phi) * Math.sin(theta);
		double z = Math.cos(phi);
		return new MVector(x, y, z);
	}

	public static MVector mult(MVector v, double f) {
		return new MVector(v.x*f, v.y*f, v.z*f);
	}

	public void limit(double l) {
		double mg = this.mag();
		if (mg>l) {
			this.mult(l/mg);
		}
	}
}
