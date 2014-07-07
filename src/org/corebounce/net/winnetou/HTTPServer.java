package org.corebounce.net.winnetou;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.corebounce.net.Log;
import org.corebounce.net.AddressUtilities;

/**
 * (c) 1999 - 2004, IIUF, DIUF, corebounce association
 * <p>
 * 
 * Simple HTTP server.
 * 
 * @author shoobee
 */

public class HTTPServer implements Runnable {
	private HashMap<Integer, HashMap<String, HTTPHandler>> handlers;
	private ServerSocket                                   server;
	private int                                            port;
	private Thread                                         serverThread;
	ThreadGroup                                            httpGroup = new ThreadGroup("Winnetou");
	InetAddress                                            addr;
	HTTPHandler                                            defaultHandler;
	
	public HTTPServer(int port) {
		this(null, port);
	}
	
	public HTTPServer(String host, int port) {
		handlers = new HashMap<Integer, HashMap<String, HTTPHandler>>();
		for (int i = HTTPHandler.METHODS.length; --i >= 0;)
			handlers.put(i, new HashMap<String, HTTPHandler>());
		try {
			this.port = port;
			server = host == null ? new ServerSocket(port) : new ServerSocket(port, 50, InetAddress.getByName(host));
			//server.setReuseAddress(true);

			addr = AddressUtilities.getDefaultInterface();

			Log.info("HTTP Server bound (" + addr + ":" + port + ")");
			serverThread = new Thread(httpGroup, this, "Server");
			serverThread.setPriority(Thread.MIN_PRIORITY);
		} catch (Exception e) {
			Log.severe(e);
		}
	}

	public void start() {
		if (serverThread != null)
			serverThread.start();
	}

	public String getHostAddress() {
		return addr.getHostAddress();
	}

	public int getPort() {
		return port;
	}

	public void addHandler(HTTPHandler handler, String path) {
		handlers.get(handler.handledMethod()).put(path, handler);
		handler.setRootPath(path);
	}

	public void removeHandler(HTTPHandler handler) {
		int method = handler.handledMethod();
		for (Iterator<String> i = handlers.get(method).keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			if (handlers.get(method).get(key) == handler)
				handlers.get(method).remove(key);
		}
	}

	public HTTPHandler getHandler(int method, URL url) {
		for (String path = url.getPath(); path != null; path = getParent(path)) {
			HTTPHandler handler = handlers.get(method).get(path);
			if (handler != null)
				return handler;
		}
		return defaultHandler;
	}

	public HTTPHandler getHandler(String methodStr, URL url) {
		int method = -1;
		for (int i = 0; i < HTTPHandler.METHODS.length; i++)
			if (methodStr.equals(HTTPHandler.METHODS[i])) {
				method = i;
				break;
			}
		return method != -1 ? getHandler(method, url) : null;
	}

	private String getParent(String path) {
		if (path.equals("/"))
			return null;
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		int idx = path.lastIndexOf('/');
		if (idx < 0)
			return null;
		return path.substring(0, idx + 1);
	}

	public HTTPHandler[] getHandlers(int method) {
		return handlers.get(method).values().toArray(new HTTPHandler[handlers.get(method).size()]);
	}

	/**
	 * Return a file for an URL.
	 * 
	 * @param url
	 *            The url that should be mapped to a file.
	 * @return The file for the given url or null if no mapping is possible.
	 */
	public File getFileForURL(URL url) {
		if (url.getProtocol().equals("file")) {
			try {
				return new File(new URI(url.getPath()));
			} catch (Exception e) {
				return null;
			}
		}
		HandlerGET handler = (HandlerGET) getHandler(HTTPHandler.GET, url);
		if (handler == null)
			return null;
		return handler.getFileForURL(url);
	}

	/**
	 * @param file
	 * @return
	 */
	public URL getURLforFile(File file) throws MalformedURLException {
		URL result = null;
		for (Iterator<HTTPHandler> i = handlers.get(HTTPHandler.GET).values().iterator(); i.hasNext();) {
			result = ((HandlerGET) i.next()).getURLforFile(file);
			if (result != null)
				break;
		}
		return result;
	}

	public void setDefaultHandler(HTTPHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}
	
	// Server thread

	@Override
	public void run() {
		while (true) {
			try {
				Socket client = server.accept();
				new HTTPRequest(client, this);
			} catch (IOException e) {
				Log.warning(e);
			}
		}
	}

	// simple server
	public static void main(String[] argv) throws Exception {
		if (argv.length != 2) {
			System.err.println("usage: " + HTTPServer.class.getName() + " <server-port> <server-root>");
			System.exit(1);
		}
		int port = Integer.parseInt(argv[0]);
		System.out.print("Starting up: " + AddressUtilities.getDefaultInterface() + ":" + port);
		HTTPServer server = new HTTPServer(port);
		server.addHandler(new HandlerGET(new File(argv[1]), server), "/");
		server.start();
		System.out.println("\nRunning.");
	}
}
