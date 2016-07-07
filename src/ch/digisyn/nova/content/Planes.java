package ch.digisyn.nova.content;


public class Planes extends Content {
	
	public Planes(int dimI, int dimJ, int dimK, int numFrames) {
		super("Planes", dimI, dimJ, dimK, numFrames);
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		int t0 = (int) (timeInSec*3);
		int t1 = (int) (timeInSec*4);
		int t2 = (int) (timeInSec*7);
		
		int x = Math.abs((t0 % ((dimI*2)-1))-(dimI-1));
		int y = Math.abs((t1 % ((dimJ*2)-1))-(dimJ-1));
		int z = Math.abs((t2 % ((dimK*2)-1))-(dimK-1));
		
		for(int i = 0; i < rgbFrame.length; i++)
			rgbFrame[i] *= 0.5f;
		
		for(int j = 0; j < dimJ; j++)
			for(int k = 0; k < dimK; k++)
				addVoxel(rgbFrame, x, j, k, 1f, 0, 0);
		
		for(int i = 0; i < dimI; i++)
			for(int k = 0; k < dimK; k++)
				addVoxel(rgbFrame, i, y, k, 0, 1f, 0);

		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++)
				addVoxel(rgbFrame, i, j, z, 0, 0, 1f);

		return --frames > 0;
	}
}
