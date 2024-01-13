package ch.bluecc.nova;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.corebounce.util.Log;

@SuppressWarnings("nls")
public class Common3DD {
	private static RandomAccessFile currentRA;
	private static File             currentFile;

	public static class Header{
		char[] checksum = new char[6]; // a checksum
		int    version;     		   // the version field
		short  dimX;		           // size of the images in x direction
		short  dimY;			       // size of the images in y direction
		short  dimZ;			       // size of the images in z direction
		int    numberOfImages;	       // number of frames in the file
		File   raw;                    // raw file

		public Header(MappedByteBuffer in) {
			for(int i = 0; i < 6; i++)
				checksum[i] = (char)(in.get() & 0xFF);
			in.getShort(); // alignemnt
			version        = in.getInt();
			dimX           = in.getShort();
			dimY           = in.getShort();
			dimZ           = in.getShort();
			in.getShort(); // alignment
			numberOfImages = in.getInt();
		}

		public Header(int dimX, int dimY, int dimZ) {
			checksum = new char[] {'3','D','D','v','1',0};
			version  = 2;
			this.dimX = (short) dimX;
			this.dimY = (short) dimY;
			this.dimZ = (short) dimZ;
		}

		// default header
		public Header(File raw, int numberOfImages) {
			this.dimX = 50;
			this.dimY = 50;
			this.dimZ = 10;
			this.numberOfImages = numberOfImages;
			this.raw  = raw;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for(char c : checksum)
				result.append(c);
			result.append(":" + version);
			result.append(":" + dimX + "x" + dimY + "x" + dimZ);
			result.append("#" + numberOfImages);
			return result.toString();
		}

		public void write(FileOutputStream out) throws IOException {
			ByteBuffer buffer = ByteBuffer.allocate(24);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			for(int i = 0; i < 8; i++)
				buffer.put(i < checksum.length ? (byte)checksum[i] : 0);
			buffer.putInt(version);
			buffer.putShort(dimX);
			buffer.putShort(dimY);
			buffer.putShort(dimZ);
			buffer.putShort((short)0);
			buffer.putInt(numberOfImages);
			out.write(buffer.array());
		}		
	}

	public static final class Frame {
		private int               start;
		private int               size;
		private MappedByteBuffer  in;
		private Header            header;
		private Reference<byte[]> frame;
		private byte[]            frameData;
		private int               frameNo;

		Frame(MappedByteBuffer in, Header header, int frameNo) {
			this.in      = in;
			this.header  = header;
			this.frameNo = frameNo;
			this.size    = in.getInt();
			this.start   = in.position();
			in.position(start + size);
		}

		public Frame(Header header, int frameNo) {
			this.header  = header;
			this.frameNo = frameNo;
			this.start   = frameNo * header.dimX * header.dimY * header.dimZ * 3;
		}

		public int getFrameNo() {
			return frameNo;
		}

		public byte[] getFrame() {
			byte[] frameArray = null;

			try {
				if(frameData != null)
					return frameData;
				else if(frame == null || frame.get() == null) {
					frameArray = new byte[header.dimX * header.dimY * header.dimZ * 3];
					frame      = new SoftReference<>(frameArray);

					if(header.raw != null) {
						RandomAccessFile in = getRAFile(header.raw);
						in.seek(start);
						in.readFully(frameArray);
					} else {
						byte[] buffer = new byte[size];						
						in.position(start);
						in.get(buffer);
						Inflater inflater = new Inflater();
						inflater.setInput(buffer);
						inflater.inflate(frameArray);
						buffer   = null;
						inflater = null;
					}
				} else
					frameArray = frame.get(); 
			} catch(Throwable t)  {
				Log.severe(t);
			}
			return frameArray;
		}

		public void setFrame(byte[] buffer) {
			frameData = buffer;
		}

		public void write(FileOutputStream out) throws IOException {
			Deflater deflater = new Deflater();
			byte[] frame  = getFrame();
			byte[] buffer = new byte[frame.length * 2];
			deflater.setInput(frame);
			deflater.finish();
			int totalCount = 0;
			for(;;) {
				int count = deflater.deflate(buffer, totalCount, buffer.length - totalCount);
				if(count == 0 && deflater.needsInput()) break;
				totalCount += count;
			}
			out.write(totalCount);
			out.write(totalCount >> 8);
			out.write(totalCount >> 16);
			out.write(totalCount >> 24);
			out.write(buffer, 0, totalCount);
		}

		public Frame getSubframe(int dimX, int dimY, int dimZ) {
			Frame result = new Frame(new Header(dimX, dimY, dimZ), getFrameNo());
			byte[] buffer = new byte[dimX*dimY*dimZ*3];
			setFrame(buffer);
			return result;
		}
	}

	static RandomAccessFile getRAFile(File f) throws IOException {
		if(!f.equals(currentFile)) {
			if(currentRA != null)
				currentRA.close();
			currentRA = new RandomAccessFile(f, "r");
		}
		return currentRA;
	}
}
