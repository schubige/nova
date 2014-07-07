package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004 IIUF, DIUF, corebounce assoication
 * <p>
 * 
 * HTTP response 501 (not implemented).
 * 
 * @author shoobee
 */

public class Response501 extends HTTPResponse {
	public Response501(HTTPRequest req) {
		super(req);
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 501 Method Not Implemented");
	}

	@Override
	public void responseData() throws IOException {
		errorPage("501 Method Not Implemented", "The requested method (" + request.tokens[0] + ") is not implemented.");
	}
}
