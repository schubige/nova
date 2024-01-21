package org.corebounce.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Log implementation.
 * <p>
 * 
 * (c) 2002, corebounce
 * <p>
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class Log {
	public static final String LOG = "org.corebounce.util";

	private static final boolean LOG_TO_FILE = false;

	static {
		if(LOG_TO_FILE) {
			FileHandler fh;
			SimpleDateFormat format = new SimpleDateFormat("MdHHmmss");
			try {
				fh = new FileHandler("NOVA_" + format.format(Calendar.getInstance().getTime()) + ".log");
				fh.setFormatter(new SimpleFormatter());
				Logger.getLogger(LOG).addHandler(fh);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	private static String getMsg(Throwable t) {
		String result = t instanceof InvocationTargetException ? ((InvocationTargetException) t).getTargetException().getMessage() : t.getMessage();
		return result + " -- " + getStackTrace(t);
	}

	public static void entering(Object o, String method) {
		Logger.getLogger(LOG).entering(o.getClass().getName(), method);
	}

	public static void exiting(Object o, String method) {
		Logger.getLogger(LOG).exiting(o.getClass().getName(), method);
	}

	public static void info(String msg) {
		Logger.getLogger(LOG).log(Level.INFO, msg);
	}

	public static void info(Throwable t) {
		Logger.getLogger(LOG).log(Level.INFO, getMsg(t), t);
	}

	public static void warning(String msg) {
		Logger.getLogger(LOG).log(Level.WARNING, msg);
	}

	public static void warning(Throwable t) {
		Logger.getLogger(LOG).log(Level.WARNING, getMsg(t), t);
	}

	public static void warning(Throwable t, String msg) {
		Logger.getLogger(LOG).log(Level.WARNING, msg, t);
	}

	public static void severe(String msg) {
		Logger.getLogger(LOG).log(Level.SEVERE, msg);
	}

	public static void severe(Throwable t) {
		Logger.getLogger(LOG).log(Level.SEVERE, getMsg(t), t);
	}
}
