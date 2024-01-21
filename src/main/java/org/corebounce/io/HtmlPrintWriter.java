package org.corebounce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

import org.corebounce.util.Log;
import org.corebounce.util.UTHTML;

/**
 * Html writer.
 * 
 * (c) 1999 - 2004 IIUF, DIUF
 * <p>
 * 
 * @author shoobee
 */

@SuppressWarnings("nls")
public class HtmlPrintWriter implements HTML {
	private Stack<String> tag_stack = new Stack<>();
	PrintWriter writer;

	public HtmlPrintWriter(String title, OutputStream out) throws IOException {
		writer = new PrintWriter(out);
		open(title);
	}

	public HtmlPrintWriter(String title, OutputStream out, boolean flush) throws IOException {
		writer = new PrintWriter(out, flush);
		open(title);
	}

	public HtmlPrintWriter(String title, Writer out) throws IOException {
		writer = new PrintWriter(out);
		open(title);
	}

	public HtmlPrintWriter(String title, Writer out, boolean flush) throws IOException {
		writer = new PrintWriter(out, flush);
		open(title);
	}

	public HtmlPrintWriter(String title, String[] body_opt, OutputStream out) throws IOException {
		writer = new PrintWriter(out);
		open(title, body_opt);
	}

	public HtmlPrintWriter(String title, String[] body_opt, OutputStream out, boolean flush) throws IOException {
		writer = new PrintWriter(out, flush);
		open(title, body_opt);
	}

	public HtmlPrintWriter(String title, String[] body_opt, Writer out) throws IOException {
		writer = new PrintWriter(out);
		open(title, body_opt);
	}

	public HtmlPrintWriter(String title, String[] body_opt, Writer out, boolean flush) throws IOException {
		writer = new PrintWriter(out, flush);
		open(title, body_opt);
	}

	public HtmlPrintWriter(OutputStream out) throws IOException {
		writer = new PrintWriter(out);
		println("<!doctype html>");
	}

	private synchronized void open(String title, String ... body_opt) throws IOException {
		println("<!doctype html>");
		tag(HTML.Html, "lang", "en");
		tag(HTML.Head);
		tag(HTML.Title);
		print(title);
		end(HTML.Title);
		end(HTML.Head);
		tag(HTML.Body, body_opt);
	}

	private void open(String title) throws IOException {
		open(title, new String[0]);
	}

	public synchronized void tag(String tag) {
		if (tag.charAt(0) == '/')
			tag_stack.push(tag);
		try {
			print("<" + tag.substring(1) + ">");
		} catch (StringIndexOutOfBoundsException e) {
			Log.severe(e);
		}
	}

	public synchronized void end(String tag) throws IOException {
		String top = tag_stack.pop();
		if (!tag.equals(top))
			throw new IOException("Tag mismatch:" + top + "," + tag);
		print("<" + tag + ">\n");
	}

	public synchronized void close() {
		while (!tag_stack.empty()) {
			try {
				end(tag_stack.peek());
			} catch (IOException e) {
				Log.severe(e);
			}
		}
		writer.close();
	}

	public synchronized void comment(String comment) {
		print("<!--" + comment + "-->");
	}

	public synchronized void script(String ... script) throws IOException {
		tag(HTML.Script, "language", "javascript", "type", "text/javascript");
		println("<!--");
		for(String line : script)
			println(line);
		println("// -->");
		end(HTML.Script);
	}

	public synchronized void style(String ... styles) throws IOException {
		tag(HTML.Style, "type", "text/css");
		println("<!--");
		for(String line : styles)
			println(line);
		println("-->");
		end(HTML.Style);
	}

	public synchronized void tag(String tag, String ... attributes) {
		if (tag.charAt(0) == '/')
			tag_stack.push(tag);
		try {
			print("<" + tag.substring(1));
		} catch (StringIndexOutOfBoundsException e) {
			Log.severe(e);
		}
		for (int i = 0; i < attributes.length; i += 2) {
			print("\n" + attributes[i] + "=" + "\"" + attributes[i+1] + "\"");
		}
		if (tag.charAt(0) == '/')
			print(">");
		else
			print("/>");
	}

	public synchronized void print(char c) {
		writer.print(UTHTML.trans.getNative("" + c));
	}

	public synchronized void print(char[] cs) {
		for (int i = 0; i < cs.length; i++)
			writer.print(UTHTML.trans.getNative("" + cs[i]));
	}

	public synchronized void print(String s) {
		if (s == null)
			writer.print("<null>");
		else
			writer.print(UTHTML.trans.getNative(s));
	}

	public synchronized void println(char c) {
		writer.println(UTHTML.trans.getNative("" + c));
	}

	public synchronized void println(char[] cs) {
		for (int i = 0; i < cs.length; i++)
			writer.println(UTHTML.trans.getNative("" + cs[i]));
	}

	public synchronized void println(String s) {
		if (s == null)
			writer.println("<null>");
		else
			writer.println(UTHTML.trans.getNative(s));
	}
}
