package ch.bluecc.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Stars extends Content {	
	java.util.Random rnd   = new java.util.Random();
    Star[]           stars = new Star[10];
    
	public Stars(int dimI, int dimJ, int dimK, int numFrames) {
		super("Stars", dimI, dimJ, dimK, numFrames);
		
		for(int i = 0; i < stars.length; i++)
			stars[i] = new Star();
	}

    static double pdf(double x, double m, double sd) {
        double a = 1.0 / (Math.sqrt(2.0 * Math.PI) * sd);
        double b = -(x - m) * (x - m) / (2.0 * sd * sd);

        return a * Math.exp(b);
    }
    
    class Star {
    	static final int SIZE = 5;
    	
    	double  startTime;
    	int     x;
    	int     y; 
    	int     z;
    	float[] rgb = new float[3];
    	double  ts;
    	
    	Star() {
    		init(0);
    	}
    	
    	void init(double now) {
    		x         = rnd.nextInt(dimI);
    		y         = rnd.nextInt(dimJ);
    		z         = rnd.nextInt(dimK);
    		setRGBfromHSV(rnd.nextFloat(), rnd.nextFloat(), 1f, rgb, 0);
    		startTime = now + rnd.nextDouble() * 10.0;
    		ts        = 0.5 + rnd.nextDouble() * 2.0;
    	}
    	
        void fillFrame(float[] rgbFrame, double t) {
        	double now = t;
        	t -= startTime;
        	if(t > 6 * ts) {
        		init(now);
        		return;
        	}
        	double sd = 2;
        	float  sc = (float)pdf(t * ts, 3, 0.2) * 2f;
        	for(int i = -SIZE; i <= SIZE; i++) {
        		final int xi = x + i;
        		if(xi < 0 || xi >= dimI) continue;
        		final float v = (float)pdf(i, 0, sd) * sc;
        		addVoxelClamp(rgbFrame, xi, y, z, v * rgb[0], v * rgb[1], v * rgb[2]);    		
        	}
           	for(int j = -SIZE; j <= SIZE; j++) {
        		final int yj = y + j;
        		if(yj < 0 || yj >= dimJ) continue;
        		final float v = (float)pdf(j, 0, sd) * sc;
           	 	addVoxelClamp(rgbFrame, x, yj, z, v * rgb[0], v * rgb[1], v * rgb[2]);
           	}
           	for(int k = -SIZE; k <= SIZE; k++) {
        		final int zk = z + k;
        		if(zk < 0 || zk >= dimK) continue;
        		final float v = (float)pdf(k, 0, sd) * sc;
           	   	addVoxelClamp(rgbFrame, x, y, zk, v * rgb[0], v * rgb[1], v * rgb[2]);
           	}
        }    	
    }
        
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		
		for(int i = 0; i < stars.length; i++)
			stars[i].fillFrame(rgbFrame, timeInSec);
		
		return --frames > 0;
	}

}
