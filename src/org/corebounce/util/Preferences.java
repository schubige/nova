package org.corebounce.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Preferences implementation.
 * 
 * (c) 1999 - 2004, IIUF, DIUF, corebounce association
 * <p>
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class Preferences {
	static final String PREF_RESET = "preferences_reset";
	static private Vector<PreferencesStore> stores = new Vector<PreferencesStore>();
	private static Hashtable<Object, String> obj2path = new Hashtable<Object, String>();
	static Hashtable<String, Object> path2obj = new Hashtable<String, Object>();
	static Hashtable<String, String> defines = new Hashtable<String, String>();

	static {
		addStore(new SysPreferences());
		addDefine(PREF_RESET, "Reset preferences");
	}

	public static void addDefine(String define, String help) {
		defines.put(define + " (" + new Throwable().getStackTrace()[1].getClassName() + ")", help);
	}

	public static boolean isDefined(String define) {
		return get(define) != null;
	}

	public static String getDefinesHelp() {
		String result = "";
		for (Iterator<String> i = defines.keySet().iterator(); i.hasNext();) {
			String key = i.next();
			result += "-D" + key + "\n  " + defines.get(key) + "\n";
		}
		return result;
	}

	private static void loadAllClasses(HashSet<File> cp) {
		for (Iterator<File> i = cp.iterator(); i.hasNext();) {
			File base = i.next();
			loadAllClasses(base, base);
		}
	}

	private static void loadAllClasses(File base, File f) {
		if (Version.isJar(f)) {
			try {
				loadAllClasses(new ZipInputStream(new BufferedInputStream(new FileInputStream(f))));
			} catch (Exception ex) {
			}
		} else if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = fs.length; --i >= 0;)
				loadAllClasses(base, fs[i]);
		} else if (Version.isClassFile(f.getName())) {
			loadClass(f.getPath().substring(base.getPath().length() + 1));
		}
	}

	private static void loadAllClasses(ZipInputStream zin) throws IOException {
		while (true) {
			ZipEntry e = zin.getNextEntry();
			if (e == null)
				break;
			if (Version.isClassFile(e.getName()))
				loadClass(e.getName());
		}
		zin.close();
	}

	private static void loadClass(String name) {
		name = name.substring(0, name.length() - 6);
		name = name.replace('/', '.');
		name = name.replace('\\', '.');
		name = name.replace(':', '.');
		if (name.startsWith("java"))
			return;
		if (name.startsWith("sun."))
			return;
		if (name.startsWith("com.sun"))
			return;
		try {
			Class.forName(name);
		} catch (Throwable t) {
		}
	}

	public static void checkAndPrintHelp() {
		if (isDefined("help")) {
			loadAllClasses(Version.getClassSearchPath());
			System.out.println();
			System.out.println("Defines:");
			System.out.println("--------");
			System.out.println(getDefinesHelp());
			System.exit(0);
		}
	}

	public static void addStore(PreferencesStore store) {
		stores.insertElementAt(store, 0);
		if (isDefined(PREF_RESET)) {
			System.err.println("Resetting " + store + " preferences.");
			store.clear();
		}
	}

	public static void set(String key, Object value) {
		for (int i = 0; i < stores.size(); i++)
			(stores.elementAt(i)).set(key, value);
	}

	public static void remove(String key) {
		for (int i = 0; i < stores.size(); i++)
			(stores.elementAt(i)).remove(key);
		Object o = path2obj.get(key);
		if (o != null) {
			path2obj.remove(key);
			obj2path.remove(o);
		}
	}

	public static Object get(String key) {
		Object result = null;

		for (int i = 0; i < stores.size(); i++) {
			result = (stores.elementAt(i)).get(key);
			if (result != null)
				break;
		}

		return result;
	}

	public static Object[] getMulti(String prefix) {
		Vector<Object> result = new Vector<Object>();
		for (int i = 0; i < stores.size(); i++) {
			try {
				(stores.elementAt(i)).getMulti(prefix, result);
			} catch (Exception e) {
			}
		}
		return result.toArray();
	}

	public static Object get(String key, Object deflt) {
		Object result = get(key);
		if (result == null)
			set(key, deflt);
		result = get(key);
		return result == null ? deflt : result;
	}

	public static void store() {
		for (int i = 0; i < stores.size(); i++)
			(stores.elementAt(i)).store();
	}

	public synchronized static String getPath(Object o) {
		if (o == null)
			return "";
		String result = obj2path.get(o);
		if (result == null)
			return "";
		return result;
	}

	public synchronized static Object getObjectForPath(String path) {
		if (path == null)
			return null;
		return path2obj.get(path);
	}
}
