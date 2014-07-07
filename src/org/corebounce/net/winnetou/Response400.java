package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004 IIUF, DIUF, corebounce association
 * <p>
 * 
 * HTTP response 400 (bad request).
 * 
 * @author shoobee
 */

public class Response400 extends HTTPResponse {

	public Response400(HTTPRequest req) {
		super(req);
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 400 Bad Request");
	}

	@Override
	public void responseData() throws IOException {
		errorPage("400 Bad Request", "The request could not be understood by the server due to malformed syntax.");
	}
}
