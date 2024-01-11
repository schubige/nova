package org.corebounce.net.winnetou;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.corebounce.util.Log;
import org.corebounce.net.MIME;
import org.corebounce.util.Base64Decoder;
import org.corebounce.util.Base64FormatException;

/**
 * (c) 1999 - 2006 IIUF, DIUF, corebounce, swisscom innovations
 * <p>
 * 
 * HTTP request.
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class HTTPRequest implements Runnable {
	private static boolean DBG = false;

	private InputStream in;
	private OutputStream out;
	private Socket client;
	private HTTPServer server;
	private InternetHeaders header;
	private Multipart fields;

	public String[] tokens;

	public HTTPRequest(Socket client, HTTPServer server) {
		if (DBG)
			Log.info("new request:" + client);
		this.client = client;
		this.server = server;
		header = new InternetHeaders();
		new Thread(server.httpGroup, this, "HTTPRequest:" + client).start();
	}

	public HTTPRequest() {
	}

	OutputStream getOutput() throws IOException {
		if (out == null)
			out = client.getOutputStream();
		return out;
	}

	public InputStream getInput() {
		return in;
	}

	public String readln() throws IOException {
		StringBuffer result = new StringBuffer();
		int c = in.read();
		if (c == -1)
			return null;
		readloop: while (true) {
			switch (c) {
			case -1:
			case '\n':
				break readloop;
			case '\r':
				c = in.read();
				continue readloop;
			default:
				result.append((char) c);
			}
			c = in.read();
		}
		return result.toString();
	}

	public void writeln(String line) throws IOException {
		write(line + "\r\n");
	}

	public void writeln() throws IOException {
		write("\r\n");
	}

	public void write(String data) throws IOException {
		getOutput();
		out.write(data.getBytes());
		out.flush();
	}

	public void write(byte[] data) throws IOException {
		getOutput();
		out.write(data);
		out.flush();
	}

	public void write(byte[] data, int length) throws IOException {
		getOutput();
		out.write(data, 0, length);
		out.flush();
	}

	public Multipart getFields() {
		return fields;
	}

	public InternetHeaders getHeaders() {
		return header;
	}

	public String getHeaderField(String h) {
		String[] result = header.getHeader(h);
		if (result == null || result.length == 0)
			return null;
		return result[0];
	}

	public int getContentLength() {
		String length = getHeaderField("content-length");
		if (length == null)
			return -1;
		return Integer.parseInt(length);
	}

	public String getContentType() {
		String result = getHeaderField("content-type");
		if (result != null) {
			int idx = result.indexOf(';');
			if (idx >= 0)
				result = result.substring(0, idx);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public URL getReferer() throws MalformedURLException {
		return new URL(getHeaderField("referer"));
	}

	public String[] getCredentials() {
		String encoded = getHeaderField("Authorization");

		if (encoded != null && encoded.startsWith("Basic ")) {
			encoded = encoded.substring(6, encoded.length());
			try {
				Base64Decoder decoder = new Base64Decoder(encoded);
				String decoded = decoder.processString();
				String[] cred = new String[2];
				cred[0] = decoded.substring(0, decoded.indexOf(':'));
				cred[1] = decoded.substring(decoded.indexOf(':') + 1, decoded.length());
				return cred;
			} catch (Base64FormatException e) {
				Log.warning(e);
			}
		}
		return null;
	}

	@Override
	public void run() {
		try {
			in = client.getInputStream();

			String inline;

			// Skip empty lines

			do {
				inline = readln();
				if (DBG)
					Log.info(inline);
				if (inline == null)
					return;
			} while (inline.equals(""));

			// Tokenize line

			StringTokenizer tokenizer = new StringTokenizer(inline);
			tokens = new String[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				tokens[i++] = tokenizer.nextToken();
			}

			// Read remaining lines up to an empty line

			do {
				inline = readln();

				if (DBG)
					Log.info(inline);

				header.addHeaderLine(inline);
			} while (!inline.equals(""));

			// Get the method handler

			@SuppressWarnings("deprecation")
			HTTPHandler handler = server.getHandler(tokens[0], new URL("http", server.getHostAddress(), server.getPort(), tokens[1]));

			// Log.info("handler for " + tokens[0] + " " + tokens[1] +
			// ":" + handler);

			HTTPResponse response = handler == null ? new Response404(this) : handler.request(this);

			if (response != null) {
				response.respond();
			}
		} catch (Exception e) {
			Log.warning(e);
		} finally {
			try {
				in.close();
				if (out != null)
					out.close();
				client.close();
			} catch (Exception e) {
				Log.warning(e);
			}
		}
	}

	public void setFields(Multipart fields) {
		this.fields = fields;
	}

	public void addBodyPart(BodyPart part) throws MessagingException {
		if (fields == null)
			fields = new MimeMultipart();
		fields.addBodyPart(part);
	}

	public void addKeyValue(String key, String value) throws MessagingException {
		BodyPart part = new MimeBodyPart();
		part.setDisposition(MIME.Form_data);
		part.setHeader("name", "\"" + key + "\"");
		part.setText(value == null ? "" : value);
		addBodyPart(part);
	}

	public void addHeaderLine(String line) {
		if (header == null)
			header = new InternetHeaders();
		header.addHeaderLine(line);
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < tokens.length; i++)
			result += tokens[i] + " ";
		for (Enumeration<?> e = header.getAllHeaderLines(); e.hasMoreElements();)
			result += e.nextElement() + ",";
		result += "\n";
		try {
			if (fields != null) {
				for (int i = 0; i < fields.getCount(); i++) {
					BodyPart part = fields.getBodyPart(i);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					org.corebounce.io.Utilities.copy(part.getInputStream(), out, true);
					result += "[";
					result += part.getContentType() + ",";
					for (Enumeration<?> e = part.getAllHeaders(); e.hasMoreElements();) {
						Header header = (Header) e.nextElement();
						result += header.getName() + "=" + header.getValue() + ",";
					}
					result += new String(out.toByteArray());
					result += "]";
				}
			}
		} catch (Exception ex) {
			Log.warning(ex);
		}
		return result;
	}

	public void setInput(InputStream in) {
		this.in = in;
	}

	public void setOutput(OutputStream out) {
		this.out = out;
	}
}
