package org.corebounce.util;


public class Matrix3x3 {

	// matrix represented by Mrow-col
	public double m00, m01, m02;
	public double m10, m11, m12;
	public double m20, m21, m22;

	/**
	 * creates a new zero-ed matrix
	 */
	public Matrix3x3() {
	}

	/**
	 * creates a new matrix with initial values from s
	 */
	public Matrix3x3(Matrix3x3 s) {
		set(s);
	}

	/**
	 * creates a new matrix from an 16 value array, col by col
	 */
	public Matrix3x3(double[] a) {
		setRowFirst(a[0], a[3], a[ 6],
				a[1], a[4], a[ 7],
				a[2], a[5], a[8]);
	}

	/**
	 * creates a new matrix from values
	 */
	public Matrix3x3(
			double a00, double a01, double a02,
			double a10, double a11, double a12,
			double a20, double a21, double a22) {
		setRowFirst(a00, a01, a02,
				a10, a11, a12,
				a20, a21, a22);
	}

	public Matrix3x3 copy() {
		return new Matrix3x3(this);
	}

	/**
	 * sets all values in this matrix
	 */
	public void setRowFirst(double a00, double a01, double a02,
			double a10, double a11, double a12,
			double a20, double a21, double a22) {
		m00 = a00; m01 = a01; m02 = a02;
		m10 = a10; m11 = a11; m12 = a12;
		m20 = a20; m21 = a21; m22 = a22;
	}

	/**
	 * sets all values in this matrix
	 */
	public void setColumnFirst(double a00, double a01, double a02,
			double a10, double a11, double a12,
			double a20, double a21, double a22) {
		m00 = a00; m10 = a01; m20 = a02; 
		m01 = a10; m11 = a11; m21 = a12; 
		m02 = a20; m12 = a21; m22 = a22; 
	}

	/**
	 * sets all values in this matrix to the ones in m
	 */
	public void set(Matrix3x3 m) {
		setRowFirst(m.m00, m.m01, m.m02,
				m.m10, m.m11, m.m12,
				m.m20, m.m21, m.m22);
	}

	public void setColumnFirst(double[] ds) {
		setColumnFirst(
				ds[0],  ds[1],  ds[2],  
				ds[3],	ds[4],  ds[5],  
				ds[6],  ds[7],  ds[8]);
	}

	/**
	 * @return an identity matrix
	 */
	public static Matrix3x3 identity() {
		return new Matrix3x3(
				1.0, 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, 1.0
		);
	}

	/**
	 * @return an identity matrix
	 */
	public void setIdentity() {
		setRowFirst(
				1.0, 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, 1.0
		);
	}

	/**
	 * multiplies matrix by s
	 */
	public void multiply(double s) {

		m00 *= s; m01 *= s; m02 *= s;
		m10 *= s; m11 *= s; m12 *= s;
		m20 *= s; m21 *= s; m22 *= s;
	}

	/**
	 * multiplies vector v (dim >= 3) by matrix, stores result in tv
	 */
	public double[] multiply(double[] v, double[] result) {
		if(result == null)
			result = new double[3];

		double x = v[0];
		double y = v[1];
		double z = v[2];
		result[0] = x * m00 + y * m01 + z * m02;
		result[1] = x * m10 + y * m11 + z * m12;
		result[2] = x * m20 + y * m21 + z * m22;

		return result;
	}

	/**
	 * multiplies vector v by matrix.
	 */
	public Vector3D multiply(Vector3D v) {
		double x = v.c[0];
		double y = v.c[1];
		double z = v.c[2];
		return new Vector3D(x * m00 + y * m01 + z * m02,
				x * m10 + y * m11 + z * m12,
				x * m20 + y * m21 + z * m22);
	}

	/**
	 * multiplies vector v (dim >= 3) by matrix, stores result in tv
	 */
	public float[] multiply(float[] v, float[] result) {
		if(result == null)
			result = new float[3];

		double x = v[0];
		double y = v[1];
		double z = v[2];

		result[0] = (float)(x * m00 + y * m01 + z * m02);
		result[1] = (float)(x * m10 + y * m11 + z * m12);
		result[2] = (float)(x * m20 + y * m21 + z * m22);

		return result;
	}


	/**
	 * multiplies matrix by p: using C = C x P
	 */
	public Matrix3x3 multiply(Matrix3x3 p) {
		setRowFirst(
				m00*p.m00 + m01*p.m10 + m02*p.m20,
				m00*p.m01 + m01*p.m11 + m02*p.m21,
				m00*p.m02 + m01*p.m12 + m02*p.m22,

				m10*p.m00 + m11*p.m10 + m12*p.m20,
				m10*p.m01 + m11*p.m11 + m12*p.m21,
				m10*p.m02 + m11*p.m12 + m12*p.m22,

				m20*p.m00 + m21*p.m10 + m22*p.m20,
				m20*p.m01 + m21*p.m11 + m22*p.m21,
				m20*p.m02 + m21*p.m12 + m22*p.m22
		);
		return this;
	}

	/**
	 * pre-multiplies matrix by p: using C = P x C
	 */
	public Matrix3x3 preMultiply(Matrix3x3 p) {
		setRowFirst(
				p.m00*m00 + p.m01*m10 + p.m02*m20,
				p.m00*m01 + p.m01*m11 + p.m02*m21,
				p.m00*m02 + p.m01*m12 + p.m02*m22 ,

				p.m10*m00 + p.m11*m10 + p.m12*m20,
				p.m10*m01 + p.m11*m11 + p.m12*m21,
				p.m10*m02 + p.m11*m12 + p.m12*m22,

				p.m20*m00 + p.m21*m10 + p.m22*m20,
				p.m20*m01 + p.m21*m11 + p.m22*m21,
				p.m20*m02 + p.m21*m12 + p.m22*m22
		);
		return this;
	}

	/**
	 * normalizes matrix by d
	 */
	public void normalize(double d) {
		m00 /= d; m01 /= d; m02 /= d;
		m10 /= d; m11 /= d; m12 /= d;
		m20 /= d; m21 /= d; m22 /= d;
	}

	/**
	 * @return true if matrix is equal to object
	 */
	@Override
	public boolean equals(Object object) {
		if ((object != null) && (object instanceof Matrix3x3)) {
			Matrix3x3 c = (Matrix3x3)object;
			return c.m00 == m00 && c.m01 == m01 && c.m02 == m02
			&& c.m10 == m10 && c.m11 == m11 && c.m12 == m12 
			&& c.m20 == m20 && c.m21 == m21 && c.m22 == m22;
		}
		return false;
	}

	/**
	 * @return hashcode for this matrix
	 */
	@Override
	public int hashCode() {
		long l = 1L;
		l = 31L * l + Double.doubleToLongBits(m00);
		l = 31L * l + Double.doubleToLongBits(m01);
		l = 31L * l + Double.doubleToLongBits(m02);
		l = 31L * l + Double.doubleToLongBits(m10);
		l = 31L * l + Double.doubleToLongBits(m11);
		l = 31L * l + Double.doubleToLongBits(m12);
		l = 31L * l + Double.doubleToLongBits(m20);
		l = 31L * l + Double.doubleToLongBits(m21);
		l = 31L * l + Double.doubleToLongBits(m22);
		return (int)(l ^ l >> 32);
	}

	/**
	 * @return string representation of this matrix
	 */
	@Override
	public String toString() {
		return 
		s(m00) + ", " + s(m01) + ", " + s(m02) + "\n" +
		s(m10) + ", " + s(m11) + ", " + s(m12) + "\n" +
		s(m20) + ", " + s(m21) + ", " + s(m22) + "\n";
	}

	static double s(double v) {
		return Math.abs(v) < 0.0000001 ? 0 : v;
	}

	public void rotate(double angle, double x, double y, double z) {

		double c = Math.cos(angle);
		double s = Math.sin(angle);

		// normalization
		double f = 1 / Math.sqrt(x * x + y * y + z * z);
		x = f * x;
		y = f * y;
		z = f * z;

		Matrix3x3 rot = Matrix3x3.identity();
		rot.m00 = x * x * (1 - c) + c;  
		rot.m01 = x * y * (1 - c) - z * s;    
		rot.m02 = x * z * (1 - c) + y * s;  

		rot.m10 = y * x * (1 - c) + z * s; 
		rot.m11 = y * y * (1 - c) + c; 
		rot.m12 = y * z * (1 - c) - x * s; 

		rot.m20 = x * z * (1 - c) - y * s; 
		rot.m21 = y * z * (1 - c) + x * s; 
		rot.m22 = z * z * (1 - c) + c; 

		multiply(rot);
	}

	/** Scale along each axis independently */
	public void scale(float sx, float sy, float sz) {
		Matrix3x3 scale = Matrix3x3.identity();

		scale.m00 = sx;
		scale.m11 = sy;
		scale.m22 = sz;

		multiply(scale);
	}
	
	public Matrix3x3 subtract(Matrix3x3 m) {
		m00 -= m.m00;
		m01 -= m.m01;
		m02 -= m.m02;

		m10 -= m.m10;
		m11 -= m.m11;
		m12 -= m.m12;

		m20 -= m.m20;
		m21 -= m.m21;
		m22 -= m.m22;

		return this;
	}

	public boolean isZero(double epsilon) {
		return 
		(Math.abs(m00) < epsilon) &&
		(Math.abs(m01) < epsilon) &&
		(Math.abs(m02) < epsilon) &&

		(Math.abs(m10) < epsilon) &&
		(Math.abs(m11) < epsilon) &&
		(Math.abs(m12) < epsilon) &&

		(Math.abs(m20) < epsilon) &&
		(Math.abs(m21) < epsilon) &&
		(Math.abs(m22) < epsilon);
	}

	public void fromEulerAnglesXYZ(double fYAngle, double fPAngle, double fRAngle) {
		double fCos, fSin;

		fCos = Math.cos(fYAngle);
		fSin = Math.sin(fYAngle);
		Matrix3x3 kXMat = new Matrix3x3(1.0,0.0,0.0,0.0,fCos,-fSin,0.0,fSin,fCos);

		fCos = Math.cos(fPAngle);
		fSin = Math.sin(fPAngle);
		Matrix3x3 kYMat = new Matrix3x3(fCos,0.0,fSin,0.0,1.0,0.0,-fSin,0.0,fCos);

		fCos = Math.cos(fRAngle);
		fSin = Math.sin(fRAngle);
		Matrix3x3 kZMat = new Matrix3x3(fCos,-fSin,0.0,fSin,fCos,0.0,0.0,0.0,1.0);

		kYMat.multiply(kZMat);
		kXMat.multiply(kYMat);
		set(kXMat);
	}
}	

