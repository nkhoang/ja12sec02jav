package com.nkhoang.common.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Utilities for manipulating words or text in ways which tend to be more
 * language specific.
 */
public class TextUtil {

	private static final int DEFAULT_MAX_TRUNCATE_LENGTH = 30;

	public static String infixCap(String s) {
		StringBuffer rtn = new StringBuffer();
		boolean space = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != ' ') {
				rtn.append(space ? Character.toUpperCase(c) : c);
				space = false;
			} else {
				space = true;
			}
		}
		return rtn.toString();
	}

	/**
	 * @deprecated replace with
	 *             {@link org.apache.commons.lang.StringUtils#capitalize(String)}
	 */
	@Deprecated
	public static String capitalize(String s) {
		return StringUtils.capitalize(s);
	}

	/**
	 * If string is longer than {@value #DEFAULT_MAX_TRUNCATE_LENGTH}, truncates
	 * string to maxLength and appends "...".
	 */
	public static String truncate(String s) {
		return truncate(s, DEFAULT_MAX_TRUNCATE_LENGTH);
	}

	/**
	 * If string is longer than maxLength, truncates string to maxLength and
	 * appends "...".
	 */
	public static String truncate(String s, int maxLength) {
		if (s == null) return s;

		if (s.length() > maxLength) {
			s = s.substring(0, maxLength) + "...";
		}
		return s;
	}

	/**
	 * Pluralizes a string and includes the given count.  For example,<br />
	 * {@code pluralize(1, "address") => "1 address"}<br />
	 * {@code pluralize(2, "address") => "2 addresses"}
	 */
	public static String pluralize(int count, String singular) {
		return pluralize(count, singular, null);
	}

	public static String pluralize(int count, String singular, String plural) {
		return count + " " + (count == 1 ? singular : (plural != null ? plural : pluralize(singular)));
	}

	/**
	 * Pluralizes a string and includes the given count.  For example,<br />
	 * {@code pluralize(1, "address") => "1 address"}<br />
	 * {@code pluralize(2, "address") => "2 addresses"}
	 */
	public static String pluralize(long count, String singular) {
		return pluralize(count, singular, null);
	}

	public static String pluralize(long count, String singular, String plural) {
		return count + " " + (count == 1L ? singular : (plural != null ? plural : pluralize(singular)));
	}

	/**
	 * Pluralizes a lowercase word, assuming the given string is a singular noun,
	 * using the default locale.
	 * <p/>
	 * See {@link #pluralize(String, java.util.Locale)}
	 */
	public static String pluralize(String str) {
		return pluralize(str, Locale.getDefault());
	}

	/**
	 * Pluralizes a capitalized or lowercased word, assuming the given string is
	 * a singular noun.  If it is already plural, then you will get incorrect
	 * output.
	 * <p/>
	 * If you have complaints or suggestions with regards to this wonderful
	 * code, then your reading of this javadoc means you probably have
	 * sufficient access to check in updates and fixes :)
	 *
	 * @param str    Singular noun
	 * @param locale locale whose rules should be used for pluralizing
	 *
	 * @return the pluralized string
	 */
	public static String pluralize(String str, Locale locale) {
		String language = locale.getLanguage();

		if (language.equals(Locale.ENGLISH.getLanguage())) {
			// Copied rules from
			// http://www.diveintopython.org/dynamic_functions/stage5.html
			if (str.equalsIgnoreCase("moose") || str.equalsIgnoreCase("sheep") || str.equalsIgnoreCase("fish")) {
				// The moose is loose!
				return str;
			}
			if (str.matches(".*ix$")) {
				return str.replaceAll("x$", "ces");
			} else if (str.matches(".*[sxz]$")) {
				return str + "es";
			} else if (str.matches(".*[^aeioudgkprt]h$")) {
				return str + "es";
			} else if (str.matches(".*[^aeiou]y$")) {
				return str.replaceAll("y$", "ies");
			} else {
				return str + "s";
			}
		}
		throw new UnsupportedOperationException("No support for locale " + locale);
	}

	/**
	 * Camel case a string that is separated by either _ or -.  Just a quick
	 * function that camel cases a string with two more common delimiters
	 *
	 * @return If str contains _ or - then a camel cased version of it.
	 *         Otherwise the string, lowercased and capitalized.
	 */
	public static String camelCase(String str) {
		if (str.contains("_")) {
			return camelCase(str, "_");
		} else if (str.contains("-")) {
			return camelCase(str, "-");
		} else {
			return StringUtils.capitalize(str.toLowerCase());
		}
	}

	/** Camel case a string separated by the given delimiter */
	public static String camelCase(String str, String delimiter) {
		StringBuilder split = new StringBuilder();
		for (String splitValue : str.split(delimiter)) {
			split.append(StringUtils.capitalize(splitValue.toLowerCase()));
		}
		return split.toString();
	}

}
