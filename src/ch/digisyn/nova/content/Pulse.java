package ch.digisyn.nova.content;

import java.util.Arrays;

public class Pulse extends Content {
	
	public Pulse(int dimI, int dimJ, int dimK, int numFrames) {
		super("Pulse", dimI, dimJ, dimK, numFrames);
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		float v= (float)Math.sin(timeInSec);
		Arrays.fill(rgbFrame, v * v);
		return --frames > 0;
	}

}
