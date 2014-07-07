package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004, IIUF, DIUF, corebounce association
 * <p>
 * 
 * HTTP response 204 (no response).
 * 
 * @author shoobee
 */

public class Response204 extends HTTPResponse {

	/**
	 * Construct a HTTP 204 response.
	 * 
	 * @param req
	 *            the request
	 */

	public Response204(HTTPRequest req) {
		super(req);
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 204 No Content");
	}

	@Override
	public void responseData() {
	}
}
