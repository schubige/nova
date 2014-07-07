package ch.digisyn.nova.content;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Movie extends Content {	
	private       byte[]        buffer;
	private final List<Content> contents;
	private final File          file;
	private double              startTime;

	public Movie(int dimI, int dimJ, int dimK, int numFramesIgnored) {
		super("Movie", dimI, dimJ, dimK, 0);

		file     = null;
		contents = new ArrayList<Content>();

		try {
			FileFilter fileFilter = new FileFilter() {
				public boolean accept(File file) {
					return ! file.isDirectory();
				}
			};

			for(File file : new File("movies").listFiles(fileFilter))
				contents.add(new Movie(this, file));
		} catch(Throwable e) {
		}
	}

	private Movie(Movie parent, File file) {
		super(file.getName().substring(0, file.getName().indexOf('.')).replace('_', ' '), parent.dimI, parent.dimJ, parent.dimK, numFrames(parent, file));
		this.file     = file;
		this.contents = parent.contents;
	}

	private static int numFrames(Movie parent, File file) {
		return (int) (file.length() / (long)(parent.dimI * parent.dimJ * parent.dimK * 3));
	}

	@Override
	public void start() {
		System.out.println("Playing " + this);
		startTime = -1.0;
		if(buffer == null) {
			final byte[] lbuffer = new byte[(int)file.length()];
			buffer = lbuffer;
			new Thread()  {
				public void run() {
					try {
						RandomAccessFile f = new RandomAccessFile(file, "r");
						try {
							buffer = lbuffer;
							f.readFully(lbuffer);
						} finally {
							f.close();
						}
					} catch(Throwable t) {
						t.printStackTrace();
					}
				}
			}.start();
		}
	}

	@Override
	public void stop() {
		System.out.println("Stopping " + this);
		buffer = null;
	}

	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		if(buffer == null) return false;

		if(startTime < 0) startTime = timeInSec;
		timeInSec -= startTime;

		final int frameNo = (int)(timeInSec * 25.0);
		final int frameSz = dimI * dimJ * dimK * 3;
		final int offset  = (frameNo * frameSz);

		if(offset < buffer.length) {
			for(int k = 0; k < dimK; k++)
				for(int i = 0; i < dimI; i++)
					for(int j = 0; j < dimJ; j++) {
						final int pos = offset + (dimI * 3 * dimJ * k + dimI * 3 * j + 3 * i);
						setVoxel(rgbFrame, i, j, k, (buffer[pos] & 0xFF) / 255.0f, (buffer[pos+1] & 0xFF) / 255.0f, (buffer[pos+2] & 0xFF) / 255.0f);
					}

			return true;
		} else {
			buffer = null;
			return false;
		}
	}

	@Override
	public List<Content> getContents() {
		return contents;
	}
}
