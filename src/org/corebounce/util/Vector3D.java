package org.corebounce.util;

import java.util.Arrays;

public class Vector3D {

    public double[] c = new double[3];

    /**
     * creates vector from x,y,z
     */
    public Vector3D(double x, double y, double z) {
        c[0] = x;
        c[1] = y;
        c[2] = z;
    }

    /**
     * creates vector from a double array.
     */
    public Vector3D(double[] v) {
        c[0] = v[0];
        c[1] = v[1];
        c[2] = v[2];
    }
    /**
     * creates null vector
     */
    public Vector3D() {
        this(0,0,0);
    }

    /**
     * creates vector from v
     */
    public Vector3D(Vector3D v) {
        this(v.c[0], v.c[1], v.c[2]);
    }       

    public double angle(Vector3D normal, Vector3D v) {
    	double   result;
    	Vector3D orth = v.normalize(null).cross(this, null);
		result = dot(v) / (length() * v.length());
		result = orth.dot(normal) / (orth.length() * normal.length()) > 0 ? Math.acos(result) : 2.0 * Math.PI - Math.acos(result);
		return result;
    }
    
    public double angle(Vector3D v) {
    	double result;
		result = dot(v) / (length() * v.length());
		result = Math.acos(result);
		return result;
    }
    /**
     * @return string representation of vector
     */
    @Override
	public String toString() {
        return "["+c[0]+", "+c[1]+", "+c[2]+"]";
    }

    /**
     * @return length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * @return length squared
     */
    public double lengthSquared() {
        return c[0]*c[0] + c[1]*c[1] + c[2]*c[2];
    }

    /**
     * @return normal this vector.
     */
    public void normalize() {
        double vlen = length();
        if (vlen != 0.0) {
    	    c[0] /= vlen;
    	    c[1] /= vlen;
    	    c[2] /= vlen;
        }
    }

    /**
     * @return normal vector , or null if length is 0
     */
    public Vector3D normalize(Vector3D r) {
        if (r == null) r = new Vector3D();
        double vlen = length();
        if (vlen != 0.0) {
    	    return r.set(c[0]/vlen, c[1]/vlen, c[2]/vlen);
        }
        return null;
    }

    public void unitize() {
        double vlen = length();
	    c[0] /= vlen;
	    c[1] /= vlen;
	    c[2] /= vlen;
    }
    
    public void scale(double s) {
	    c[0] *= s;
	    c[1] *= s;
	    c[2] *= s;    	
    }
    
    /**
     * @return vector scaled by s
     */
    public Vector3D scale(double s, Vector3D r) {
        if (r == null) r = new Vector3D();
        return r.set(s*c[0], s*c[1], s*c[2]);
    }

    /**
     * @return difference between vector and s
     */
    public Vector3D sub(Vector3D s, Vector3D r) {
        if (r == null) r = new Vector3D();
        return r.set(c[0] - s.c[0], c[1] - s.c[1], c[2] - s.c[2]);
    }

    public double distance(Vector3D s) {
    	return distance(s.c[0], s.c[1], s.c[2]);
    }

    public double distance(double x, double y, double z) {
    	return Math.sqrt(((c[0] - x) * (c[0] - x)) + ((c[1] - y) * (c[1] - y)) + ((c[2] - z) * (c[2] - z)));
    }

    /**
     * @return sum of vector and v
     */
    public Vector3D add(Vector3D v, Vector3D r) {
        if (r == null) r = new Vector3D();
        return r.set(c[0] + v.c[0], c[1] + v.c[1], c[2] + v.c[2]);
    }

    /**
     * @return sum of vector and v
     */
    public Vector3D add(Vector3D v) {
    	return new Vector3D(c[0] + v.c[0], c[1] + v.c[1], c[2] + v.c[2]);
    }
    
    /**
     * In-place add
     */
	public void add(double x, double y, double z) {
        c[0] += x;
        c[1] += y;
        c[2] += z;
	}

    /**
     * @return product of vector and f
     */
    public Vector3D mul(double f, Vector3D r) {
        if (r == null) r = new Vector3D();
        return r.set(c[0] * f, c[1] * f, c[2] * f);
    }
    
    /**
     * @return product of vector and f
     */
    public Vector3D mul(double f) {
    	return new Vector3D(c[0] * f, c[1] * f, c[2] * f);
    }

    /**
     * @return the negation of vector
     */
    public Vector3D negate(Vector3D r) {
        if (r == null) r = new Vector3D();
        return r.set(-c[0], -c[1], -c[2]);
    }

    /**
     * @return dot product of vector and v
     */
    public double dot(Vector3D v) {
        return c[0]*v.c[0] + c[1]*v.c[1] + c[2]*v.c[2];
    }
    
    /**
     * @return cross product of vector x v
     */
    public Vector3D cross(Vector3D v, Vector3D result) {
        if (result == null) result = new Vector3D();
        return result.set(c[1]*v.c[2]-c[2]*v.c[1], c[2]*v.c[0]-c[0]*v.c[2], c[0]*v.c[1]-c[1]*v.c[0]);
    }
    
    /**
     * @return half arc between vector and v
     */
    public Vector3D bisect(Vector3D v, Vector3D r) {
        if (r == null) r = new Vector3D();
        add(v, r);
        double length = r.length();
        return (length < 1.0e-7) ? r.set(0, 0, 1) : r.scale(1/length, r);
    }

    /**
     * @return the vector set to x,y,z
     */
    public Vector3D set(double x, double y, double z) {
        c[0] = x;
        c[1] = y;
        c[2] = z;
        return this;
    }

	public void set(Vector3D vector) {
		c[0] = vector.c[0];
		c[1] = vector.c[1];
		c[2] = vector.c[2];
	}
	
	public void set(double[] c) {
		this.c[0] = c[0];
		this.c[1] = c[1];
		this.c[2] = c[2];
	}

	public void set(float[] c) {
		this.c[0] = c[0];
		this.c[1] = c[1];
		this.c[2] = c[2];
	}

	@Override
	public int hashCode() {
		return (int) (Double.doubleToLongBits(c[0]) ^ Double.doubleToLongBits(c[1]) ^Double.doubleToLongBits(c[2]));
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			return o != null && Arrays.equals(c, ((Vector3D)o).c);
		} catch(Exception ex) {
			return false;
		}
	}

	public void copyTo(float[] v) {
		v[0] = (float) c[0];
		v[1] = (float) c[1];
		v[2] = (float) c[2];
	}
}
