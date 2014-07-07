package ch.digisyn.nova.content;

public class Random extends Content {
	long    last;
	float[] frame = new float[0];
	
	public Random(int dimI, int dimJ, int dimK, int numFrames) {
		super("Random", dimI, dimJ, dimK, numFrames);
	}

	private void randomize() {
		for(int i = 0; i < frame.length; i++)
			frame[i] = (float) Math.random();
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		if(rgbFrame.length != frame.length) {
			frame = new float[rgbFrame.length];
			last  = -1;
		}
		if(last != (long)timeInSec) {
			randomize();
			last = (long) timeInSec;
		}
		System.arraycopy(frame, 0, rgbFrame, 0, frame.length);
		return --frames > 0;
	}
}
