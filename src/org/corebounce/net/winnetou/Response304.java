package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2014, IIUF, DIUF, corebounce association, digisyn
 * <p>
 * 
 * HTTP response 304 (Not Modified).
 * 
 * @author shoobee
 */

public class Response304 extends HTTPResponse {
	protected byte[] data = null;

	/**
	 * Construct a HTTP 304 response.
	 * 
	 * @param req
	 *            the request
	 * @param mimetype
	 *            type of the data to be sent
	 * @param data
	 *            the data itself
	 */

	public Response304(HTTPRequest req, byte[] data) {
		super(req);
		this.data = data;
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.1 304 Not Modified");
	}

	@Override
	public void responseHeader() throws IOException {
		super.responseHeader();
	}

	@Override
	public void responseData() throws IOException {
		request.write(data);
	}
}
