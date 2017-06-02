package org.corebounce.net.winnetou;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.corebounce.util.Strings;

/**
 * (c) 1999 - 2004, IIUF, corebounce association
 * <p>
 * 
 * HTTP response.
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public abstract class HTTPResponse {
	protected HTTPRequest request;

	public HTTPResponse(HTTPRequest req) {
		setRequest(req);
	}

	public void respond() throws IOException {
		responseStatus();
		responseHeader();
		request.writeln();
		responseData();
	}

	public abstract void responseStatus() throws IOException;

	public abstract void responseData() throws IOException;

	public void responseHeader() throws IOException {
		Date date = Calendar.getInstance().getTime();
		request.writeln("Date: " + date.toString());
	}

	public void errorPage(String title, String message) throws IOException {
		request.writeln("<html>");
		request.writeln("<head><title>" + title + "</title></head>");
		request.writeln("<body>");
		request.writeln("<h1>" + title + "</h1>");
		request.writeln("<p>" + message + "</p>");
		if (request.tokens != null)
			request.writeln("<p><code>" + Strings.cat(request.tokens, ' ') + "</code></p>");
		request.writeln("</body>");
		request.writeln("</html>");
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}
}
