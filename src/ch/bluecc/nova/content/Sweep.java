package ch.bluecc.nova.content;


@SuppressWarnings("nls")
public class Sweep extends Content {	
	public Sweep(int dimI, int dimJ, int dimK, int numFrames) {
		super("Sweep", dimI, dimJ, dimK, numFrames);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		final double dimK_1 = dimK - 1;
		for(int k = 0; k < dimK; k++) {
			double dk    = k / dimK_1; 
			float  v     = (float)Math.abs(Math.sin(dk * Math.PI + timeInSec));
			for(int i = 0; i < dimI; i++)
				for(int j = 0; j < dimJ; j++)
					setVoxel(rgbFrame, i, j, k, v, v, v);
		}
		return --frames > 0;
	}

}
