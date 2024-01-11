package org.corebounce.net.winnetou;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.corebounce.io.HTML;
import org.corebounce.io.HtmlPrintWriter;
import org.corebounce.net.FileTypeMap;
import org.corebounce.net.AddressUtilities;
import org.corebounce.util.Strings;

/**
 * (c) 1999 - 2004, IIUF, DIUF, corebounce association
 * <p>
 * 
 * Handler for the GET request.
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class HandlerGET extends HTTPHandler implements HTML {
	File root;
	String prefix;

	public HandlerGET(File root, HTTPServer server) {
		this(root, "", server);
	}

	public HandlerGET(File root, String prefix, HTTPServer server) {
		super(GET, server);
		this.root = root;
		this.prefix = prefix;
	}

	@SuppressWarnings("deprecation")
	public URL getURLforFile(File file) throws MalformedURLException {
		String path = file.toURI().toURL().getPath();
		if (path.startsWith(root.toURI().toURL().getPath())) {
			path = prefix + path.substring(root.getPath().length() + 1);
			Strings.replace(path, "//", "/");
			return new URL("http", server.getHostAddress(), server.getPort(), path);
		}
		return null;
	}

	private File getFile(String location) {
		// decode url
		try {
			location = AddressUtilities.URLDecode(location);
		} catch (Exception ex) {
			return null;
		}

		// Strip prefix from location
		if (location.startsWith(prefix))
			location = location.substring(prefix.length());

		// Strip leading "/" from location
		if (location.startsWith("/")) {
			location = location.substring(1, location.length());
		}

		return new File(root, location);
	}

	public File getFileForURL(URL url) {
		return getFile(url.getPath());
	}

	@Override
	public HTTPResponse request(HTTPRequest request) {
		String location = request.tokens[1];
		// make path
		File path = getFile(location);
		if (path == null)
			return new Response500(request);

		// check if it is a directory
		try {
			if (path.isDirectory()) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				HtmlPrintWriter out = new HtmlPrintWriter("Index of directory " + location, bout);
				File[] entries = path.listFiles();
				for (int i = 0; i < entries.length; i++) {
					out.tag(Li);
					out.tag(A, "href", entries[i].getName() + (entries[i].isDirectory() ? "/" : ""));
					out.tag(Code);
					out.print(entries[i].getName() + (entries[i].isDirectory() ? "/" : "(" + entries[i].length() + ")"));
					out.end(Code);
					out.end(A);
					out.end(Li);
				}
				out.close();
				return new Response200(request, "text/html", bout.toByteArray());
			}
		} catch (IOException ex) {
			return new Response500(request);
		}

		// Try to open file
		try {
			return new Response200(request, FileTypeMap.getContentTypeFor(path.getPath()), path);
		} catch (FileNotFoundException e) {
			return new Response404(request);
		}
	}
}
