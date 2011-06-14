package com.nkhoang.common.util;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Helper methods for working with properties. */
public class PropertyUtil {

	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$[{]([^}]*)[}]");

	/**
	 * Returns a new String with any patterns like "${foo}" replaced with the
	 * value of the system property "foo".  If the system property does not
	 * exist, the pattern is left as-is.
	 *
	 * @param str string to expand, may be {@code null}
	 */
	public static String substituteProperties(String str) {
		return substituteProperties(str, System.getProperties());
	}

	/**
	 * Returns a new String with any patterns like "${foo}" replaced with the
	 * value of the property "foo" from the given properties.  If the property
	 * does not exist, the pattern is left as-is.
	 *
	 * @param str   string to expand, may be {@code null}
	 * @param props properties to use for replacements, may not be {@code null}
	 */
	public static String substituteProperties(String str, Properties props) {
		// is there a real string?
		if (str == null) return null;

		// replace anything that looks like ${foo} with the relevant system
		// property, if it exists
		Matcher propMatcher = PROPERTY_PATTERN.matcher(str);
		//yeah, i know, but java.util likes it (see matcher below)
		StringBuffer sb = new StringBuffer();
		while (propMatcher.find()) {
			String propKey = propMatcher.group(1);
			String value = props.getProperty(propKey);
			if (value == null) {
				// if no value, leave as is
				value = propMatcher.group();
			}
			// make sure replacement string is not further interpreted
			value = Matcher.quoteReplacement(value);
			propMatcher.appendReplacement(sb, value);
		}
		propMatcher.appendTail(sb);
		return sb.toString();
	}

}
