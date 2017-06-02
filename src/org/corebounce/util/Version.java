package org.corebounce.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.corebounce.net.AddressUtilities;

/**
 * Class version interface.
 * <p>
 * 
 * (c) 2001 - 2004 DIUF, corebounce association
 * <p>
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class Version {
	public final static String fImplTitle = "__IMPL_TITLE__";
	public final static String fImplVendor = "__IMPL_VENDOR__";
	public final static String fImplVersion = "__IMPL_VERSION__";
	public final static String fImplRelease = "__IMPL_RELEASE__";
	public final static String fImplTag = "__IMPL_TAG__";
	public final static String fImplURL = "__IMPL_URL__";
	public final static String fImplCopyright = "__IMPL_COPYRIGHT__";
	public final static String fImplAuthor = "__IMPL_AUTHOR__";
	public final static String fImplBuild = "__IMPL_BUILD__";
	public final static String fImplDate = "__IMPL_DATE__";

	private String implTitle;
	private String implVendor;
	private String implVersion;
	private String implRelease;
	private String implTag;
	private String implURL;
	private String implCopyright;
	private String implAuthor;
	private int implBuild;
	private long implDate;

	private Version() {
	}

	public static Version getVersion(Class<?> cls) {
		Version result = new Version();
		try {
			result.implTitle = (String) cls.getDeclaredField(fImplTitle).get(null);
		} catch (Exception e) {
			result.implTitle = cls.getPackage().getImplementationTitle();
		}
		try {
			result.implVendor = (String) cls.getDeclaredField(fImplVendor).get(null);
		} catch (Exception e) {
			result.implVendor = cls.getPackage().getImplementationVendor();
		}
		try {
			result.implVersion = (String) cls.getDeclaredField(fImplVersion).get(null);
		} catch (Exception e) {
			result.implVersion = cls.getPackage().getImplementationVersion();
		}
		try {
			result.implRelease = (String) cls.getDeclaredField(fImplRelease).get(null);
		} catch (Exception e) {
		}
		try {
			result.implTag = (String) cls.getDeclaredField(fImplTag).get(null);
		} catch (Exception e) {
		}
		try {
			result.implURL = (String) cls.getDeclaredField(fImplURL).get(null);
		} catch (Exception e) {
		}
		try {
			result.implCopyright = (String) cls.getDeclaredField(fImplCopyright).get(null);
		} catch (Exception e) {
		}
		try {
			result.implAuthor = (String) cls.getDeclaredField(fImplAuthor).get(null);
		} catch (Exception e) {
		}
		try {
			result.implBuild = cls.getDeclaredField(fImplBuild).getInt(null);
		} catch (Exception e) {
		}
		try {
			result.implDate = cls.getDeclaredField(fImplDate).getLong(null);
		} catch (Exception e) {
		}
		return result;
	}

	public String getImplementationTitle() {
		return implTitle;
	}

	public String getImplementationVendor() {
		return implVendor;
	}

	public String getImplementationVersion() {
		return implVersion;
	}

	public String getImplementationRelease() {
		return implRelease;
	}

	public String getImplementationTag() {
		return implTag;
	}

	public String getImplementationURL() {
		return implURL;
	}

	public String getImplementationCopyright() {
		return implCopyright;
	}

	public String getImplementationAuthor() {
		return implAuthor;
	}

	public int getImplementationBuild() {
		return implBuild;
	}

	public long getImplementationDate() {
		return implDate;
	}

	@Override
	public String toString() {
		String result = "";
		result += "title       :" + implTitle;
		result += "\nvendor    :" + implVendor;
		result += "\nversion   :" + implVersion;
		result += "\nrelease   :" + implRelease;
		result += "\ntag       :" + implTag;
		result += "\nurl       :" + implURL;
		result += "\ncopyright :" + implCopyright;
		result += "\nauthor    :" + implAuthor;
		result += "\nbuild     :" + implBuild;
		result += "\ndate      :" + new Date(implDate);
		return result;
	}

	private static void wrongVersion(String version, int major, int minor, String wrongmsg, String reqmsg, int exitCode) {
		System.out.println(wrongmsg + version);
		System.out.println(reqmsg + major + "." + minor);
		System.exit(exitCode);
	}

	public static void ensureVersion(String version, int major, int minor, String wrongmsg, String reqmsg, int exitCode) {
		StringTokenizer st = new StringTokenizer(version, ".", false);
		String mj = st.nextToken();
		String mi = st.nextToken();
		if (mj.compareTo("" + major) == -1)
			wrongVersion(version, major, minor, wrongmsg, reqmsg, exitCode);
		if (mj.compareTo("" + major) == 0)
			if (mi.compareTo("" + minor) == -1)
				wrongVersion(version, major, minor, wrongmsg, reqmsg, exitCode);
	}

	static long date = 0;
	static String[] versions;

	public static String[] getPackageVersions() {
		if (versions == null) {
			for (Iterator<File> i = getClassSearchPath().iterator(); i.hasNext();) {
				File f = i.next();
				if (!f.exists())
					continue;
				if (isJar(f)) {
					if (f.lastModified() > date)
						date = f.lastModified();
				} else
					findNewestClass(f);
			}
			Package[] ps = Package.getPackages();
			versions = new String[ps.length];
			for (int i = ps.length; --i >= 0;) {
				String version = ps[i].getImplementationVersion();
				if (version == null)
					version = "<unknown>";
				versions[i] = ps[i].getName() + ";" + ps[i].getImplementationVersion();
			}
			Arrays.sort(versions);
		}
		return versions;
	}

	private static void findNewestClass(File f) {
		if (f.isFile()) {
			String name = f.getName();
			if (isClassFile(name)) {
				if (f.lastModified() > date)
					date = f.lastModified();
			}
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = files.length; --i >= 0;)
				findNewestClass(files[i]);
		}
	}

	public static Date getDate() {
		return new Date(date);
	}

	public static boolean isClassFile(String name) {
		return name.endsWith(".class") || name.endsWith(".CLASS");
	}

	public static boolean isJar(File f) {
		String name = f.getName().toUpperCase();
		return name.endsWith(".JAR") || name.endsWith(".ZIP");
	}

	public static HashSet<File> getClassSearchPath() {
		HashSet<File> result = new HashSet<File>();
		String[] classpath = System.getProperty("java.class.path", "").split(File.pathSeparator);
		for (int i = classpath.length; --i >= 0;) {
			try {
				result.add(new File(classpath[i]));
			} catch (Exception ex) {
			}
		}
		result = buildSearchPath(result, Version.class.getClassLoader());
		return result;
	}

	private static HashSet<File> buildSearchPath(HashSet<File> result, ClassLoader loader) {
		if (loader == null)
			return result;
		if (loader instanceof URLClassLoader) {
			URL[] urls = ((URLClassLoader) loader).getURLs();
			for (int i = urls.length; --i >= 0;) {
				if (urls[i].getProtocol().equals("file"))
					result.add(new File(AddressUtilities.URLDecode(urls[i].getFile())));
			}
		}
		return buildSearchPath(result, loader.getParent());
	}
}
