package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004 IIUF, DIUF, corebounce assoication
 * <p>
 * 
 * HTTP response 500 (internal error).
 * 
 * @author shoobee
 */

public class Response500 extends HTTPResponse {
	Throwable t;

	public Response500(HTTPRequest req) {
		super(req);
	}

	public Response500(HTTPRequest req, Throwable t) {
		super(req);
		this.t = t;
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 500 Internal Server Error");
	}

	@Override
	public void responseData() throws IOException {
		errorPage("500 Internal Server Error", t == null ? "An internal server error occurred." : t.getClass().getName() + ":" + t.getMessage());
	}
}
