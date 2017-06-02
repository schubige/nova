package ch.digisyn.nova;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.corebounce.net.Log;

@SuppressWarnings("nls")
public class TCPServer extends Thread {
	public static final int PORT = 55555;
	
	private final ServerSocket socket;

	public TCPServer(String host, int port) throws UnknownHostException, IOException {
		super("TCPServer");
		setDaemon(true);

		socket = new ServerSocket(port, 50, host == null ? null : InetAddress.getByName(host));
		
		Log.info("Running TCP server on " + socket);
	}

	@Override
	public void run() {
		for(;;) {
			try {
				new TCPHandler(socket.accept());
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		TCPServer server = new TCPServer(args[0], PORT);
		server.setDaemon(false);
		server.start();
	}
}
