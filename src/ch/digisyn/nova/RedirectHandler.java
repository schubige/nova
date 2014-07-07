package ch.digisyn.nova;

import org.corebounce.net.winnetou.HTTPHandler;
import org.corebounce.net.winnetou.HTTPRequest;
import org.corebounce.net.winnetou.HTTPResponse;
import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.net.winnetou.Response301;
import org.corebounce.net.winnetou.Response304;
import org.corebounce.net.winnetou.Response500;

public class RedirectHandler extends HTTPHandler {

	protected RedirectHandler(HTTPServer server) {
		super(GET, server);
	}

	@Override
	public HTTPResponse request(HTTPRequest req) {
		try {
			if(req.toString().contains("Windows Phone 8"))
				return new Response304(req, "Microsoft NCSI".getBytes());
			else
				return new Response301(req, "http://" + server.getHostAddress() + "/");
		} catch(Throwable t) {
			return new Response500(req, t);
		}
	}
}
