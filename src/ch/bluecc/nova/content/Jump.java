package ch.bluecc.nova.content;

import ch.bluecc.nova.Content;

@SuppressWarnings("nls")
public class Jump extends Content {	
	public Jump(int dimI, int dimJ, int dimK, int numFrames) {
		super("Jump", dimI, dimJ, dimK, numFrames);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		final double dimK_2 = dimK / 2;
		final int    dimI_1 = dimI - 1;
		final int    dimJ_1 = dimJ - 1;
		final double t      = Math.PI * timeInSec;
		for(int k = 0; k < dimK; k++) {
			float v0  = k == dimK_2 + (int)(Math.sin(t) * (dimK_2) - 0.5) ? 1 : 0;
			float v1  = k == dimK_2 + (int)(Math.sin(t+Math.PI) * (dimK_2) - 0.5) ? 1 : 0;
			for(int i = 0; i < dimI; i++)
				for(int j = 0; j < dimJ; j++) {
					if(i==0 || j==0 || i == dimI_1 || j == dimJ_1)
						setVoxel(rgbFrame, i, j, k, v0, v0, v0, 0.05f, 0.05f, 0.1f);
					else
						setVoxel(rgbFrame, i, j, k, v1, v1, v1, 0.05f, 0.1f, 0.05f);
				}
		}
		return --frames > 0;
	}

}
