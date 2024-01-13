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
public final class ParamHandler extends HTTPHandler {
	ParamHandler(HTTPServer server) {
		super(GET, server);
	}

	@Override
	public HTTPResponse request(HTTPRequest req) {
		try {
			NOVAControl control = NOVAControl.get();
			String[] url = req.tokens[1].split("[/?=]");
			String param = url[2];
			String value = url.length > 4 ? url[4] : null;
			switch (param) {
			case "red":
				control.setRed(Double.parseDouble(value));
				break;
			case "green":
				control.setGreen(Double.parseDouble(value));
				break;
			case "blue":
				control.setBlue(Double.parseDouble(value));
				break;
			case "brightness":
				control.setBrightness(Double.parseDouble(value));
				break;
			case "color":
				control.setRed(Integer.parseInt(value.substring(0, 2), 16) << 2);
				control.setGreen(Integer.parseInt(value.substring(2, 4), 16) << 2);
				control.setBlue(Integer.parseInt(value.substring(4, 6), 16) << 2);
				break;
			case "speed":
				control.setSpeed(Double.parseDouble(value));
				break;
			case "content":
				control.setContent((int) Double.parseDouble(value));
				break;
			case "reset":
				control.novaReset();
				break;
			case "reload":
				Log.info("User requested reload. Exiting.");
				System.exit(0);
				break;
			default:
				Log.warning("Unkonwn HTTP request " + param);
			}
			return new Response200(req, MIME.HTML, EMPTY);
		} catch (Throwable t) {
			return new Response500(req, t);
		}
	}
}
