package com.nkhoang.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * String utility functions.
 * <p/>
 * <pre>
 *
 *  String [] data = { "LastName", "FirstName", "SSN", "ADDR1", "CITY", "STATE", "ZIP", "PHONE", "FAX" };
 *  String tabDelimitedLine = StringUtil.join( "\t", data );
 *
 * String dirty = "  \t This is some\t\t\t\n data    ";
 * String clean = StringUtil.condenseWhiteSpace( dirty ).trim();
 *
 * </pre>
 */
public class StringUtil {
	private static final Log LOG = LogFactory.getLog(StringUtil.class);


	/**
	 * Returns the Java literal which could be used in source code that represents
	 * the given string.
	 *
	 * @param str - any string
	 *
	 * @return the given string surrounded in double-quotes with any odd
	 *         characters escaped, or "null" without quotes if the given string is null.
	 */
	public static String inspect(String str) {
		return str == null ? "null" : "\"" + StringEscapeUtils.escapeJava(str) + "\"";
	}

	/**
	 * Case insensitve <code>String.startsWith</code> method.
	 *
	 * @param str    - any string
	 * @param prefix - any string
	 *
	 * @return <li>false if <code>str</code> is <code>null</code>.</li>
	 *         <li>false if <code>prefix</code> is <code>null</code>.</li>
	 *         <li>false if <code>str.length</code> is less than
	 *         <code>prefix.length</code>.</li>
	 *         <li>false if <code>str</code> does not start with prefix (case insensitve)
	 *         <li>true otherwise</li>
	 *
	 * @deprecated use StringUtils.startsWithIgnoreCase method in apache commons-lang
	 */
	@Deprecated
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null || str.length() < prefix.length()) {
			return false;
		} else {
			return prefix.equalsIgnoreCase(str.substring(0, prefix.length()));
		}
	}

	/**
	 * Case insensitve <code>Collections.contains</code> method for strings.
	 *
	 * @param collection - a collection of strings
	 * @param str        - any strings
	 *
	 * @return <li>false if <code>collection</code> is <code>null</code>.</li>
	 *         <li><code>collection.contains(str)</code> if
	 *         <code>str</code> is null.</li>
	 *         <li>false if <code>collection</code> does not contain a string that equals
	 *         (case insensitve) to str.</li>
	 *         <li>true otherwise</li>
	 */
	public static boolean containsIgnoreCase(
		Collection<String> collection, String str) {
		if (collection == null) return false;
		if (str == null) {
			return collection.contains(str);
		}

		for (String s : collection) {
			if (str.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Simple utility function to join an array of data into a single
	 * delimited string.
	 *
	 * @param delim the delimiter
	 * @param args  the data to concatenate together.
	 */
	public static String join(String delim, String... args) {
		if (ArrayUtils.isEmpty(args)) {
			return "";
		}
		StringBuilder buff = new StringBuilder();
		int i;
		for (i = 0; i < (args.length - 1); ++i) {
			buff.append(StringUtils.defaultString(args[i]));
			buff.append(delim);
		}
		buff.append(StringUtils.defaultString(args[i]));
		return buff.toString();
	}


	/**
	 * Simple utility function to join an array of data into a single
	 * delimited string.
	 *
	 * @param delim the delimiter
	 * @param args  the data to concatenate together.
	 *
	 * @deprecated use {@link #join(String, Iterable)} instead
	 */
	@Deprecated
	public static String join(String delim, List args) {
		return join(delim, (Collection) args);
	}

	/**
	 * Simple utility function to join an array of data into a single
	 * delimited string.
	 *
	 * @param delim the delimiter
	 * @param args  the data to concatenate together.
	 */
	public static String join(String delim, Iterable args) {
		if (null == args) {
			return "";
		}
		StringBuilder buff = new StringBuilder();
		boolean addDelim = false;
		Iterator i = args.iterator();
		while (i.hasNext()) {
			if (addDelim) {
				buff.append(delim);
			} else {
				addDelim = true;
			}
			buff.append(i.next());
		}

		return buff.toString();
	}

	/**
	 * Simple utility function to join a two-dimensional array of data into a
	 * single delimited string.
	 *
	 * @param delim1 the 'outer' delimiter
	 * @param delim2 the 'inner' delimiter
	 * @param data   the data to concatenate togeather.
	 */
	public static String join(String delim1, String delim2, String data[][]) {
		if (ArrayUtils.isEmpty(data)) {
			return "";
		}

		StringBuilder buff = new StringBuilder();
		int i;
		for (i = 0; i < data.length - 1; ++i) {
			buff.append(join(delim2, data[i]));
			buff.append(delim1);
		}
		buff.append(join(delim2, data[i]));
		return buff.toString();
	}

	/**
	 * Naieve html stripping.  Doesn't support
	 * comments surrounding other HTML tags.
	 *
	 * @deprecated use {@link HtmlUtil#stripSimpleHtml} instead
	 */
	@Deprecated
	public static String stripSimpleHtml(String data) {
		return HtmlUtil.stripSimpleHtml(data);
	}

	/**
	 * Naive breaking whitespace stripper.
	 * Embeded newlines/carrige returns/form feeds will be
	 * escaped with a '\n', '\r', and '\f' respectively
	 */
	private static String CR = "\r";
	private static String LF = "\n";
	private static String FF = "\f";

	private static String CRESC = "\\\\r";
	private static String LFESC = "\\\\n";
	private static String FFESC = "\\\\f";

	public static String escapeCrlfs(String data) {
		data = data.replaceAll(LF, LFESC);
		data = data.replaceAll(CR, CRESC);
		data = data.replaceAll(FF, FFESC);
		return data;
	}


	/**
	 * Encode all occurences of string vals found with URL style encoded values.
	 * <p/>
	 * Non-ASCII characters are first encoded as sequences of
	 * two or three bytes, using the UTF-8 algorithm, before being
	 * encoded as %HH escapes.
	 *
	 * @param str  the original string
	 * @param vals the characters to encode
	 *
	 * @return the 'encoded' string
	 */
	public static String hexEncodeChars(String str, char... vals) throws UnsupportedEncodingException {
		String encodedVals = "";
		StringBuilder sbuf = new StringBuilder();
		for (char c : vals) {
			byte[] x = String.valueOf(c).getBytes("UTF-8");
			char[] enc = Hex.encodeHex(x);
			sbuf.append("%");
			sbuf.append(new String(enc));
		}
		encodedVals = sbuf.toString();
		str = str.replaceAll(Pattern.quote(new String(vals)), Matcher.quoteReplacement(encodedVals));
		return str;
	}

	/**
	 * Strips occurances of multiply occurring whitespace down to a
	 * single character, that is the same as the first whitespace character
	 * encoutered.
	 *
	 * @param data the string to strip
	 *
	 * @return the 'cleaned' string.
	 */
	public static String condenseWhiteSpace(String data) {
		if (null == data || 0 == data.length()) {
			return data;
		}
		char[] chars = data.toCharArray();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < chars.length; ++i) {
			//append the charcter
			result.append(chars[i]);
			if (Character.isWhitespace(chars[i])) {
				//move past all additional whitespace
				do {
					i++;
				} while (i < chars.length && Character.isWhitespace(chars[i]));
				if (i < chars.length) {
					result.append(chars[i]);
				}
			}
		}
		return result.toString();
	}

	/**
	 * Decode HTML ligatures (like &amp;).
	 * See also: <a href="http://www.w3.org/TR/html401/sgml/entities.html">W3.org's list of HTML Entities</a>.
	 *
	 * @param data - the string to decode.
	 *
	 * @return the decoded string
	 *
	 * @deprecated use {@link HtmlUtil#htmlDecode} instead
	 */
	@Deprecated
	public static String htmlDecode(String data) {
		return HtmlUtil.htmlDecode(data);
	}


	/**
	 * Simplisitic string cleaning.  If the string is null, then an
	 * empty string will be returned.  Otherwise, stripSimpleHtml,
	 * condenseWhiteSpace, and then trim() are all used to clean the string.
	 *
	 * @param data the string to clean
	 *
	 * @return the clenaed data.
	 *
	 * @deprecated use {@link HtmlUtil#simpleCleanString} instead
	 */
	@Deprecated
	public static String simpleCleanString(String data) {
		return HtmlUtil.simpleCleanString(data);
	}

	/**
	 * Simplistic string cleaning.  Calls simpleCleanString on each string
	 * in the array.
	 *
	 * @param data the strings to clean
	 *
	 * @return the cleaned data.
	 *
	 * @deprecated use {@link HtmlUtil#simpleCleanStrings(String[])} instead
	 */
	@Deprecated
	public static String[] simpleCleanStrings(String data[]) {
		return HtmlUtil.simpleCleanStrings(data);
	}

	/**
	 * Clean a two dimensional array of string data.
	 *
	 * @param data the string array to clean
	 *
	 * @return the string array that was cleaned.
	 *
	 * @deprecated use {@link HtmlUtil#simpleCleanStrings(String[][])} instead
	 */
	@Deprecated
	public static String[][] simpleCleanStrings(String data[][]) {
		return HtmlUtil.simpleCleanStrings(data);
	}

	/**
	 * Safely copy elements from src into dst, where src is not null, upto and
	 * including the length of src, any elements in dst beyond the length of src
	 * are filled with an empty string.
	 *
	 * @param src the source array
	 * @param dst the destination array
	 */
	public static void copyArray(String src[], String dst[]) {
		int i = 0;
		for (i = 0; i < src.length && i < dst.length; ++i) {
			dst[i] = null == src[i] ? "" : src[i];
		}
		while (i < dst.length) {
			dst[i++] = "";
		}
	}

	/** @deprecated use {@link TextUtil#infixCap} instead */
	@Deprecated
	public static String infixCap(String s) {
		return TextUtil.infixCap(s);
	}

	/** @deprecated use {@link TextUtil#capitalize} instead */
	@Deprecated
	public static String capitalize(String s) {
		return TextUtil.capitalize(s);
	}

	/**
	 * Camel case a string that is separated by either _ or -.  Just a quick
	 * function that camel cases a string with two more common delimiters
	 *
	 * @return If str contains _ or - then a camel cased version of it.
	 *         Otherwise the string, lowercased and capitalized.
	 *
	 * @deprecated use {@link TextUtil#camelCase(String)} instead
	 */
	@Deprecated
	public static String camelCase(String str) {
		return TextUtil.camelCase(str);
	}

	/**
	 * Camel case a string separated by the given delimiter
	 *
	 * @deprecated use {@link TextUtil#camelCase(String, String)} instead
	 */
	@Deprecated
	public static String camelCase(String str, String delimiter) {
		return TextUtil.camelCase(str, delimiter);
	}


	/** @deprecated Use StringUtils.rightPad(String str, int size) instead */
	@Deprecated
	public static String rpad(String str, int length) {
		return StringUtils.rightPad(str, length);
	}

	/** @deprecated use StringUtils.isBlank() or StringUtils.isEmpty() instead. */
	@Deprecated
	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	/**
	 * If the given string is "empty" according to {@link #isEmpty(String)} returns a
	 * <code>null</code> string, otherwise returns the given string.
	 * TODO: Rename to nullIfBlank, and use StringUtils.isBlank
	 */
	public static String nullIfEmpty(String s) {
		if (isEmpty(s)) {
			return null;
		}
		return s;
	}

	/**
	 * If the "first" string Equals any of the other strings ignoring case.
	 * <p/>
	 * if only one parameter is supplied false is returned.
	 *
	 * @param firstStr the String that is being compared to the other strings
	 * @param otherStr the Strings that the first string need to equal at least one of.
	 *
	 * @return true, if firstStr equals any of the following strings.  Otherwise false is returned.
	 */
	public static boolean equalsAnyIgnoreCase(String firstStr, String... otherStr) {
		for (String s : otherStr) {
			if (StringUtils.equalsIgnoreCase(firstStr, s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return s with URLs surrounded by a href elements, suitable for display
	 *         in a web page
	 *
	 * @deprecated use {@link HtmlUtil#linkURLs} instead
	 */
	@Deprecated
	public static String linkURLs(String s) {
		return HtmlUtil.linkURLs(s);
	}

	/**
	 * Wrap a string at a certain column size by inserting line breaks
	 * without breaking apart words unless the word is longer than the
	 * column size.
	 * For example:	<pre>
	 * wrapAt("ABC DE F", 1)
	 * A
	 * B
	 * C
	 * D
	 * E
	 * F
	 * wrapAt("ABC DE F", 2)
	 * AB
	 * C
	 * DE
	 * F
	 * wrapAt("ABC DE F", 3)
	 * ABC
	 * DE
	 * F
	 * wrapAt("ABC DE F", 4)
	 * ABC
	 * DE F
	 * wrapAt("ABC DE F", 5)
	 * ABC
	 * DE F
	 * wrapAt("ABC DE F", 6)
	 * ABC DE
	 * F
	 * wrapAt("ABC DE F", 7)
	 * ABC DE
	 * F
	 * wrapAt("ABC DE F", 8)
	 * ABC DE F	</pre>
	 *
	 * @param s     The string to wrap
	 * @param chars The number of characters to wrap the string at
	 */
	public static String wrapAt(String s, int chars) {
		if (s == null) {
			return null;
		}
		StringBuilder rtn = new StringBuilder();
		wrapAt(rtn, s, chars);
		return rtn.toString();
	}

	private static void wrapAt(StringBuilder buffer, String left, int chars) {
		left = left.trim();
		if (left.length() <= chars) {
			buffer.append(left);
		} else {
			String max = left.substring(0, chars + 1);
			int index = max.lastIndexOf(' ');
			if (index == -1) {
				buffer.append(max);
				buffer.append("\n");
				wrapAt(buffer, left.substring(chars), chars);
			} else {
				buffer.append(max.substring(0, index));
				String next = max.substring(index + 1) + left.substring(chars + 1);
				if (next.length() > 0) {
					buffer.append("\n");
				}
				wrapAt(buffer, next, chars);
			}
		}
	}

	public static String truncate(String s, int max) {
		if (s == null) {
			return null;
		}
		String rtn = s;
		if (s.length() > max) {
			rtn = s.substring(0, max);
			if (LOG.isDebugEnabled()) {
				LOG.debug(s + " being truncated to " + rtn);
			}
		}
		return rtn;
	}


	public static int indexOfIgnoreCase(String source, String searchFor) {
		return indexOfIgnoreCase(source, searchFor, 0);
	}

	public static int indexOfIgnoreCase(String source, String searchFor, int fromIndex) {
		return indexOfIgnoreCase(
			source.toCharArray(), 0, source.length(), searchFor.toCharArray(), 0, searchFor.length(), fromIndex);
	}

	private static int indexOfIgnoreCase(
		char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount,
		int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			if (Character.toLowerCase(source[i]) != Character.toLowerCase(first)) {
				while (++i <= max && Character.toLowerCase(source[i]) != Character.toLowerCase(first)) ;
			}
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && Character.toLowerCase(
					source[j]) == Character.toLowerCase(target[k]); j++, k++)
					;
				if (j == end) {
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public static int lastIndexOfIgnoreCase(String source, String searchFor) {
		return lastIndexOfIgnoreCase(source, searchFor, source.length());
	}

	public static int lastIndexOfIgnoreCase(
		String source, String searchFor, int fromIndex) {
		return lastIndexOfIgnoreCase(
			source.toCharArray(), 0, source.length(), searchFor.toCharArray(), 0, searchFor.length(), fromIndex);
	}

	private static int lastIndexOfIgnoreCase(
		char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount,
		int fromIndex) {
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar:
		while (true) {
			while (i >= min && Character.toLowerCase(source[i]) != Character.toLowerCase(strLastChar)) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (Character.toLowerCase(source[j--]) != Character.toLowerCase(target[k--])) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}


	/**
	 * Finds the first occurrence of the search string within the source string,
	 * ignoring any whitespace in both strings. The returned index will point at
	 * the first matching non-whitespace character in the source string, -1 if
	 * the search string could not be found.
	 * <p/>
	 * Why would you ever want such a thing? Consider the case where you have a
	 * GUI component for editing text, with the opportunity to reformat that
	 * text. It's nice if you can preserve the user's cursor location even after
	 * formatting the content. By passing the trailing portion of the original
	 * content as the search string, and the formatted content as the source
	 * string, you can do this.
	 */
	public static int indexOfIgnoreWhitespace(String source, String search) {
		// convert the search string into a regexp where there's an optional space
		// between every character
		StringBuilder regex = new StringBuilder(search.length() * 4);
		for (int ii = 0; ii < search.length(); ii++) {
			char c = search.charAt(ii);
			if (!Character.isWhitespace(c)) {
				regex.append(c);
				regex.append("\\s*");
			}
		}

		Matcher m = Pattern.compile(regex.toString()).matcher(source);
		if (!m.find(0)) {
			return -1;
		}

		return m.start();
	}


	/**
	 * Performs multiple <code>String.indexOf()</code> calls, one per search
	 * string, where each call uses the index returned by the previous call.
	 * Returns -1 if unable to find any of the desired strings.
	 * <p/>
	 * This method is null-safe: it returns -1 if any of the passed strings
	 * are <code>null</code>.
	 */
	public static int linkedIndexOf(String src, String... searchFor) {
		if (src == null) {
			return -1;
		}

		int index = 0;
		for (String search : searchFor) {
			if (search == null) {
				return -1;
			}
			index = src.indexOf(search, index);
			if (index < 0) {
				return -1;
			}
		}
		return index;
	}


	/**
	 * Finds the first index of any of the specified characters within the
	 * search string, -1 if none of the characters are found. This method
	 * is null-safe: it returns -1 if either <code>src</code> or <code>chars
	 * </code> is null.
	 * <p/>
	 * Jakarta Commons-lang provides an <code>indexOfAny()</code> method
	 * that (as of release 2.1) starts from the beginning of the string. If
	 * they add in one that starts from a known point, use it instead.
	 */
	public static int indexOfAny(String src, String chars, int fromIndex) {
		if ((src == null) || (chars == null)) {
			return -1;
		}

		for (int ii = fromIndex; ii < src.length(); ii++) {
			for (int jj = 0; jj < chars.length(); jj++) {
				if (src.charAt(ii) == chars.charAt(jj)) {
					return ii;
				}
			}
		}

		return -1;
	}

	/**
	 * Checks whether <code>line</code> only contains plain text characters,
	 * as defined as:
	 * <ul>
	 * <li>ASCII printable characters (Decimals 32-126)</li>
	 * <li>Horizontal Tab Control Character (^I, \t, Decimal 9)</li>
	 * <li>Line Feed Control Character (^J, \n, Decimal 10)</li>
	 * <li>Carriage Return Control Character (^I, \r Decimal 13)</li>
	 * </ul>
	 * <p/>
	 * This method is null-safe: it returns false if <code>line</code>
	 * is <code>null</code>.
	 */
	public static boolean isPlainText(String line) {
		if (line == null) {
			return false;
		}
		for (char c : line.toCharArray()) {
			if ((c < 32 || c > 126) && c != 9 && c != 10 && c != 13) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("char is " + c + " which is not a plain text character");
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>Quote <code>anyObject</code> using <code>quotes</code> for the
	 * quotation string. This method is imilar to {@link #q(Object)}, but allows
	 * caller to specify the string that will be used to quote
	 * <code>anyObject</code>. Note that <code>quotes</code> <em>may</em> be a
	 * multi character string.</p>
	 * <p/>
	 * <p>Examples:
	 * <pre>
	 *     q("\"", null)    // yields: "null"
	 *     q("\"", "foo")   // yields: "\"foo\""
	 *     q("***", "foo")  // yields: "\"***foo***\""
	 * </pre>
	 * </p>
	 *
	 * @param quotes    The string to "quote" <code>anyObject</code> with. May
	 *                  <em>not</em> be <code>null</code>.
	 * @param anyObject The object whose value will be converted to a quoted string.
	 *
	 * @see #q(Object anyObject)
	 * @see #q(String left, String right, Object anyObject)
	 * @see #q(Character quoteChar, Object anyObject)
	 * @see #q(Character left, Character right, Object anyObject)
	 */
	public static String q(String quotes, Object anyObject) {
		if (null == quotes) {
			throw new IllegalArgumentException("null provided for required 'quotes' param");
		}
		return q(quotes, quotes, anyObject);
	}

	/**
	 * <p>Quote <code>anyObject</code> using <code>left</code> and
	 * <code>right</code> as the left and right quote marks, respectively. This
	 * method is similar to {@link #q(Object)}, but allows caller to specify
	 * (independently) Strings that will be used for the left and right quotes
	 * around <code>anyObject</code>. Note that <code>left</code> and/or
	 * <code>right</code> <em>may</em> be multi character strings.</p>
	 * <p/>
	 * <p>Examples:
	 * <pre>
	 *     q("\"",  "\"",  null)    // yields: "null"
	 *     q("\"",  "\"",  "foo")   // yields: "\"foo\""
	 *     q("X",   "Y",   "foo")   // yields: "XfooY"
	 *     q("&gt;&gt;&gt;", "&lt;&lt;&lt;", "foo")   // yields: "&gt;&gt;&gt;foo&lt;&lt;&lt;"
	 *     q("&lt;elem&gt;", "&lt;/elem&gt;", "foo")   // yields: "&lt;elem&gt;foo&lt;/elem&gt;"
	 * </pre>
	 * </p>
	 *
	 * @param left      The string to use as the left-hand "quote" for
	 *                  <code>anyObject</code> with. May <em>not</em> be
	 *                  <code>null</code>.
	 * @param right     The string to use as the right-hand "quote" for
	 *                  <code>anyObject</code> with. May <em>not</em> be
	 *                  <code>null</code>.
	 * @param anyObject The object whose value will be converted to a quoted string.
	 *
	 * @see #q(Object anyObject)
	 * @see #q(String quotes, Object anyObject)
	 * @see #q(Character quoteChar, Object anyObject)
	 * @see #q(Character left, Character right, Object anyObject)
	 */
	public static String q(String left, String right, Object anyObject) {
		if (null == left) {
			throw new IllegalArgumentException("null provided for required 'left' param");
		}
		if (null == right) {
			throw new IllegalArgumentException("null provided for required 'right' param");
		}
		if (null == anyObject) {  // special case
			return "null";
		}
		return new StringBuilder(left).append(anyObject).append(right).toString();
	}

	/**
	 * <p>Quote <code>anyObject</code> with <code>quoteChar</code>. This method
	 * is similar to {@link #q(Object)}, but allows caller to specify the {@link
	 * Character} that will be used to quote <code>anyObject</code>.</p>
	 * <p/>
	 * <p>Examples:
	 * <pre>
	 *     q('"', null)    // yields: "null"
	 *     q('"', "foo")   // yields: "\"foo\""
	 *     q('X', "foo")   // yields: "XfooX"
	 * </pre>
	 * </p>
	 *
	 * @see #q(Object anyObject)
	 * @see #q(String quotes, Object anyObject)
	 * @see #q(String left, String right, Object anyObject)
	 * @see #q(Character left, Character right, Object anyObject)
	 */
	public static String q(Character quoteChar, Object anyObject) {
		if (null == quoteChar) {
			throw new IllegalArgumentException("null provided for required 'quoteChar' param");
		}
		return q(quoteChar.toString(), anyObject);
	}

	/**
	 * <p>Quote <code>anyObject</code> using <code>left</code> and
	 * <code>right</code> as the left and right quotation characters,
	 * respectively. This method is similar to {@link #q(Object)}, but allows
	 * caller to specify (independently) Characters that will be used for the
	 * left and right quotes around <code>anyObject</code>.</p>
	 * <p/>
	 * <p>Examples:
	 * <pre>
	 *     q(new Character('"'),  new Character('"'),  null)    // yields: "null"
	 *     q(new Character('"'),  new Character('"'),  foo)     // yields: "\"foo\""
	 *     q('"', '"', "foo")    // yields: "\"foo\""  // same as above, using autoboxing
	 *     q('X', 'Y', "foo")    // yields: "XfooY"
	 *     q('&gt;', '&lt;', "foo")    // yields: "&gt;foo&lt;"
	 * </pre>
	 * </p>
	 *
	 * @param left      The string to use as the left-hand "quote" for
	 *                  <code>anyObject</code> with. May <em>not</em> be
	 *                  <code>null</code>.
	 * @param right     The string to use as the right-hand "quote" for
	 *                  <code>anyObject</code> with. May <em>not</em> be
	 *                  <code>null</code>.
	 * @param anyObject The object whose value will be converted to a quoted
	 *                  string.
	 *
	 * @see #q(Object anyObject)
	 * @see #q(String quotes, Object anyObject)
	 * @see #q(String left, String right, Object anyObject)
	 * @see #q(Character quoteChar, Object anyObject)
	 */
	public static String q(Character left, Character right, Object anyObject) {
		if (null == left) {
			throw new IllegalArgumentException(
				"null provided for required 'left' param");
		}
		if (null == right) {
			throw new IllegalArgumentException(
				"null provided for required 'right' param");
		}
		return q(left.toString(), right.toString(), anyObject);
	}

	/**
	 * <p>Produces double-quoted string for <code>anyObject</code>. This is
	 * useful for any value that you want to display as a quoted value (to an end
	 * user, or in a log file, for example). If the value of
	 * <code>anyObject</code> is:
	 * <pre>
	 *   foo
	 * </pre>
	 * then the returned string will be:
	 * <pre>
	 *   "foo"
	 * </pre>
	 * As a special case, if <code>anyObject</code> is <code>null</code>, then
	 * the returned String will be:
	 * <pre>
	 *   null
	 * </pre>
	 * <em>not</em>:
	 * <pre>
	 *   "null"
	 * </pre>
	 * </p>
	 * <p/>
	 * <p>To maximize joy, use this method as a static import.</p>
	 */
	public static String q(Object anyObject) {
		return q('"', anyObject);
	}

	/**
	 * <p>Produces single-quoted string for <code>anyObject</code>. This is
	 * useful for any value that you want to display as a quoted value (to an end
	 * user, or in a log file, for example). If the value of
	 * <code>anyObject</code> is:
	 * <pre>
	 *   foo
	 * </pre>
	 * then the returned string will be:
	 * <pre>
	 *   'foo'
	 * </pre>
	 * As a special case, if <code>anyObject</code> is <code>null</code>, then
	 * the returned String will be:
	 * <pre>
	 *   null
	 * </pre>
	 * <em>not</em>:
	 * <pre>
	 *   'null'
	 * </pre>
	 * </p>
	 * <p/>
	 * <p>To maximize joy, use this method as a static import.</p>
	 */
	public static String sq(Object anyObject) {
		return q("'", anyObject);
	}

	/**
	 * Returns a new String with any patterns like "${foo}" replaced with the
	 * value of the system property "foo".  If the system property does not
	 * exist, the pattern is left as-is.
	 *
	 * @param str string to expand, may be {@code null}
	 *
	 * @deprecated use {@link PropertyUtil#substituteProperties(String)} instead
	 */
	@Deprecated
	public static String substituteProperties(String str) {
		return PropertyUtil.substituteProperties(str);
	}

	/**
	 * Returns a new String with any patterns like "${foo}" replaced with the
	 * value of the property "foo" from the given properties.  If the property
	 * does not exist, the pattern is left as-is.
	 *
	 * @param str   string to expand, may be {@code null}
	 * @param props properties to use for replacements, may not be {@code null}
	 *
	 * @deprecated use {@link PropertyUtil#substituteProperties(String, java.util.Properties)} instead
	 */
	@Deprecated
	public static String substituteProperties(String str, Properties props) {
		return PropertyUtil.substituteProperties(str, props);
	}

	/** @deprecated use {@link TextUtil#pluralize(String)} instead */
	@Deprecated
	public static String pluralize(String str) {
		return TextUtil.pluralize(str);
	}

	/** @deprecated use {@link TextUtil#pluralize(String, java.util.Locale)} instead */
	@Deprecated
	public static String pluralize(String str, Locale locale) {
		return TextUtil.pluralize(str, locale);
	}

	/**
	 * Of the set of strings, returns the first which is both non-null
	 * and non-empty.  Otherwise returns null.
	 */
	public static String firstNonEmpty(String... things) {
		if (null == things) return null;

		for (String thing : things)
			if (!StringUtil.isEmpty(thing)) return thing;

		return null;
	}

}
