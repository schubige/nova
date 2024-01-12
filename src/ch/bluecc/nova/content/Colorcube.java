package ch.bluecc.nova.content;

import org.corebounce.util.Matrix3x3;
import org.corebounce.util.Vector3D;

import ch.bluecc.nova.Content;
import ch.bluecc.nova.NOVAControl;

@SuppressWarnings("nls")
public class Colorcube extends Content {
	static final double SPEED_FACTOR = 2;

	private double[] mScale                = new double[4];
	private double[] mRotation             = new double[4];
	private double[] mPosition             = new double[4];

	private double[] mScaleSpeed           = new double[4];
	private double[] mRotationSpeed        = new double[4];
	private double[] mPositionSpeed        = new double[4];

	private double[] mScaleAcceleration    = new double[4];
	private double[] mRotationAcceleration = new double[4];
	private double[] mPositionAcceleration = new double[4];

	private double[]   mMaxAccChange        = new double[3];
	private double[][] mAccBound            = new double[3][2];
	private double[][] mSpeedBound          = new double[3][2];
	private double[][] mValBound            = new double[3][2];

	private double  mLastSpeed = Double.MAX_VALUE;
	
	public Colorcube(int dimI, int dimJ, int dimK, int numFrames) {
		super("Colorcube", dimI, dimJ, dimK, numFrames);

		for (int i=0; i<3; i++) {
			mScale[i] = FRAND(0.25, 0.75);
			mRotation[i] = -Math.PI + FRAND(0, Math.PI);
			mPosition[i] = FRAND(0,1);

			mScaleSpeed[i] = -0.1 + FRAND(0,0.2);
			mRotationSpeed[i] = -0.1 + FRAND(0,0.2);
			mPositionSpeed[i] = -0.1 + FRAND(0,0.2);

			mScaleAcceleration[i] = -0.01 + FRAND(0,0.02);
			mRotationAcceleration[i] = -0.01 + FRAND(0,0.02);
			mPositionAcceleration[i] = -0.01 + FRAND(0,0.02);
		}
	}
		
	private void updateSpeed() {
		double speed = NOVAControl.getSpeed();
		if(mLastSpeed == speed) return;
		
		mLastSpeed = speed;
		
		speed = Math.pow(2, speed);
		
		// scale
		mMaxAccChange[0] = 0.00001*SPEED_FACTOR*speed;			
		mAccBound[0][0] = -0.0001*SPEED_FACTOR*speed;
		mAccBound[0][1] = 0.0001*SPEED_FACTOR*speed;
		mSpeedBound[0][0] = -0.001*SPEED_FACTOR*speed;
		mSpeedBound[0][1] = 0.001*SPEED_FACTOR*speed;
		mValBound[0][0] = 0.3;
		mValBound[0][1] = 1.5;

		// rotation
		mMaxAccChange[1] = 0.000001*SPEED_FACTOR*speed;		
		mAccBound[1][0] = -0.00001*SPEED_FACTOR*speed;
		mAccBound[1][1] = 0.00001*SPEED_FACTOR*speed;
		mSpeedBound[1][0] = -0.0001*SPEED_FACTOR*speed;
		mSpeedBound[1][1] = 0.0001*SPEED_FACTOR*speed;
		mValBound[1][0] = -Math.PI;
		mValBound[1][1] = Math.PI;

		// position
		mMaxAccChange[2] = 0.00001*SPEED_FACTOR*speed;		
		mAccBound[2][0] = -0.0001*SPEED_FACTOR*speed;
		mAccBound[2][1] = 0.0001*SPEED_FACTOR*speed;
		mSpeedBound[2][0] = -0.001*SPEED_FACTOR*speed;
		mSpeedBound[2][1] = 0.001*SPEED_FACTOR*speed;
		mValBound[2][0] = -1;
		mValBound[2][1] = 1;
	}

	private final Vector3D _p = new Vector3D();
	private final Matrix3x3 s = new Matrix3x3();
	private final Matrix3x3 r = new Matrix3x3();
	private final Matrix3x3 m = new Matrix3x3();
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		updateSpeed();
		
		s.setRowFirst(mScale[0], 0, 0, 0, mScale[1], 0, 0, 0, mScale[2]);
		r.setIdentity();
		r.fromEulerAnglesXYZ(mRotation[0], mRotation[1], mRotation[2]);
		Vector3D t  = new Vector3D(mPosition[0], mPosition[1], mPosition[2]);

		m.setIdentity();

		m.multiply(r);
		m.multiply(s);

		for (int z=0; z<dimK; z++) {
			for (int y=0; y<dimJ; y++) {
				for (int x=0; x<dimI; x++) {
					// scale to [0..1]
					_p.set(x/(double)dimI,y/(double)dimJ, z/(double)dimK);
					// rotate, zoom and translate
					Vector3D _t = m.multiply(_p).add(t);

					// 
					for (int i=0; i<3; i++) {
						while (_t.c[i]<=0) _t.c[i]+=2;
						while (_t.c[i]>2) _t.c[i]-=2;
						// now in ]0..2]
						if (_t.c[i]>1)
							_t.c[i] = 2-_t.c[i];
					}
					// in [0..1]
					setVoxel(rgbFrame, x, y, z, (float)_t.c[0], (float)_t.c[1], (float)_t.c[2]);
				}
			}
		}

		for (int i=0; i<3; i++) {
			mScaleAcceleration[i] += -mMaxAccChange[0] + FRAND(0,2*mMaxAccChange[0]);
			mScaleAcceleration[i] = CROP_INTERVAL(mScaleAcceleration[i], mAccBound[0][0], mAccBound[0][1]);
			mRotationAcceleration[i] += -mMaxAccChange[1] + FRAND(0,2*mMaxAccChange[1]);
			mRotationAcceleration[i] = CROP_INTERVAL(mRotationAcceleration[i], mAccBound[1][0], mAccBound[1][1]);
			mPositionAcceleration[i] += -mMaxAccChange[2] + FRAND(0,2*mMaxAccChange[2]);
			mPositionAcceleration[i] = CROP_INTERVAL(mPositionAcceleration[i], mAccBound[2][0], mAccBound[2][1]);

			mScaleSpeed[i] += mScaleAcceleration[i];
			mScaleSpeed[i] = CROP_INTERVAL(mScaleSpeed[i], mSpeedBound[0][0], mSpeedBound[0][1]);
			mRotationSpeed[i] += mRotationAcceleration[i];
			mRotationSpeed[i] = CROP_INTERVAL(mRotationSpeed[i], mSpeedBound[1][0], mSpeedBound[1][1]);
			mPositionSpeed[i] += mPositionAcceleration[i];
			mPositionSpeed[i] = CROP_INTERVAL(mPositionSpeed[i], mSpeedBound[2][0], mSpeedBound[2][1]);

			mScale[i] += mScaleSpeed[i];
			mScale[i] = CROP_INTERVAL(mScale[i], mValBound[0][0], mValBound[0][1]);
			mRotation[i] += mRotationSpeed[i];
			mScale[i] = CROP_INTERVAL(mScale[i], mValBound[1][0], mValBound[1][1]);
			mPosition[i] += mPositionSpeed[i];
			mPosition[i] = CROP_INTERVAL(mPosition[i], mValBound[2][0], mValBound[2][1]);
		}	
		
		return --frames > 0;
	}

	public static double CROP_INTERVAL(double val, double low, double high) {
		return val < low ? low : (val > high ? high : val);
	}

	public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}

}
