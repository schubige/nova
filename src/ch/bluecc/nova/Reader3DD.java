package ch.bluecc.nova;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class Reader3DD extends Common3DD {
	private Header   header;
	Frame[]          frames;
	File             file;

	public Reader3DD(File f) throws IOException {
		this(f, 50, 50, 10, false);
	}

	public Reader3DD(File f, int ddimX, int ddimY, int ddimZ) throws IOException {
		this(f, ddimX, ddimY, ddimZ, false);
	}
	
	@SuppressWarnings("nls")
	public Reader3DD(File f, int ddimX, int ddimY, int ddimZ, boolean overwrite) throws IOException {
		this.file    = f;

		boolean cropX = ddimX < 0; ddimX = Math.abs(ddimX);
		boolean cropY = ddimY < 0; ddimY = Math.abs(ddimY);
		boolean cropZ = ddimZ < 0; ddimZ = Math.abs(ddimZ);
		
		int dimX = 50;
		int dimY = 50;
		int dimZ = 10;
		
		{
			RandomAccessFile     inf = new RandomAccessFile(f, "r");
			FileChannel          in  = inf.getChannel();
			MappedByteBuffer  buffer = in.map(MapMode.READ_ONLY, 0, in.size());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			header = new Header(buffer);
			System.out.println(header.toString());
			in.close();
			inf.close();
			
			dimX = header.dimX;
			dimY = header.dimY;
			dimZ = header.dimZ;
		}

		
		File rawFile = new File(f.getParentFile(), f.getName() + "_"+ddimX+"x"+ddimY+"x"+ddimZ+".raw");
		if(!(overwrite) && rawFile.exists() && rawFile.lastModified() > f.lastModified()) {
			int n = (int)rawFile.length() / (dimX * dimY * 10 * 3);
			header = new Header(rawFile, n);
			frames = new Frame[header.numberOfImages];
			for(int i = 0; i < frames.length; i++)
				frames[i] = new Frame(header, i);
		} else {
			System.out.println("Converting " + f + 
					" x:" +dimX + "->" + ddimX + (cropX ? "c" : "") + 
					" y:"+ dimY + "->" + ddimY + (cropY ? "c" : "") +
					" z:"+ dimZ + "->" + ddimZ + (cropZ ? "c" : ""));

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(rawFile), 8 * 1024 * 1024);
			RandomAccessFile     inf = new RandomAccessFile(f, "r");
			FileChannel          in  = inf.getChannel();
			MappedByteBuffer  buffer = in.map(MapMode.READ_ONLY, 0, in.size());
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			header = new Header(buffer);

			frames = new Frame[header.numberOfImages];

			byte[] dst = new byte[ddimX*ddimY*ddimZ*3];
			for(int fn = 0; fn < frames.length; fn++) {
				frames[fn] = new Frame(buffer, header, fn);
				byte[] src = frames[fn].getFrame();
				for(int k = 0; k < ddimZ; k++)
					for(int j = 0; j < ddimY; j++)
						for(int i = 0; i < ddimX; i++) {
							final int didx = 3 * (k + (ddimZ * (i + j * ddimX)));
							final int si   = cropX ? i : ((dimX * i) / ddimX);
							final int sj   = cropY ? j : ((dimY * j) / ddimY);
							final int sk   = cropZ ? k : ((dimZ * k) / ddimZ);
							final int sidx = 3 * (sk + (dimZ * (si + sj * dimX)));
							dst[didx+0] = src[sidx+0];
							dst[didx+1] = src[sidx+1];
							dst[didx+2] = src[sidx+2];
						}
				out.write(dst);
			}

			out.close();
			in.close();
			inf.close();
		}
	}

	public byte[] getFrame(int i) {
		return frames[i].getFrame();
	}

	public int getNumFrames() {
		return frames.length;
	}

	@Override
	public String toString() {
		return header.toString();
	}

	public int getDimX() {
		return header.dimX;
	}

	public int getDimY() {
		return header.dimY;
	}

	public int getDimZ() {
		return header.dimZ;
	}

	public File getFile() {
		return file;
	}
}
