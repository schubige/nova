package ch.digisyn.nova.content;

import java.util.Arrays;

@SuppressWarnings("nls")
public class Solid extends Content {

	public Solid(int dimI, int dimJ, int dimK, int numFrames) {
		super("Solid", dimI, dimJ, dimK, numFrames);
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 1f);
		return --frames > 0;
	}

}
