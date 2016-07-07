package ch.digisyn.nova.content;

import java.util.Collections;
import java.util.List;

/**
 * Content base class for NOVA Server content.
 * 
 * @author sschubiger
 */
public abstract class Content {
	/* name of content. */
	protected final String name;
	/* X-dimension in the horizontal plane of the NOVA hardware, always a multiple of 5. */
	protected final int    dimI;
	/* Y-dimension in the horizontal plane of the NOVA hardware, always a multiple of 5. */
	protected final int    dimJ;
	/* Z-dimension (vertical) of the NOVA hardware, always 10. */
	protected final int    dimK;
	/* number of frames to run. */
	protected final int    numFrames;
	/* number current number of frames. */
	protected       int    frames;
	
	/**
	 * Creates a content instance.
	 * 
	 * @param name The name of the content.
	 * @param dimI The X-dimension.
	 * @param dimJ The Y-dimension.
	 * @param dimK The Z-dimension.
	 * @param numFrames The number of frames to run.
	 */
	protected Content(String name, int dimI, int dimJ, int dimK, int numFrames) {
		this.name      = name;
		this.dimI      = dimI;
		this.dimJ      = dimJ;
		this.dimK      = dimK;
		this.numFrames = numFrames <= 0 ? Integer.MAX_VALUE : numFrames;
	}
	
	/**
	 * Utility function to set a RGB values of a voxel at position (i,j,k).
	 * @param rgbFrame The voxel frame to operate on.
	 * @param i The X-position.
	 * @param j The Y-position.
	 * @param k The Z-position.
	 * @param r The red value.
	 * @param g The green value.
	 * @param b The blue value.
	 */
	protected void setVoxel(float[] rgbFrame, int i, int j, int k, float r, float g, float b) {
		final int idx = 3 * (k + (dimK * (i + j * dimI)));
		rgbFrame[idx+0] = r;
		rgbFrame[idx+1] = g;
		rgbFrame[idx+2] = b;
	}

	/**
	 * Utility function to add RGB values of a voxel at position (i,j,k) to rgbFrame.
	 * @param rgbFrame The voxel frame to operate on.
	 * @param i The X-position.
	 * @param j The Y-position.
	 * @param k The Z-position.
	 * @param r The red value.
	 * @param g The green value.
	 * @param b The blue value.
	 */
	protected void addVoxel(float[] rgbFrame, int i, int j, int k, float r, float g, float b) {
		final int idx = 3 * (k + (dimK * (i + j * dimI)));
		rgbFrame[idx+0] += r;
		rgbFrame[idx+1] += g;
		rgbFrame[idx+2] += b;
	}

	static final float SCALE = 5f;
	/**
	 * Utility function to set a weighted RGB values of a voxel at position (i,j,k).
	 * @param rgbFrame The voxel frame to operate on.
	 * @param i The X-position.
	 * @param j The Y-position.
	 * @param k The Z-position.
	 * @param r The red value.
	 * @param g The green value.
	 * @param b The blue value.
	 * @param w The weight.
	 */
	protected void setVoxel(float[] rgbFrame, int i, int j, int k, float r, float g, float b, float w) {
		final int idx  = 3 * (k + (dimK * (i + j * dimI)));
		w *= SCALE;
		final float w1 = 1 -w;
		rgbFrame[idx+0] = r * w + w1 * rgbFrame[idx+0];
		rgbFrame[idx+1] = g * w + w1 * rgbFrame[idx+1];
		rgbFrame[idx+2] = b * w + w1 * rgbFrame[idx+2];
	}

	/**
	 * Utility function to set a weighted RGB values of a voxel at position (i,j,k).
	 * @param rgbFrame The voxel frame to operate on.
	 * @param i The X-position.
	 * @param j The Y-position.
	 * @param k The Z-position.
	 * @param r The red value.
	 * @param g The green value.
	 * @param b The blue value.
	 * @param wr The red weight.
	 * @param wg The green weight.
	 * @param wb The blue weight.
	 */
	protected void setVoxel(float[] rgbFrame, int i, int j, int k, float r, float g, float b, float wr, float wg, float wb) {
		final int idx  = 3 * (k + (dimK * (i + j * dimI)));
		wr *= SCALE;
		wg *= SCALE;
		wb *= SCALE;
		rgbFrame[idx+0] = r * wr + (1-wr) * rgbFrame[idx+0];
		rgbFrame[idx+1] = g * wg + (1-wg) * rgbFrame[idx+1];
		rgbFrame[idx+2] = b * wb + (1-wb) * rgbFrame[idx+2];
	}

	/**
	 * Called when a content is activated.
	 */
	public void start() {frames = numFrames;}
	/**
	 * Called when a content is deactivated.
	 */
	public void stop() {}
	
	/**
	 * Request a voxel frame to be filled. Must complete in less than 40ms in order to keep up with the 
	 * 25 Hz frame rate of the display.
	 * 
	 * @param rgbFrame The voxel frame to operate on.
	 * @param timeInSec The relative animation time starting from 0.
	 * @return True if this content has more frames avilable or fals if the server should switch to the next content.
	 */
	public abstract boolean fillFrame(float[] rgbFrame, double timeInSec);
		
	/**
	 * Returns the name of the content.
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Called by the server to get a list of content instances.
	 * 
	 * @return The list of content instances. Usually just this instance.
	 */
	public List<Content> getContents() {
		return Collections.singletonList(this);
	}
}
