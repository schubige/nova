package ch.bluecc.nova;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.corebounce.util.Log;
import org.corebounce.util.TextUtilities;

import ch.bluecc.nova.content.Content;

@SuppressWarnings("nls")
public class TCPHandler extends Thread {
	private static final Set<String> NUMBERS_SET = new HashSet<String>();
	private static final Set<String> ANIMS_SET   = new HashSet<String>();
	private static final Set<String> CMD_SET     = new HashSet<String>();

	static {
		for(int i = 0; i < 255; i++)
			NUMBERS_SET.add(TextUtilities.toHex((byte)i));
		CMD_SET.add("lum");
		CMD_SET.add("col");
		CMD_SET.add("vel");
		CMD_SET.add("anim");
	}

	private final InputStream  in;
	private final OutputStream out;

	public TCPHandler(Socket socket) throws IOException {
		super("TCPHandler");
		setDaemon(true);
		in  = socket.getInputStream();
		out = socket.getOutputStream();
		
		StringBuilder items = new StringBuilder();
		for(Content content : NOVAControl.contents)
			items.append(content.toString()).append(',');
		if(items.length() > 0)
			items.setLength(items.length() - 1);
		for(String anim : items.toString().split("[,]"))
			ANIMS_SET.add(anim);
		out.write(items.toString().getBytes("UTF-8"));
		out.flush();
		start();
	}
	
	@Override
	public void run() {
		try {
			for(;;) {
				try {
				String scmd = consume(CMD_SET);
				if     ("lum".equals(scmd))  lum();
				else if("vel".equals(scmd))  vel();
				else if("col".equals(scmd))  col();
				else if("anim".equals(scmd)) anim();
				} catch(Throwable t) {}
				while(in.read() != ',') {}
			}
		} catch(Throwable t) {
			Log.severe(t);
		}
	}

	private String consume(Set<String> values) throws IOException {
		StringBuilder buffer = new StringBuilder();
		for(int i = 0; i < 80; i++) {
			int c = in.read();
			if(c < 0) throw new EOFException();
			buffer.append((char)c);
			String sbuffer = buffer.toString();
			if(values.contains(sbuffer))
				return sbuffer;
		}
		throw new IOException();
	}

	private void anim() throws IOException {
		String anim = consume(ANIMS_SET);
		for(int i = 0; i < NOVAControl.contents.size(); i++)
			if(NOVAControl.contents.get(i).toString().equals(anim)) {
				NOVAControl.setContent(i);
				break;
			}
	}

	private void col() throws IOException {
		int r = Integer.parseInt(consume(NUMBERS_SET), 16);
		int g = Integer.parseInt(consume(NUMBERS_SET), 16);
		int b = Integer.parseInt(consume(NUMBERS_SET), 16);
		NOVAControl.setRed  (r / 255.0);
		NOVAControl.setGreen(g / 255.0);
		NOVAControl.setBlue (b / 255.0);
	}

	private void lum() throws NumberFormatException, IOException {
		int l = Integer.parseInt(consume(NUMBERS_SET), 16);
		NOVAControl.setBrightness(l / 255.0);
	}

	private void vel() throws NumberFormatException, IOException {
		int v = Integer.parseInt(consume(NUMBERS_SET), 16);
		NOVAControl.setSpeed((v / 32.0) - 3.0);
		
	}
}
