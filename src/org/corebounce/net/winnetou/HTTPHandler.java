package org.corebounce.net.winnetou;

/**
 * (c) 1999 - 2004, IIUF, DIUF, corebounce
 * <p>
 * 
 * Structure of an HTTP request handler.
 * 
 * @author shoobee
 */

public abstract class HTTPHandler {
	public static final byte[] EMPTY = new byte[0]; 

	public static final int GET = 0;
	public static final int PUT = 1;
	public static final int POST = 2;
	public static final int DELETE = 3;

	public static final String[] METHODS = { "GET", "PUT", "POST", "DELETE" };

	private int method;
	protected String rootPath;
	protected HTTPServer server;

	protected HTTPHandler(int method, HTTPServer server) {
		this.method = method;
		this.server = server;
	}

	protected void setRootPath(String path) {
		this.rootPath = path;
	}

	public int handledMethod() {
		return method;
	}

	public String handledMethodStr() {
		return METHODS[method];
	}

	public abstract HTTPResponse request(HTTPRequest request);
}
