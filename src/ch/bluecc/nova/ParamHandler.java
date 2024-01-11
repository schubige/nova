package ch.bluecc.nova;

import org.corebounce.net.MIME;
import org.corebounce.net.winnetou.HTTPHandler;
import org.corebounce.net.winnetou.HTTPRequest;
import org.corebounce.net.winnetou.HTTPResponse;
import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.net.winnetou.Response200;
import org.corebounce.net.winnetou.Response500;
import org.corebounce.util.Log;

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
			switch (param) {
			case "red":
				NOVAControl.setRed(Double.parseDouble(value));
				break;
			case "green":
				NOVAControl.setGreen(Double.parseDouble(value));
				break;
			case "blue":
				NOVAControl.setBlue(Double.parseDouble(value));
				break;
			case "brightness":
				NOVAControl.setBrightness(Double.parseDouble(value));
				break;
			case "color":
				NOVAControl.setRed(  Integer.parseInt(value.substring(0, 2), 16) << 2);
				NOVAControl.setGreen(Integer.parseInt(value.substring(2, 4), 16) << 2);
				NOVAControl.setBlue( Integer.parseInt(value.substring(4, 6), 16) << 2);
				break;
			case "speed":
				NOVAControl.setSpeed(Double.parseDouble(value));
				break;
			case "content":
				NOVAControl.setContent((int)Double.parseDouble(value));
				break;
			case "reset":
				NOVAControl.resetNOVA();
				break;
			case "reload":
				Log.info("User requested reload. Exiting.");
				System.exit(0);
				break;
			default:
				Log.warning("Unkonwn HTTP request " + param);
			}
			return new Response200(req, MIME.HTML, EMPTY);
		} catch(Throwable t) {
			return new Response500(req, t);
		}
	}
}
