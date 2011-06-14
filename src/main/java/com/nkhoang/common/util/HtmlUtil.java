package com.nkhoang.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Helper methods for working with HTML strings. */
public class HtmlUtil {
	/**
	 * Naieve html stripping.  Doesn't support
	 * comments surrounding other HTML tags.
	 *
	 * @param data string to remove
	 */
	public static String stripSimpleHtml(String data) {
		data = data.replaceAll("\u00a0", " ");
		data = htmlDecode(data);

		if (null == data || 0 == data.length()) {
			return data;
		}
		data = data.replaceAll("<[Bb][Rr][^>]*>", "\n");
		data = data.replaceAll("<[^>]+>", "");
		return data;
	}

	/**
	 * Decode HTML ligatures (like &amp;).
	 * See also: <a href="http://www.w3.org/TR/html401/sgml/entities.html">W3.org's list of HTML Entities</a>.
	 * <p/>
	 * TODO: Does this belong somewhere other than in StringUtil?
	 *
	 * @param data - the string to decode.
	 *
	 * @return the decoded string
	 */
	public static String htmlDecode(String data) {
		String[] patterns = {"&shy;", "-", "&curren;", "$", "&nbsp;", " ", "&amp;", "&", "&gt;", ">", "&lt;", "<", "&quot;", "'",};
		String result = data;

		if (-1 == result.indexOf("&")) {
			return result;
		}

		for (int i = 0; i < patterns.length; i += 2) {
			Pattern pat = Pattern.compile(patterns[i]);
			result = pat.matcher(result).replaceAll(patterns[i + 1]);
		}

		String ligatureRegex = "&#(\\d{2,3});";
		Pattern pat = Pattern.compile(ligatureRegex);
		Matcher m = pat.matcher(result);
		while (m.find()) {
			String ch = _decodeChar(m.group(1));
			result = result.replaceAll(ligatureRegex, ch);
			m = pat.matcher(result);
		}

		return result;
	}

	private static String _decodeChar(String ch) {
		return "" + (char) Integer.parseInt(ch);
	}

	/**
	 * Simplisitic string cleaning.  If the string is null, then an
	 * empty string will be returned.  Otherwise, stripSimpleHtml,
	 * condenseWhiteSpace, and then trim() are all used to clean the string.
	 * <p/>
	 * TODO: Does this belong somewhere other than in StringUtil?
	 *
	 * @param data the string to clean
	 *
	 * @return the clenaed data.
	 */
	public static String simpleCleanString(String data) {
		return null == data ? "" : StringUtil.condenseWhiteSpace(
			stripSimpleHtml(data)).trim();
	}

	/**
	 * Simplisitic string cleaning.  Calls simpleCleanString on each string
	 * in the array.
	 *
	 * @param data the strings to clean
	 *
	 * @return the cleaned data.
	 */
	public static String[] simpleCleanStrings(String data[]) {
		for (int i = 0; i < data.length; ++i) {
			data[i] = simpleCleanString(data[i]);
		}
		return data;
	}

	/**
	 * Clean a two dimensional array of string data.
	 *
	 * @param data the string array to clean
	 *
	 * @return the string array that was cleaned.
	 */
	public static String[][] simpleCleanStrings(String data[][]) {
		for (int i = 0; i < data.length; ++i) {
			for (int j = 0; j < data[i].length; ++j) {
				data[i][j] = simpleCleanString(data[i][j]);
			}
		}
		return data;
	}

	/**
	 * @return s with URLs surrounded by a href elements, suitable for display
	 *         in a web page
	 */
	public static String linkURLs(String s) {
		return s.replaceAll("(https?\\://[^\\s]+)", "<a href=\"$1\">$1</a>");
	}

}
