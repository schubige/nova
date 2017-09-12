package ch.digisyn.nova.content;


@SuppressWarnings("nls")
public class Fourier extends Content {
	
	int nx, ny, nz;
	int num = 4;
	double[] params, par2;
	public Fourier(int dimI, int dimJ, int dimK, int numFrames) {
		super("Fourier", dimI, dimJ, dimK, numFrames);
		nx = dimI;
		ny = dimJ;
		nz = dimK;
		
		params = new double[num];
		for (int i=0; i<num; i++) {
			params[i] = 1+(Math.random()-0.5)*2;
		}
		par2 = new double[num];
		for (int i=0; i<num; i++) {
			par2[i] = (Math.random()-0.5)*2;
		}
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		double d = Math.sin(timeInSec);
		for (int x = 0; x<nx; x++) {
			for (int y = 0; y<ny; y++) {
				for (int z = 0; z<nz; z++) {
					float v = 0;
					for (int i=0; i<num; i++) {
						v += par2[i]*Math.sin(y*((i+1)*0.2) + timeInSec*params[i]);
					}
					v /= 1.;
					float f = (float) (1 / (1 + Math.exp(v-(z-nz/2)))); // sigmoid
					f = 1-f;
					setVoxel(rgbFrame, x, y, z, 0,f/2,f);
					
				}
			}
		}
		return --frames > 0;
	}

}
