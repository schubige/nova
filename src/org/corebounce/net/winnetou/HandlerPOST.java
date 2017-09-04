package org.corebounce.net.winnetou;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePartDataSource;

import org.corebounce.util.Log;
import org.corebounce.net.MIME;
import org.corebounce.net.AddressUtilities;
import org.corebounce.util.Strings;

/**
 * (c) 1999 - 2004 IIUF
 * <p>
 * 
 * Handler for the POST request.
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public abstract class HandlerPOST extends HTTPHandler {
	static final String FORM_DATA = MIME.Multipart + "/" + MIME.Form_data;
	static final String FORM_URLENCODED = MIME.Application + "/" + MIME.X_www_form_urlencoded;

	public HandlerPOST(HTTPServer server) {
		super(POST, server);
	}

	@Override
	public final HTTPResponse request(HTTPRequest request) {
		try {
			int len = request.getContentLength();
			byte[] data = new byte[len];
			int off = 0;
			while (off < len) {
				int read = request.getInput().read(data, off, len - off);
				if (read == -1)
					break;
				off += read;
			}
			if (FORM_DATA.equals(request.getContentType())) {
				request.setFields(new MimeMultipart(new MimePartDataSource(new MimeBodyPart(request.getHeaders(), data))));
			} else if (FORM_URLENCODED.equals(request.getContentType())) {
				String[] form = Strings.split(AddressUtilities.URLDecode(new String(data)), '&');
				for (int i = 0; i < form.length; i++) {
					String[] field = Strings.split(form[i], '=');
					if (field.length == 2)
						request.addKeyValue(field[0], field[1]);
					else
						request.addKeyValue(form[i], null);
				}
			} else {
				Log.warning("Can't handle content type:" + request.getContentType());
				return new Response501(request);
			}
		} catch (Exception ex) {
			Log.warning(ex);
			return new Response500(request);
		}
		return postRequest(request);
	}

	public abstract HTTPResponse postRequest(HTTPRequest request);
}
