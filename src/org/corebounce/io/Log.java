package org.corebounce.io;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
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
public class Log {
	public static final String LOG = "org.corebounce.io";

	private static String getMsg(Throwable t) {
		return t instanceof InvocationTargetException ? ((InvocationTargetException) t).getTargetException().getMessage() : t.getMessage();
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
		System.err.println("SEVERE:" + msg);
		Logger.getLogger(LOG).log(Level.SEVERE, msg);
	}

	public static void severe(Throwable t) {
		System.err.println("SEVERE:" + getMsg(t));
		t.printStackTrace(System.err);
		Logger.getLogger(LOG).log(Level.SEVERE, getMsg(t), t);
	}
}
