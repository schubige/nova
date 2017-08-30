package ch.digisyn.nova.content;


@SuppressWarnings("nls")
public class BouncingMetaBalls extends Content {

  private static final int NUM = 7;
  private double[][] positions;
  private double[][] speeds;
  private double[] radii;
  private int[] dim;

  public BouncingMetaBalls(int dimI, int dimJ, int dimK, int numFrames) {
    super("BouncingBalls", dimI, dimJ, dimK, numFrames);
    this.positions = new double[NUM][3];
    this.speeds = new double[NUM][3];
    dim = {dimI, dimJ, dimK};
    for (int i=0; i<NUM; i++) {
      for (int j=0; j<3; j++) {
        this.postions[i][j] = FRAND(0,dim[j]);
        this.speeds[i][j] = FRAND(-1,1);
      }
      this.radii[i] = FRAND(1,2.5);
    }
  }

  @Override
  public boolean fillFrame(float[] rgbFrame, double timeInSec) {
    // update positions
    for (int i=0; i<NUM; i++) {
      for (int j=0; j<3; j++) {
        this.postions[i][j] += this.speeds[i][j];
        if (this.postions[i][j]<0 || this.postions[i][j]>dim[j]) {
          this.speeds[i][j] *= -1;
        }
      }
    }

    // update voxel colors
    for (int z=0; z<dimK; z++) {
			for (int y=0; y<dimJ; y++) {
				for (int x=0; x<dimI; x++) {
          double sum = 0;
          for (int i=0; i<NUM; i++) {
            double rad = this.radii[i];
            double dsq = Math.pow((this.postions[i][0]-x),2) +
                         Math.pow((this.postions[i][1]-y),2) +
                         Math.pow((this.postions[i][2]-z),2);
            dsq = Math.sqrt(dsq);
            sum += 100 * rad / dsq;
          }
          double H = CROP_INTERVAL(sum,0,360);
          double C = 1;
          double X = 1- Math.abs(sum/60.0 % 2 - 1);

          // HSV to RGB conversion for fancy rainbow colors
          double r,g,b;
          if (H<60) {
            r = C;
            g = X;
            b = 0;
          } else if (H<120) {
            r = X;
            g = C;
            b = 0;
          } else if (H<180) {
            r = 0;
            g = C;
            b = X;
          } else if (H<240) {
            r = 0;
            g = X;
            b = C;
          } else if (H<300) {
            r = X;
            g = 0;
            b = C;
          } else {
            r = C;
            g = 0;
            b = X;
          }

          setVoxel(rgbFrame, x, y, z, r,g,b);
        }
      }
    }
    return --frames > 0;
  }

  public static double FRAND(double from, double to) {
		return from + (Math.random() * (to - from));
	}

  public static double CROP_INTERVAL(double val, double low, double high) {
		return val < low ? low : (val > high ? high : val);
	}
}
