package com.nkhoang.common;

public class StringUtil {
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
}
