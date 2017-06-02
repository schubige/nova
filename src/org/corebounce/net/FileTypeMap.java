package org.corebounce.net;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Tries to recognize MIME types of files.
 * 
 * (c) 1999 - 2005, IIUF, DIUF, corebounce association, swisscom innovations
 * <p>
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class FileTypeMap extends MIME {

	private static HashMap<String, String> mimeMediaType = new HashMap<String, String>();
	private static HashMap<String, String> mimeSubType = new HashMap<String, String>();
	private static HashMap<String, String> mime2extension = new HashMap<String, String>();
	private static ArrayList<String[]> mimeTypes = new ArrayList<String[]>();

	static {
		put(Application, Mac_binhex40, ".hqx");
		put(Application, Mac_compactpro, ".cpt");
		put(Application, Msword, ".doc");
		put(Application, Octet_stream, ".bin");
		put(Application, Octet_stream, ".dms");
		put(Application, Octet_stream, ".lha");
		put(Application, Octet_stream, ".lzh");
		put(Application, Octet_stream, ".exe");
		put(Application, Octet_stream, ".class");
		put(Application, Oda, ".oda");
		put(Application, Pdf, ".pdf");
		put(Application, Postscript, ".ai");
		put(Application, Postscript, ".eps");
		put(Application, Postscript, ".ps");
		put(Application, Powerpoint, ".ppt");
		put(Application, Smil, ".smil");
		put(Application, Rtf, ".rtf");
		put(Application, X_bcpio, ".bcpio");
		put(Application, X_cdlink, ".vcd");
		put(Application, X_cpio, ".cpio");
		put(Application, X_csh, ".csh");
		put(Application, X_director, ".dcr");
		put(Application, X_director, ".dir");
		put(Application, X_director, ".dxr");
		put(Application, X_dvi, ".dvi");
		put(Application, X_gtar, ".gtar");
		put(Application, X_hdf, ".hdf");
		put(Application, X_koan, ".skp");
		put(Application, X_koan, ".skd");
		put(Application, X_koan, ".skt");
		put(Application, X_koan, ".skm");
		put(Application, X_latex, ".latex");
		put(Application, X_mif, ".mif");
		put(Application, X_netcdf, ".nc");
		put(Application, X_netcdf, ".cdf");
		put(Application, X_resource, ".xrs");
		put(Application, X_sh, ".sh");
		put(Application, X_shar, ".shar");
		put(Application, X_sl1, ".sl1");
		put(Application, X_sl2, ".sl2");
		put(Application, X_stuffit, ".sit");
		put(Application, X_sv4cpio, ".sv4cpio");
		put(Application, X_sv4crc, ".sv4crc");
		put(Application, X_tar, ".tar");
		put(Application, X_tcl, ".tcl");
		put(Application, X_tex, ".tex");
		put(Application, X_texinfo, ".texinfo");
		put(Application, X_texinfo, ".texi");
		put(Application, X_troff, ".t");
		put(Application, X_troff, ".tr");
		put(Application, X_troff, ".roff");
		put(Application, X_troff_man, ".man");
		put(Application, X_troff_me, ".me");
		put(Application, X_troff_ms, ".ms");
		put(Application, X_ustar, ".ustar");
		put(Application, X_wais_source, ".src");
		put(Application, Zip, ".zip");
		put(Application, X_lpl, ".LPL");
		put(Application, X_lpl, ".lpl");
		put(Application, Java_archive, ".jar");
		put(Application, X_font, ".pfa");
		put(Application, X_font, ".pfb");
		put(Application, X_font, ".gsf");
		put(Application, X_font, ".pcf");
		put(Application, X_font, ".pcf.Z");
		put(Application, X_font, ".ttf");
		put(Audio, Amr, ".amr");
		put(Audio, Basic, ".au");
		put(Audio, Basic, ".snd");
		put(Audio, Midi, ".mid");
		put(Audio, Midi, ".midi");
		put(Audio, Midi, ".kar");
		put(Audio, Mpeg, ".mpga");
		put(Audio, Mpeg, ".mp2");
		put(Audio, X_aiff, ".aif");
		put(Audio, X_aiff, ".aiff");
		put(Audio, X_aiff, ".aifc");
		put(Audio, X_pn_realaudio, ".ram");
		put(Audio, X_pn_realaudio_plugin, ".rpm");
		put(Audio, X_raw, ".raw");
		put(Audio, X_realaudio, ".ra");
		put(Audio, X_wav, ".wav");
		put(Audio, MP3, ".mp3");
		put(Chemical, X_pdb, ".pdb");
		put(Chemical, X_pdb, ".xyz");
		put(Image, Bmp, ".bmp");
		put(Image, Gif, ".gif");
		put(Image, Ief, ".ief");
		put(Image, Jpeg, ".jpe");
		put(Image, Jpeg, ".jpeg");
		put(Image, Pjpeg, ".jpg");
		put(Image, Jpeg, ".jpg");
		put(Image, Png, ".png");
		put(Image, Tiff, ".tiff");
		put(Image, Tiff, ".tif");
		put(Image, X_cmu_raster, ".ras");
		put(Image, X_portable_anymap, ".pnm");
		put(Image, X_portable_bitmap, ".pbm");
		put(Image, X_portable_graymap, ".pgm");
		put(Image, X_portable_pixmap, ".ppm");
		put(Image, X_rgb, ".rgb");
		put(Image, X_xbitmap, ".xbm");
		put(Image, X_xpixmap, ".xpm");
		put(Image, X_xwindowdump, ".xwd");
		put(Text, Html, ".html");
		put(Text, Html, ".htm");
		put(Text, Plain, ".txt");
		put(Text, Richtext, ".rtx");
		put(Text, Tab_separated_values, ".tsv");
		put(Text, X_setext, ".etx");
		put(Text, X_sgml, ".sgml");
		put(Text, X_sgml, ".sgm");
		put(Text, X_vcard, ".vcf");
		put(Text, X_vcal, ".vcs");
		put(Video, Mpeg, ".mpeg");
		put(Video, Mpeg, ".mpg");
		put(Video, Mpeg, ".mpe");
		put(Video, Quicktime, ".qt");
		put(Video, Quicktime, ".mov");
		put(Video, Quicktime, ".mp4");
		put(Video, X_msvideo, ".avi");
		put(Video, X_sgi_movie, ".movie");
		put(X_conference, X_cooltalk, ".ice");
		put(X_geometry, Obj, ".obj");
		put(X_world, X_vrml, ".wrl");
		put(X_world, X_vrml, ".vrml");
	}

	private static void put(String mediaType, String subType, String extension) {
		mimeMediaType.put(extension, mediaType);
		mimeSubType.put(extension, subType);
		mimeTypes.add(new String[] { mediaType, subType });
		mime2extension.put(mediaType + "/" + subType, extension);
	}

	public static String[][] getMimeTypes() {
		return mimeTypes.toArray(new String[mimeTypes.size()][]);
	}

	private static String extension(String filename) {
		int position = filename.lastIndexOf('.');
		if (position != -1)
			return filename.substring(position).toLowerCase();
		return filename.toLowerCase();
	}

	public static String mime2extension(String mimeType) {
		String result = mime2extension.get(mimeType);
		return result == null ? "" : result;
	}

	/**
	 * Get the MIME type of a file.
	 * 
	 * @param file
	 *            The file in question.
	 * @return a MIME type or null if the file could not be identified.
	 */
	public static String getContentTypeFor(File file) {
		return getContentTypeFor(file.getName());
	}

	/**
	 * Get the MIME type of a file.
	 * 
	 * @param filename
	 *            the name of the file in question.
	 * @return a MIME type or null if the file could not be identified.
	 */
	public static String getContentTypeFor(String filename) {
		String extension = extension(filename);
		String mclass = mimeMediaType.get(extension);
		if (mclass == null)
			return Application + "/" + Octet_stream;
		String mtype = mimeSubType.get(extension);
		if (mtype == null)
			return Application + "/" + Octet_stream;
		return mclass + "/" + mtype;
	}
}
