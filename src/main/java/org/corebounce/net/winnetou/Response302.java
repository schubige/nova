package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 2020 bluecc
 * <p>
 * 
 * HTTP response 302 (found).
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class Response302 extends HTTPResponse {
	protected byte[] data = null;
	protected String newLocation;
	
	/**
	 * Construct a HTTP 302 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param data
	 *            the data itself
	 */

	public Response302(HTTPRequest req, String newLocation) {
		super(req);
		this.newLocation = newLocation;
		this.data        = ("<html><head><title>Moved</title></head><body><h1>Moved</h1><p>This page has moved to <a href=\""
		+ newLocation + "\">"
		+ newLocation + "</a>.</p></body></html>").getBytes();
	}
	
	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.1 302 Found");
		request.writeln("Location: " + newLocation);
	}

	@Override
	public void responseHeader() throws IOException {
		super.responseHeader();
		request.writeln("Content-Length: " + data.length);
		request.writeln("Content-Type: text/html");
	}

	@Override
	public void responseData() throws IOException {
		request.write(data);
	}
}
