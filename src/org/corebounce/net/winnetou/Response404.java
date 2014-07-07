package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004 IIUF, DIUF, corebounce association
 * <p>
 * 
 * HTTP response 404 (not found).
 * 
 * @author shoobee
 */

public class Response404 extends HTTPResponse {
	public Response404(HTTPRequest req) {
		super(req);
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 404 Not Found");
	}

	@Override
	public void responseData() throws IOException {
		errorPage("404 Not Found", "Winnetou: The requested location could not be found.");
	}
}
