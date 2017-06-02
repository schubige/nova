package org.corebounce.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

/**
 * Various io utilities.
 * 
 * (c) 2000 - 2004 IIUF, DIUF, corebounce, swisscom innovations
 * <p>
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class Utilities {
	static final int BUFFER_SZ = 8192;

	public static boolean isLocked(File file) throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException(file.getPath());
		if (file.lastModified() + 2000 > System.currentTimeMillis())
			return true;
		if (file.canWrite()) {
			try {
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.close();
				return false;
			} catch (Exception e) {
			}
		}
		return true;
	}

	public static void moveFile(File from, File to, boolean deleteToIfExists) throws IOException {
		moveFile(from, to, deleteToIfExists, false);
	}

	public static void moveFile(File from, File to, boolean deleteToIfExists, boolean createDirs) throws IOException {
		if (deleteToIfExists && to.exists())
			to.delete();
		if (createDirs)
			to.getParentFile().mkdirs();
		if (!from.renameTo(to)) {
			copy(from, to);
			from.delete();
		}
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source reader.
	 * @param dest
	 *            The destination writer.
	 */
	public static long copy(Reader source, Writer dest) throws IOException {
		return copy(source, dest, true);
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source file.
	 * @param dest
	 *            The destination file.
	 */
	public static long copy(File source, File dest) throws IOException {
		return copy(new FileInputStream(source), new FileOutputStream(dest));
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source stream.
	 * @param dest
	 *            The destination stream.
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException {
		return copy(in, out, true);
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source stream.
	 * @param dest
	 *            The destination stream.
	 * @param close
	 *            Close both stream when done.
	 */
	public static long copy(InputStream in, OutputStream out, boolean close) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		long count = 0;
		try {
			byte[] buffer = new byte[BUFFER_SZ];
			int read = -1;
			while ((read = bin.read(buffer, 0, buffer.length)) != -1) {
				bout.write(buffer, 0, read);
				count += read;
			}
			bout.flush();
			return count;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (close) {
				try {
					bin.close();
				} catch (Exception ex) {
				}
				try {
					bout.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source stream.
	 * @param dest
	 *            The destination stream.
	 * @param close
	 *            Close both stream when done.
	 */
	public static long copy(InputStream in, byte[] out, boolean close) throws IOException {
		int offset = 0;
		try {
			int read = -1;
			while ((read = in.read(out, offset, out.length - offset)) >= 0)
				offset += read;
			return offset;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (close) {
				try {
					in.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * Copy source into dest.
	 * 
	 * @param source
	 *            The source reader.
	 * @param dest
	 *            The destination reader.
	 * @param close
	 *            Close both stream when done.
	 */
	public static long copy(Reader in, Writer out, boolean close) throws IOException {
		BufferedReader bin = new BufferedReader(in);
		BufferedWriter bout = new BufferedWriter(out);
		long count = 0;
		try {
			char[] buffer = new char[BUFFER_SZ];
			int read = -1;
			while ((read = bin.read(buffer, 0, buffer.length)) != -1) {
				bout.write(buffer, 0, read);
				count += read;
			}
			bout.flush();
			return count;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (close) {
				try {
					bin.close();
				} catch (Exception ex) {
				}
				try {
					bout.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * Apppend source to dest.
	 * 
	 * @param source
	 *            The source file.
	 * @param dest
	 *            The destination file.
	 */
	public static long append(File source, File dest) throws IOException {
		return append(new FileInputStream(source), new FileOutputStream(dest, true));
	}

	/**
	 * Append source to dest.
	 * 
	 * @param source
	 *            The source stream.
	 * @param dest
	 *            The destination stream.
	 */
	public static long append(InputStream in, OutputStream out) throws IOException {
		return copy(in, out, true);
	}

	public static void deleteTempFiles(final String prefix, final String suffix) {
		try {
			deleteTempFiles(prefix, suffix, File.createTempFile(prefix, suffix).getParentFile());
		} catch (IOException e) {
		}
	}

	public static void deleteTempFiles(final String prefix, final String suffix, File dir) {
		File[] temps = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(prefix) && name.endsWith(suffix);
			}
		});
		for (int i = 0; i < temps.length; i++) {
			try {
				temps[i].delete();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Delete recursivly
	 * 
	 * @param file
	 *            the file (or directory) to delete.
	 */
	public static boolean delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				if (clean(file))
					return file.delete();
				return false;
			}
			return file.delete();
		}
		return true;
	}

	/**
	 * Clean recursivly
	 * 
	 * @param file
	 *            the directory to clean
	 */
	public static boolean clean(File file) {
		if (file.isDirectory()) {
			String filen[] = file.list();
			for (int i = 0; i < filen.length; i++) {
				File subfile = new File(file, filen[i]);
				if ((subfile.isDirectory()) && (!clean(subfile)))
					return false;
				else if (!subfile.delete())
					return false;
			}
		}
		return true;
	}

	public static void closeWithoutException(InputStream stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (Exception ex) {
			Log.warning(ex);
		}
	}

	public static void closeWithoutException(Socket socket) {
		if (socket == null)
			return;
		try {
			socket.close();
		} catch (Exception ex) {
			Log.warning(ex);
		}
	}

	public static void closeWithoutException(Reader stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (Exception ex) {
			Log.warning(ex);
		}
	}

	public static void closeWithoutException(Writer stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (Exception ex) {
			Log.warning(ex);
		}
	}

	public static void closeWithoutException(OutputStream stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (Exception ex) {
			Log.warning(ex);
		}
	}
}
