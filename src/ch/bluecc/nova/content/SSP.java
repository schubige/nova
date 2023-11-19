package ch.bluecc.nova.content;

import java.util.Arrays;

public class SSP extends Content {
	static final float _ = 0f;
	static final float x = .5f;
	static final float X = 1f;
	
	private static final float[][] FONT = {
			{},
			{
				_,X,X,X,X,
				X,x,x,x,x,
				X,x,_,_,_,
				X,x,_,_,_,
				x,X,X,X,x,
				_,x,x,x,X,
				_,_,_,x,X,
				_,_,_,x,X,
				x,x,x,x,X,
				X,X,X,X,_,
			},
			{
				X,X,X,X,_,
				x,x,x,x,X,
				_,_,_,x,X,
				_,_,_,x,X,
				X,X,X,X,x,
				X,x,x,x,_,
				X,x,_,_,_,
				X,x,_,_,_,
				X,x,_,_,_,
				X,x,_,_,_,
			},
			{},
			{},
			{},
	};

	private static final double DT = 0.7;
	float[] rgb = {0,0,0};

	public SSP(int dimI, int dimJ, int dimK, int numFrames) {
		super("SSP", dimI, dimJ, dimK, numFrames); //$NON-NLS-1$		
		FONT[0] = FONT[1];
		FONT[3] = FONT[0];
		FONT[4] = FONT[1];
		FONT[5] = FONT[2];
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		setRGBfromHSV((float)(timeInSec * 0.1), 1f, 1f, rgb, 0);
		
		final int ch = (int)((long)(timeInSec / (Math.PI * 2)) % 6);
		timeInSec -= Math.PI;
		for(int i = 0; i < dimI; i++) {
			for(int j = 0; j < dimJ; j++) {
				for(int k = 0; k < dimK; k++) {
					final double t = ch >= 3 ? timeInSec+(i*DT) : timeInSec+((dimJ-j-1)*DT);  
					final float  w = (float)(Math.sin(t));
					final float  v = ch >= 3 ? 
							w * FONT[ch][(dimK-k-1)*dimJ+(dimJ-j-1)] :
							w * FONT[ch][(dimK-k-1)*dimI+(dimI-i-1)];
					if(v > 0)
						setVoxel(rgbFrame, i, j, k, rgb[0] * v, rgb[1] * v, rgb[2] * v);
				}
			}			
		}
		return --frames > 0;
	}
}
