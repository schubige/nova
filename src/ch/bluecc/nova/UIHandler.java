package ch.bluecc.nova;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.corebounce.io.HTML;
import org.corebounce.io.HtmlPrintWriter;
import org.corebounce.net.MIME;
import org.corebounce.net.winnetou.HTTPHandler;
import org.corebounce.net.winnetou.HTTPRequest;
import org.corebounce.net.winnetou.HTTPResponse;
import org.corebounce.net.winnetou.HTTPServer;
import org.corebounce.net.winnetou.Response200;
import org.corebounce.net.winnetou.Response302;
import org.corebounce.net.winnetou.Response500;
import org.corebounce.util.Strings;

@SuppressWarnings("nls")
class UIHandler extends HTTPHandler {

	protected UIHandler(HTTPServer server) {
		super(GET, server);
	}

	private void makeSlider(HtmlPrintWriter out, String cmd, String name, double value, double min, double max) throws IOException {
		out.tag(HTML.Div, "id", name);
		out.tag(HTML.Input, 
				"id",    name, 
				"type",  "range", 
				"min",   Double.toString(min), 
				"max",   Double.toString(max), 
				"step",  Double.toString((max - min) / 200.0),
				"value", Double.toString(value), 
				"style", "width:250px;",
				"onInput", 
				"httpGet('/nova/" + cmd + "?value=' + this.value);");
		out.print(name);
		out.end(HTML.Div);
	}

	void makeColorPicker(HtmlPrintWriter out, String cmd, String name, int r, int g, int b) throws IOException {
		String value = '#' + Strings.toHex((byte)(r >> 2)) + Strings.toHex((byte)(g >> 2)) + Strings.toHex((byte)(b >> 2));
		out.tag(HTML.Div, "id", name);
		out.println(name);
		out.tag(HTML.Input, 
				"id",    name, 
				"type",  "color",
				"value", value,
				"onChange",
				"httpGet('/nova/" + cmd + "?value=' + this.value.substring(1));");
		out.end(HTML.Div);
	}

	private void makeButton(HtmlPrintWriter out, String cmd, String name) throws IOException {
		out.tag(HTML.Button, 
				"type",       "button",
				"onClick", 
				"httpGet('/nova/" + cmd + "');"
				);
		out.print(name);
		out.end(HTML.Button);
	}

	private <T extends Object> void makeDropDown(HtmlPrintWriter out, String cmd, String name, List<Content> contents) throws IOException {
		out.tag(HTML.Select, 
				"id", 
				name,
				"style", "width:300px;",
				"onChange", 
				"httpGet('/nova/" + cmd + "?value=' + this.value);");
		for(int i = 0; i < contents.size(); i++) {
			if(i == NOVAControl.get().getContent())
				out.tag(HTML.Option, "value", Integer.toString(i), "selected", "selected");
			else
				out.tag(HTML.Option, "value", Integer.toString(i));

			out.print(contents.get(i).toString());
			out.end(HTML.Option);
		}
		out.end(HTML.Select);
	}

	@Override
	public HTTPResponse request(HTTPRequest req) {
		if (!(req.toString().contains("htm")))
			return new Response302(req, "http://" + server.getHostAddress() + "/index.html");

		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			HtmlPrintWriter out  = new HtmlPrintWriter(bout);
			out.tag(HTML.Html);
			out.tag(HTML.Head);
			out.tag(HTML.Title);
			out.print("NOVA");
			out.end(HTML.Title);
			out.print("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
			out.script(
					"function httpGet(theUrl) {",
					"   var xmlHttp = new XMLHttpRequest();",
					"   xmlHttp.open( 'GET', theUrl, true);",
					"   xmlHttp.setRequestHeader('If-Modified-Since', 'Sat, 1 Jan 2005 00:00:00 GMT');",
					"   xmlHttp.send( null );",
					"}"
					);
			out.style(
					"body {",
					"   font-family:sans-serif;",
					"   background-color:#202020;",
					"   color:#a0a0a0;",
					"}"
					);
			out.end(HTML.Head);
			out.tag(HTML.Body);

			NOVAControl control = NOVAControl.get();
			
			makeDropDown(out, "content",    "Content",    control.getContents());			
			// makeColorPicker(out, "color",   "Color",   control.getRed(), control.getGreen(), control.getBlue());
			makeSlider(out,   "red",        "Red",        control.getRed(),         0,   1.0);
			makeSlider(out,   "green",      "Green",      control.getGreen(),       0,   1.0);
			makeSlider(out,   "blue",       "Blue",       control.getBlue(),        0,   1.0);
			makeSlider(out,   "brightness", "B'ness", control.getBrightness(),  0.1, 1.0);
			makeSlider(out,   "speed",      "Speed",      control.getSpeed(),      -3.0, 5);
			makeButton(out,   "reset",      "Reset");
			makeButton(out,   "reload",     "Reload");

			out.close();
			
			return new Response200(req, MIME.HTML, bout.toByteArray());
		} catch (Throwable t) {
			return new Response500(req, t);
		}
	}

}
