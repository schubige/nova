package org.corebounce.nova.content;

import org.corebounce.nova.Content;

@SuppressWarnings("nls")
public class Random extends Content {
	long    last;
	float[] frame = new float[0];
	
	public Random(int dimI, int dimJ, int dimK, int numFrames) {
		super("Random", dimI, dimJ, dimK, numFrames);
	}

	private void randomize() {
		for(int i = 0; i < frame.length; i++)
			frame[i] += (float) (Math.random() * 0.2);
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		if(rgbFrame.length != frame.length) {
			frame = new float[rgbFrame.length];
			last  = -1;
			for(int i = 0; i < frame.length; i++)
				frame[i] = (float) (Math.random() * Math.PI * 2);
		}
		long t = (long)(timeInSec*10);
		if(last != t) {
			randomize();
			last = t;
		}
		for(int i = 0; i < frame.length; i++) {
			float v = (float) Math.sin(frame[i]);
			rgbFrame[i] = 1 - (v * v);
		}
		return --frames > 0;
	}
}
