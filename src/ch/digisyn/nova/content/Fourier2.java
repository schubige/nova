package ch.digisyn.nova.content;


@SuppressWarnings("nls")
public class Fourier2 extends Content {
	
	int nx, ny, nz;
	int num = 4;
	double[] params, par2;
	public Fourier2(int dimI, int dimJ, int dimK, int numFrames) {
		super("Fourier2", dimI, dimJ, dimK, numFrames);
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
						v += par2[i]*Math.cos((y-ny/2)*((i+1)*0.2)) * Math.sin(timeInSec*params[i]);
					}
					float vx = 0;
					for (int i=0; i<num; i++) {
						vx += par2[3-i]*Math.cos((x-nx/2)*((i+1)*0.2)) * Math.sin(timeInSec*params[i]);
					}
					float vz = 0;
					for (int i=0; i<num; i++) {
						vz += par2[i]*Math.cos((z-nz/2)*((i+1)*0.2)) * Math.sin(timeInSec*params[3-i]);
					}
//					v *= vx;
//					v *= vz;
//					v /= 1.;
					float f = Math.abs(v); //(float) (1 / (1 + Math.exp(v))); // sigmoid
//					f = 3-f;
					setVoxel(rgbFrame, x, y, z, v,vx,vz);
					
				}
			}
		}
		return --frames > 0;
	}

}
