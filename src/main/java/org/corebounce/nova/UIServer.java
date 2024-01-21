package org.corebounce.nova;

import org.corebounce.net.winnetou.HTTPServer;

class UIServer {
	private final HTTPServer httpServer;
	private final ParamHandler paramHandler;
	private final UIHandler uiHandler;

	UIServer() {
		httpServer     = new HTTPServer(null, 80);
		uiHandler      = new UIHandler(httpServer);
		paramHandler   = new ParamHandler(httpServer);

		httpServer.setDefaultHandler(uiHandler);
		httpServer.addHandler(uiHandler,    "/");
		httpServer.addHandler(uiHandler,    "/index.html");
		httpServer.addHandler(paramHandler, "/nova/red");
		httpServer.addHandler(paramHandler, "/nova/green");		
		httpServer.addHandler(paramHandler, "/nova/blue");
		httpServer.addHandler(paramHandler, "/nova/brightness");
		httpServer.addHandler(paramHandler, "/nova/speed");
		httpServer.addHandler(paramHandler, "/nova/content");
		httpServer.addHandler(paramHandler, "/nova/color");
		httpServer.addHandler(paramHandler, "/nova/reset");
		httpServer.addHandler(paramHandler, "/nova/reload");
		httpServer.start();		
	}
}
