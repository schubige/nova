package org.corebounce.net.winnetou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.corebounce.io.Utilities;

/**
 * (c) 1999 - 2004, IIUF, DIUF, corebounce association
 * <p>
 * 
 * HTTP response 200 (OK).
 * 
 * @author shoobee
 */

public class Response200 extends HTTPResponse {
	protected byte[] data = null;
	protected String mimetype;
	protected InputStream stream;
	protected File file;
	protected long len = -1;

	/**
	 * Construct a HTTP 200 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param data
	 *            the data itself
	 */

	public Response200(HTTPRequest req, String mimetype, byte[] data) {
		super(req);
		this.mimetype = mimetype;
		this.data = data;
		this.len = data.length;
	}

	/**
	 * Construct a HTTP 200 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param stream
	 *            InputStream containing the data
	 */

	public Response200(HTTPRequest req, String mimetype, InputStream stream) {
		super(req);
		this.mimetype = mimetype;
		this.stream = stream;
	}

	/**
	 * Construct a HTTP 200 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param stream
	 *            InputStream containing the data
	 */

	public Response200(HTTPRequest req, String mimetype, InputStream stream, long len) {
		super(req);
		this.mimetype = mimetype;
		this.stream = stream;
		this.len = len;
	}

	/**
	 * Construct a HTTP 200 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param file
	 *            File containing the data
	 */

	public Response200(HTTPRequest req, String mimetype, File file) throws FileNotFoundException {
		super(req);
		this.mimetype = mimetype;
		this.file = file;
		this.stream = new FileInputStream(file);
		this.len = file.length();
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 200 OK");
	}

	private static final SimpleDateFormat ARPA_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

	private String arpaDate(Date d) {
		return ARPA_FORMAT.format(d) + " GMT";
	}

	@Override
	public void responseHeader() throws IOException {
		super.responseHeader();

		String date = arpaDate(new Date());

		if (len >= 0)
			request.writeln("Content-Length: " + len);
		if (file != null)
			date = arpaDate(new Date(file.lastModified()));

		request.writeln("Date: " + date);
		request.writeln("Last-Modified: " + date);
		request.writeln("Content-Type: " + mimetype);
	}

	@Override
	public void responseData() throws IOException {
		if (data != null)
			request.write(data);
		else {
			Utilities.copy(stream, request.getOutput(), true);
		}
	}
}
