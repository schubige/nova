package ch.digisyn.nova.content;


@SuppressWarnings("nls")
public class Test extends Content {	
	public Test(int dimI, int dimJ, int dimK, int numFrames) {
		super("Test", dimI, dimJ, dimK, numFrames);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
        float speed = 1;
		for(int i = 0; i < dimI; i++) {
			float v = (int)(timeInSec / speed % dimI) == i ? 1f : 0f;
			for(int k = 0; k < dimK; k++) {
				for(int j = 0; j < dimJ; j++) {
					setVoxel(rgbFrame, i, j, k, v, v, v);
                }
            }
		}
		
		return --frames > 0;
	}

}
