package org.corebounce.util;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * String utilities.
 * 
 * (c) 1999, 2000, 2001, 2002, IIUF, DIUF, corebounce
 * <p>
 * 
 * @author shoobee
 */
@SuppressWarnings("nls")
public class Strings {

	public static final String WS = " \t\r\n";

	public static int count(String str, char c) {
		int result = 0;
		int len = str.length();
		for (int i = 0; i < len; i++)
			if (str.charAt(i) == c)
				result++;
		return result;
	}

	private static final String[] EMPTY_STR_A = new String[0];
	private static final String EMPTY_STR = "";

	public static String[] split(String str, char splitchar) {
		if (str == null)
			return EMPTY_STR_A;

		int len = str.length();
		int count = 1;
		for (int i = 0; i < len; i++)
			if (str.charAt(i) == splitchar)
				count++;

		String[] result = new String[count];

		count = 0;
		int start = 0;
		for (int i = 0; i < len; i++)
			if (str.charAt(i) == splitchar) {
				result[count++] = start == i ? EMPTY_STR : str.substring(start, i);
				start = i + 1;
			}
		result[count] = str.substring(start, len);

		return result;
	}

	public static String cat(String[] strings, char catchar) {
		return cat(strings, 0, strings.length, catchar);
	}

	public static String cat(String[] strings, int start, char catchar) {
		return cat(strings, start, strings.length - start, catchar);
	}

	public static String cat(String[] strings, int start, int count, char catchar) {
		String result = "";
		for (int i = start; i < start + count; i++)
			result += (i != 0 ? "" + catchar : "") + strings[i];
		return result;
	}

	public static boolean contains(String[] strings, String that) {
		for (int i = 0; i < strings.length; i++)
			if (that.equals(strings[i]))

				return true;
		return false;
	}

	public static String array2str(String[] strs) {
		String result = "";
		for (int i = 0; i < strs.length; i++)
			result += "[" + i + "]\"" + strs[i] + "\"";
		return result;
	}

	public static String[] removeDuplicates(String[] strings) {
		Vector<String> result_v = new Vector<String>();
		for (int i = 0; i < strings.length; i++)
			if (!result_v.contains(strings[i]))
				result_v.addElement(strings[i]);
		String[] result = new String[result_v.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = result_v.elementAt(i);
		return result;
	}

	public static String replace(String in, String that, String by) {
		if (that.equals(""))
			return in;
		int index = in.indexOf(that);
		if (index == -1)
			return in;
		return in.substring(0, index) + by + replace(in.substring(index + that.length()), that, by);
	}

	public static String[] append(String[] array, String s) {
		String[] result = new String[array.length + 1];

		System.arraycopy(array, 0, result, 0, array.length);
		result[array.length] = s;

		return result;
	}

	public static String[] remove(String[] array, String s) {
		int count = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(s))
				count++;

		if (count == 0)
			return array;

		String[] result = new String[array.length - count];

		int i = 0;
		for (int j = 0; j < array.length; j++)
			if (!array[j].equals(s))
				result[i++] = array[j];

		return result;
	}

	/**
	 * Concatenates two String[] arrays.
	 * 
	 * @param array1
	 *            First array.
	 * @param array2
	 *            Second array.
	 */
	public static String[] arraycat(String[] array1, String[] array2) {
		String[] result = new String[array1.length + array2.length];

		int i = 0;

		for (int j = 0; j < array1.length; j++)
			result[i++] = array1[j];

		for (int j = 0; j < array2.length; j++)
			result[i++] = array2[j];

		return result;
	}

	/**
	 * Returns the given string minus <code>amount</code> chars.
	 * 
	 * If the length of the resulting string is negative, the empty String ("")
	 * is returnend.
	 * 
	 * @param str
	 *            The source string.
	 * @param amount
	 *            The number of charactersto remove at the right end.
	 * @return The truncated string.
	 */
	public static String rightTrunc(String str, int amount) {
		if (str.length() < amount)
			return "";
		return str.substring(0, str.length() - amount);
	}

	/**
	 * List of words separated by whitespace.
	 * 
	 * Shortcut for <code>words(string, Strings.WS)</code>.
	 * 
	 * @param string
	 *            The source string.
	 * @return The list of words.
	 */
	public static String[] words(String string) {
		return words(string, WS);
	}

	/**
	 * List of words separated by chars supplied from the
	 * <code>separators</code> string.
	 * 
	 * @param string
	 *            The source string.
	 * @param separators
	 *            The set of chars separating words.
	 * @return The list of words.
	 */
	public static String[] words(String string, String separators) {
		Vector<String> resultv = new Vector<String>();
		StringTokenizer st = new StringTokenizer(string, separators, false);

		while (st.hasMoreTokens())
			resultv.addElement(st.nextToken());

		String[] result = new String[resultv.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = resultv.elementAt(i);

		return result;
	}

	/**
	 * Snatch the chars form <code>snatchars</code> and replace them by
	 * <code>snatchar</code>.
	 * 
	 * @param string
	 *            The source string.
	 * @param snatchars
	 *            The chars to snatch.
	 * @param snatchar
	 *            The chat that will replace a sequence of snatchchars.
	 * @return The snatched string.
	 */
	public static String snatch(String string, String snatchchars, char snatchar) {
		StringTokenizer st = new StringTokenizer(string, snatchchars, false);

		String result = "";

		while (st.hasMoreTokens())
			result += st.nextToken() + snatchar;

		result = result.substring(0, result.length() - 1);

		return result.trim();
	}

	public static String repeat(String pattern, int repetitions) {
		String result = "";
		for (int i = 0; i < repetitions; i++)
			result += pattern;
		return result;
	}

	/**
	 * Remove all occurences of the characters in <code>filter</code>.
	 * 
	 * @param string
	 *            The source string.
	 * @param filter
	 *            The list of chars to remove as string.
	 * @return The source string with all occurences of the characters in
	 *         <code>filter</code> removed.
	 */
	public static String remove(String string, String filter) {
		String result = "";
		for (int i = 0; i < string.length(); i++)
			if (filter.indexOf(string.charAt(i)) == -1)
				result += string.charAt(i);

		return result;
	}

	/**
	 * Return only the chars contained in <code>filter</code>.
	 * 
	 * @param string
	 *            The source string.
	 * @param filter
	 *            The list of chars to filter with as string.
	 * @return The source string consisting only of chars from
	 *         <code>filter</code>.
	 */
	public static String filter(String string, String filter) {
		String result = "";
		for (int i = 0; i < string.length(); i++)
			if (filter.indexOf(string.charAt(i)) >= 0)
				result += string.charAt(i);

		return result;
	}

	public static String toHex(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++)
			result += toHex(str.charAt(i));
		return result;
	}

	public static String toHex(byte[] data) {
		return toHex(data, 0, data.length, 1, -1, " ");
	}

	public static String toHex(byte[] data, int off, int length) {
		return toHex(data, off, length, 1, -1, " ");
	}

	public static String toHex(byte[] data, int off, int length, int groupSize) {
		return toHex(data, off, length, groupSize, -1, " ");
	}

	public static String toHex(byte[] data, int off, int length, int groupSize, int bytesPerLine, String groupSeparator) {
		String result = "";
		int c = 0;
		for (int i = off; i < off + length; i++) {
			if (c != 0 && (c % groupSize == 0))
				result += groupSeparator;
			if (c != 0 && bytesPerLine != -1 && (c % bytesPerLine == 0))
				result += "\n";
			result += toHex(data[i]);
			c++;
		}
		return result;
	}

	public static String toHex(int i) {
		return toHex((byte) (i >> 24)) + toHex((byte) (i >> 16)) + toHex((byte) (i >> 8)) + toHex((byte) i);
	}

	public static String toHex(char c) {
		return toHex((byte) (c >> 8)) + toHex((byte) c);
	}

	private static final char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHex(byte b) {
		return new StringBuffer().append(HEXTAB[(b >> 4) & 0xF]).append(HEXTAB[b & 0xF]).toString();
	}

	public final static String   ILLEGAL_NAME_CHARS  = " \t\n\b#$%^&-+*=|<>[]{}()!?;,.\"\'`/";
	public final static String[] ILLEGAL_CLASS_NAMES = {
		"while",        "while_",
		"class",        "class_", 
		"else",         "else_",
		"extends",      "extends_",
		"if",           "if_",
		"import",       "import_",
		"int",          "int_",
		"new",          "new_",
		"null",         "null_",
		"private",      "private_",
		"public",       "public_",
		"return",       "return_",
		"this",         "this_",
		"void",         "void_",
		"abstract",     "abstract_",
		"boolean",      "boolean_",
		"break",        "break_",
		"byte",         "byte_",
		"byvalue",      "byvalue_",
		"case",         "case_",
		"cast",         "cast_",
		"catch",        "catch_",
		"char",         "char_",
		"const",        "const_",
		"continue",     "continue_",
		"default",      "default_",
		"do",           "do_",
		"double",       "double_",
		"false",        "false_",
		"final",        "final_",
		"finally",      "finally_",
		"float",        "float_",
		"for",          "for_",
		"future",       "future_",
		"generic",      "generic_",
		"goto",         "goto_",
		"implements",   "implements_",
		"inner",        "inner_",
		"instanceof",   "instanceof_",
		"interface",    "interface_",
		"long",         "long_",
		"native",       "native_",
		"operator",     "operator_",
		"outer",        "outer_",
		"package",      "package_",
		"protected",    "protected_",
		"rest",         "rest_",
		"short",        "short_",
		"static",       "static_",
		"super",        "super_",
		"switch",       "switch_",
		"synchronized", "synchronized_",
		"throw",        "throw_",
		"throws",       "throws_",
		"transient",    "transient_",
		"true",         "true_",
		"try",          "try_",
		"var",          "var_",
		"volatile",     "volatile_"};

	public static String toClassName(String string) {
		if(string == null) return null;
		String result = "";
		if(string.charAt(0) >= '0' && string.charAt(0) <= '9')
			result += "_";
		for(int i = 0; i < string.length(); i++)
			result += string.charAt(i) >= 128 ? '_' : string.charAt(i);
			for(int i = 0; i < ILLEGAL_CLASS_NAMES.length; i += 2)
				if(ILLEGAL_CLASS_NAMES[i].equals(string))
					result = ILLEGAL_CLASS_NAMES[i + 1];
			try{
				for(int i = 0; i < ILLEGAL_NAME_CHARS.length(); i++)
					result = result.replace(ILLEGAL_NAME_CHARS.charAt(i), '_');
			} catch(StringIndexOutOfBoundsException e) {e.printStackTrace();}

			String[] sresult = split(result, '_');

			result = "";
			for(int i = 0; i < sresult.length; i++)
				if(sresult[i].length() < 2)
					result += "_" + sresult[i];
				else {
					if(i == 0)
						result += Character.toUpperCase(sresult[i].charAt(0)) + sresult[i].substring(1);
					else
						if(Character.isLowerCase(sresult[i - 1].charAt(sresult[i - 1].length() - 1)))
							result += Character.toUpperCase(sresult[i].charAt(0)) + sresult[i].substring(1);
						else
							result += "_" + sresult[i];
				}

			return result;
	}
}
