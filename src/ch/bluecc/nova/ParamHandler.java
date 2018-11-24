package ch.bluecc.nova;

import org.corebounce.net.MIME;
import org.corebounce.net.winnetou.HTTPHandler;
import org.corebounce.net.winnetou.HTTPRequest;
import org.corebounce.net.winnetou.HTTPResponse;
import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.net.winnetou.Response200;
import org.corebounce.net.winnetou.Response500;

@SuppressWarnings("nls")
public class ParamHandler extends HTTPHandler {

	protected ParamHandler(HTTPServer server) {
		super(GET, server);
	}

	@Override
	public HTTPResponse request(HTTPRequest req) {
		try {
			String[] url   = req.tokens[1].split("[/?=]");
			String   param = url[2];
			String   value = url.length > 4 ? url[4] : null;
			if("red".equals(param))
				NOVAControl.setRed(Double.parseDouble(value));
			else if("green".equals(param))
				NOVAControl.setGreen(Double.parseDouble(value));
			else if("blue".equals(param))
				NOVAControl.setBlue(Double.parseDouble(value));
			else if("brightness".equals(param))
				NOVAControl.setBrightness(Double.parseDouble(value));
			else if("color".equals(param)) {
				NOVAControl.setRed(  Integer.parseInt(value.substring(0, 2), 16) << 2);
				NOVAControl.setGreen(Integer.parseInt(value.substring(2, 4), 16) << 2);
				NOVAControl.setBlue( Integer.parseInt(value.substring(4, 6), 16) << 2);
			}
			else if("speed".equals(param))
				NOVAControl.setSpeed(Double.parseDouble(value));
			else if("content".equals(param))
				NOVAControl.setContent((int)Double.parseDouble(value));
			else if("reset".equals(param))
				NOVAControl.resetNOVA();
			else if("reload".equals(param))
				System.exit(0);
			return new Response200(req, MIME.HTML, EMPTY);
		} catch(Throwable t) {
			return new Response500(req, t);
		}
	}

}
