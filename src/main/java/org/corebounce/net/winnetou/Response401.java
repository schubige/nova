package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * (c) 1999 - 2004 IIUF, DIUF, corebounce association
 * <p>
 * 
 * HTTP response 401 (unauthorized).
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class Response401 extends HTTPResponse {
	protected String realm;
	protected String message;

	public Response401(HTTPRequest req, String realm, String message) {
		super(req);
		this.realm = realm;
		this.message = message;
	}

	@Override
	public void responseStatus() throws IOException {
		request.writeln("HTTP/1.0 401 Authorization Required");
	}

	@Override
	public void responseHeader() throws IOException {
		super.responseHeader();
		request.writeln("WWW-Authenticate: Basic realm=\"WebAdmin\"");
		request.writeln("Content-Type: text/html");
	}

	@Override
	public void responseData() throws IOException {
		request.write(message);
	}
}
