package org.corebounce.util;

/**
 * Unicode translator.
 * 
 * (c) 1999 - 2005, IIUF, DIUF, corebounce association
 * <p>
 * 
 * @see org.corebounce.util.UnicodeTranslator
 * @author shoobee
 */
@SuppressWarnings("nls")
public class UnicodeTrans implements Unicode {

	public static String[] HTML2UNICODE = UTHTML.HTML2UNICODE;
	public static String[] UNICODE2HTML = UTHTML.UNICODE2HTML;

	public static String _trans(String string, String[] table) {
		String result = string;
		for (int i = 0; i < table.length; i += 2)
			result = Strings.replace(result, table[i], table[i + 1]);
		return result;
	}

	public static String decode(String s) {
		if (s.length() == 0)
			return s;
		if (s.charAt(0) == 0xFEFF)
			return s.substring(1);
		if (s.charAt(0) == 0xFFFE) {
			String result = "";
			for (int i = 1; i < s.length(); i++)
				result += (char) ((s.charAt(i) >>> 8) | (s.charAt(i) << 8));
			return result;
		}
		if (s.length() > 1) {
			if (s.charAt(0) == 0xFE && s.charAt(1) == 0xFF) {
				String result = "";
				for (int i = 2; i < s.length(); i += 2)
					result += (char) (s.charAt(i + 1) | (s.charAt(i) << 8));
				return result;
			}
			if (s.charAt(0) == 0xFF && s.charAt(1) == 0xFE) {
				String result = "";
				for (int i = 2; i < s.length(); i += 2)
					result += (char) (s.charAt(i) | (s.charAt(i + 1) << 8));
				return result;
			}
		}
		return s;
	}
}

