package org.corebounce.util;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("nls")
public class TextUtilities {
	static public final String HEXTAB   = "0123456789ABCDEF";
	
	public static String toHex(byte[] buffer) {
		return toHex(buffer, 0, buffer.length, 0);
	}
	
	public static String toHex(byte[] buffer, int off, int len) {
		return toHex(buffer, off, len, 0);
	}

	public static String toHex(byte[] buffer, int off, int len, int split) {
		return toHex(buffer, off, len, split, 0);
	}

	public static String toHex(byte[] buffer, int off, int len, int split, int newline) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i <len; i++) {
			if(split   != 0 && i != 0 && i % split   == 0) result.append(' ');
			if(newline != 0 && i != 0 && i % newline == 0) result.append('\n');
			result.append(HEXTAB.charAt((buffer[i + off] >> 4) & 0xF));
			result.append(HEXTAB.charAt(buffer[i + off] & 0xF));
		}
		return result.toString();
	}
	
	public static String toHex(byte b) {
		StringBuffer result = new StringBuffer();
		result.append(HEXTAB.charAt((b >> 4) & 0xF));
		result.append(HEXTAB.charAt(b & 0xF));
		return result.toString();
	}
	
	public static String toString(Object o) {
		return toString("[", ", ", "]", o);
	}
	
	public static final int NONE           = 0;
	public static final int QUOTE_STRINGS  = 1;
	public static final int FORMAT_NUMBERS = 2;
	
	public static String toString(String gOpen, String gSep, String gClose, Object o) {
		return toString(gOpen, gSep, gClose, o, QUOTE_STRINGS);
	}

	public static String toString(String gOpen, String gSep, String gClose, Object array, int flags, int offset, int length) {
		StringBuilder result = new StringBuilder(gOpen);
		for(int i = 0; i < length; i++)
			result.append((i == 0 ? ClassUtilities.EMPTY_String : gSep)).append(toString(gOpen, gSep, gClose, Array.get(array, offset + i), flags));
		return result.append(gClose).toString();
	}

	public static String toString(String gOpen, String gSep, String gClose, Object o, int flags) {
		if(o == null) return "null";
		else if(o instanceof Throwable) {
			return toString((Throwable)o, new StringBuilder()).toString();
		}
		else if(o instanceof Reference<?>)
			return "Ref:" + toString(gOpen, gSep, gClose, ((Reference<?>)o).get(), flags);
		else if(o instanceof String)
			return (flags & QUOTE_STRINGS) != 0 ? "\"" + o + "\"" : o.toString();
		else if(o.getClass().isArray()) {
			return toString(gOpen, gSep, gClose, o, flags, 0, Array.getLength(o));
		} else if(o instanceof Iterable<?>) {
			StringBuilder result = new StringBuilder(gOpen);
			int i = 0;
			for(Object item : ((Iterable<?>)o))
				result.append((i++ == 0 ? ClassUtilities.EMPTY_String : gSep) + toString(gOpen, gSep, gClose, item, flags));				
			return result.append(gClose).toString();
		} else if(o instanceof Map<?, ?>) {
			Map<?,?> m = (Map<?,?>)o;
			StringBuilder result = new StringBuilder(gOpen);
			int i = 0;
			for(Entry<?,?> entry : m.entrySet())
				result.append((i++ == 0 ? ClassUtilities.EMPTY_String : gSep) + toString(gOpen, gSep, gClose, entry.getKey(), flags) + "=" + toString(gOpen, gSep, gClose, entry.getValue(), flags));
			return result.append(gClose).toString();
		} else if((flags & FORMAT_NUMBERS) != 0 && o instanceof Number) 
			return new DecimalFormat(ClassUtilities.isIntegral(o.getClass()) ? "#,##0" : "#,##0.0000").format(((Number)o).doubleValue());
		return o.toString();
	}
	
	private static StringBuilder toString(Throwable t, StringBuilder result) {
		if(t.getMessage() != null) {
			result.append(t.getMessage());
			result.append(" (");
		} else {
			String name = t.getClass().getName();
			name        = name.substring(name.indexOf('.') + 1);
			for(int i = 1; i < name.length(); i++) {
				result.append(name.charAt(i - 1));
				if(         Character.isLowerCase(name.charAt(i - 1)) 
						&& (Character.isUpperCase(name.charAt(i)) || Character.isDigit(name.charAt(i))))
					result.append(' ');
			}
			result.append(name.charAt(name.length() - 1));
		}
		if(t.getMessage() != null)
			result.append(")");
		if(t.getCause() != null) {
			result.append('/');
			result.append(toString(t.getCause(), result));
		}
		return result;
	}

	public static int count(String str, char ch) {
		int result = 0;
		for(int i = str.length(); --i >= 0;)
			if(str.charAt(i) == ch)
				result++;
		return result;
	}

	public static int count(String string, String sub) {
		int start = 0;
		int end   = string.length();
		int n     = sub.length();
		if(n == 0)
			return 0;
		//return end - start + 1;

		int result = 0;
		while(true){
			int index = string.indexOf(sub, start);
			start = index + n;
			if(start > end || index == -1)
				break;
			result++;
		}
		return result;
	}

	public static String getFileNameWithoutExtension(String path) {
		return stripFileExtension(getFileName(path));
	}

	public static String getFileNameWithoutExtension(File file) {
		return stripFileExtension(file.getName());
	}
	
	public static String stripFileExtension(String name) {
		if(name == null) return null;
		int idx = name.lastIndexOf('.');
		return idx > 0 ? name.substring(0, idx) : name;
	}

	public static String getFileName(String path) {
		return path.substring(path.replace(File.separatorChar, '/').lastIndexOf('/') + 1, path.length());
	}
}
