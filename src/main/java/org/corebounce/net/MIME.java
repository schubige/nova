package org.corebounce.net;

import org.corebounce.util.Strings;

/**
 * Some mime types & utilities.
 * <p>
 * 
 * (c) 1999 - 2006, IIUF, DIUF, corebounce association, swisscom innovations
 * <p>
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class MIME {
	public final static String GIF = MIME.Image + "/" + MIME.Gif;
	public final static String HTML = MIME.Text + "/" + MIME.Html;

	public final static String Application = "application";
	public final static String Audio = "audio";
	public final static String Image = "image";
	public final static String Chemical = "chemical";
	public final static String Text = "text";
	public final static String Video = "video";
	public final static String X_conference = "x-conference";
	public final static String X_world = "x-world";
	public final static String Message = "message";
	public final static String Multipart = "multipart";

	public final static String Activemessage = "activemessage";
	public final static String Andrew_inset = "andrew-inset";
	public final static String Amr = "amr";
	public final static String Applefile = "applefile";
	public final static String Atomicmail = "atomicmail";
	public final static String Bmp = "bmp";
	public final static String Dca_rft = "dca-rft";
	public final static String Dec_dx = "dec-dx";
	public final static String Form_data = "form-data";
	public final static String Java_archive = "java-archive";
	public final static String Mac_binhex40 = "mac-binhex40";
	public final static String Mac_compactpro = "mac-compactpro";
	public final static String Macwriteii = "macwriteii";
	public final static String Msword = "msword";
	public final static String News_message_id = "news-message-id";
	public final static String News_transmission = "news-transmission";
	public final static String Octet_stream = "octet-stream";
	public final static String Oda = "oda";
	public final static String Pdf = "pdf";
	public final static String Obj = "obj";
	public final static String Postscript = "postscript";
	public final static String Powerpoint = "powerpoint";
	public final static String Remote_printing = "remote-printing";
	public final static String Rtf = "rtf";
	public final static String Smil = "smil";
	public final static String Slate = "slate";
	public final static String Wita = "wita";
	public final static String Wordperfect5_1 = "wordperfect5.1";
	public final static String X_bcpio = "x-bcpio";
	public final static String X_cdlink = "x-cdlink";
	public final static String X_compress = "x-compress";
	public final static String X_cpio = "x-cpio";
	public final static String X_csh = "x-csh";
	public final static String X_director = "x-director";
	public final static String X_dvi = "x-dvi";
	public final static String X_gtar = "x-gtar";
	public final static String X_gzip = "x-gzip";
	public final static String X_hdf = "x-hdf";
	public final static String X_koan = "x-koan";
	public final static String X_latex = "x-latex";
	public final static String X_lpl = "x-lpl";
	public final static String X_mif = "x-mif";
	public final static String X_raw = "x-raw";
	public final static String X_netcdf = "x-netcdf";
	public final static String X_resource = "x-resource";
	public final static String X_sh = "x-sh";
	public final static String X_shar = "x-shar";
	public final static String X_stuffit = "x-stuffit";
	public final static String X_sl1 = "x-soundium-lang1";
	public final static String X_sl2 = "x-soundium-lang2";
	public final static String X_sv4cpio = "x-sv4cpio";
	public final static String X_sv4crc = "x-sv4crc";
	public final static String X_tar = "x-tar";
	public final static String X_tcl = "x-tcl";
	public final static String X_tex = "x-tex";
	public final static String X_texinfo = "x-texinfo";
	public final static String X_troff = "x-troff";
	public final static String X_troff_man = "x-troff-man";
	public final static String X_troff_me = "x-troff-me";
	public final static String X_troff_ms = "x-troff-ms";
	public final static String X_ustar = "x-ustar";
	public final static String X_wais_source = "x-wais-source";
	public final static String X_www_form_urlencoded = "x-www-form-urlencoded";
	public final static String Zip = "zip";
	public final static String Basic = "basic";
	public final static String Midi = "midi";
	public final static String X_aiff = "x-aiff";
	public final static String X_pn_realaudio = "x-pn-realaudio";
	public final static String X_pn_realaudio_plugin = "x-pn-realaudio-plugin";
	public final static String X_realaudio = "x-realaudio";
	public final static String X_wav = "x-wav";
	public final static String MP3 = "mp3";
	public final static String MP4 = "mp4";
	public final static String X_pdb = "x-pdb";
	public final static String Gif = "gif";
	public final static String Ief = "ief";
	public final static String Jpeg = "jpeg";
	public final static String Pjpeg = "pjpeg";
	public final static String Png = "png";
	public final static String Tiff = "tiff";
	public final static String X_cmu_raster = "x-cmu-raster";
	public final static String X_portable_anymap = "x-portable-anymap";
	public final static String X_portable_bitmap = "x-portable-bitmap";
	public final static String X_portable_graymap = "x-portable-graymap";
	public final static String X_portable_pixmap = "x-portable-pixmap";
	public final static String X_rgb = "x-rgb";
	public final static String X_xbitmap = "x-xbitmap";
	public final static String X_xpixmap = "x-xpixmap";
	public final static String X_xwindowdump = "x-xwindowdump";
	public final static String External_body = "external-body";
	public final static String News = "news";
	public final static String Partial = "partial";
	public final static String Rfc822 = "rfc822";
	public final static String Alternative = "alternative";
	public final static String Appledouble = "appledouble";
	public final static String Digest = "digest";
	public final static String Mixed = "mixed";
	public final static String Parallel = "parallel";
	public final static String Html = "html";
	public final static String Plain = "plain";
	public final static String Richtext = "richtext";
	public final static String Tab_separated_values = "tab-separated-values";
	public final static String X_setext = "x-setext";
	public final static String X_sgml = "x-sgml";
	public final static String Mpeg = "mpeg";
	public final static String Quicktime = "quicktime";
	public final static String X_msvideo = "x-msvideo";
	public final static String X_sgi_movie = "x-sgi-movie";
	public final static String X_cooltalk = "x-cooltalk";
	public final static String X_vrml = "x-vrml";
	public final static String X_geometry = "x-geometry";
	public final static String X_font = "x-font";
	public final static String X_vcard = "x-vcard";
	public final static String X_vcal = "x-vcal";

	public final static String[] MEDIA_TYPES = { Application, Audio, Image,
			Chemical, Text, Video, X_conference, X_world, X_geometry, Message,
			Multipart, };

	public final static String[] SUB_TYPE = { Activemessage, Andrew_inset,
			Applefile, Atomicmail, Dca_rft, Dec_dx, Java_archive, Mac_binhex40,
			Mac_compactpro, Macwriteii, Msword, News_message_id,
			News_transmission, Octet_stream, Oda, Pdf, Postscript, Powerpoint,
			Remote_printing, Rtf, Slate, Wita, Wordperfect5_1, X_bcpio,
			X_cdlink, X_compress, X_cpio, X_csh, X_director, X_dvi, X_gtar,
			X_gzip, X_hdf, X_koan, X_latex, X_lpl, X_mif, X_netcdf, X_resource,
			X_sh, X_shar, X_stuffit, X_sv4cpio, X_sv4crc, X_tar, X_tcl, X_tex,
			X_texinfo, X_troff, X_troff_man, X_troff_me, X_troff_ms, X_ustar,
			X_wais_source, Zip, Basic, Midi, X_aiff, X_pn_realaudio,
			X_pn_realaudio_plugin, X_realaudio, X_wav, X_pdb, Gif, Ief, Jpeg,
			Png, Tiff, X_cmu_raster, X_portable_anymap, X_portable_bitmap,
			X_portable_graymap, X_portable_pixmap, X_rgb, X_xbitmap, X_xpixmap,
			X_xwindowdump, External_body, News, Partial, Rfc822, Alternative,
			Appledouble, Digest, Mixed, Parallel, Html, Plain, Richtext,
			Tab_separated_values, X_setext, X_sgml, Mpeg, Quicktime, X_msvideo,
			X_sgi_movie, X_cooltalk, X_vrml, };

	public static boolean match(String mimeType, String pattern) {
		String[] mt = Strings.split(mimeType, '/');
		String[] mp = Strings.split(pattern, '/');

		boolean result = (mp[0].equals(mt[0]) && mp[1].equals(mt[1]))
				|| (mp[0].equals("*") && mp[1].equals(mt[1]))
				|| (mp[0].equals(mt[0]) && mp[1].equals("*"))
				|| (mp[0].equals("*") && mp[1].equals("*"));

		return result;
	}
}
