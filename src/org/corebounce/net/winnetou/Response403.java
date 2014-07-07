package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2006 IIUF, DIUF, corebounce association, swisscom innovations
 * <p>
 * 
 * HTTP response 403 (forbidden).
 * 
 * @author shoobee
 */

public class Response403 extends HTTPResponse {
	public Response403(HTTPRequest req) {
		super(req);
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 403 Forbidden");
	}

	@Override
	public void responseData() throws IOException {
		errorPage("403 Forbidden", "Winnetou: You are not allowed to access this location");
	}
}
