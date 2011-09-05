package com.nkhoang.gae.utils;

import com.nkhoang.gae.utils.math.Constants;
import com.nkhoang.gae.utils.math.Fmt;
import com.nkhoang.gae.utils.math.Gridtable;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public final class MathUtils {
	/**
	 * Calculate the log (base 2) of a specified long. This can be
	 * useful for calculating a bit index from a bit mask.
	 *
	 * @param x a long to calculate the log of
	 *
	 * @return floor(lg(<b>x</b>)), that is, the greatest integer
	 *         less than or equal to log base 2 of <b>x</b>.
	 */
	public static int log2(long x) {
		Assert.assertTrue("long x must be greater than 0", x > 0);
		int lg = -1;
		for (int i = 1; i <= x; i += i) {
			lg++;
		}
		return lg;
	}

	/**
	 * Calculate 2 raised to a specified power.
	 *
	 * @param x the power to raise 2 to, must be greater or equal to 0
	 *
	 * @return 2 raised to the power of <b>x</b>.
	 */
	public static long power2(int x) {
		Assert.assertTrue(
			"power2 currently requires integers greater or equal to 0", x >= 0);
		long p = 1;
		for (int i = 1; i <= x; i++)
			p += p;
		return p;
	}

	/**
	 * Returns the specified number with its least significant bit
	 * zeroed.
	 *
	 * @param n an integer to make even
	 *
	 * @return the specified number with its least significant bit
	 *         zeroed
	 */
	public static int forceEven(int n) {
		return (n & 0xfffffffe);
	}

	/**
	 * Calculate 10 raised to a specified power.
	 *
	 * @param power the power to raise 10 to
	 *
	 * @return 10 raised to the power of <b>power</b>.
	 */
	public static double powerOfTen(int power) {
		Assert.assertTrue(
			"power2 currently requires integers greater or equal to 0", power >= 0);
		double value = 10.0;
		if (power == 0) {
			return 1.0;
		}
		for (int i = 1; i < power; i++) {
			value *= 10.0;
		}
		return value;
	}

	/**
	 * Returns the integer result of (d mod 10).  If round is true, then
	 * result will be rounded, otherwise, the resulted will be the greatest
	 * integer at or below the result.
	 *
	 * @param d     a double to mod against 10
	 * @param round if <b>true</b> the double will be rounded to the
	 *              nearest long, if <b>false</b> floor() will be called.
	 *
	 * @return an integer between 0 and 9 of the mod result
	 */
	public static int modulo10(double d, boolean round) {
		long l = round ? Math.round(d) : (long) Math.floor(d);
		int i = (int) (l % 10);
		return i;
	}

	/**
	 * Returns the specified number with its least significant bit
	 * set.
	 *
	 * @param n an integer to make odd
	 *
	 * @return the specified number with its least significant bit
	 *         set
	 */
	public static int forceOdd(int n) {
		return (n | 0x1);
	}


	/**
	 * Calculate the greatest common denominator for two longs.
	 *
	 * @param a the first number in long
	 * @param b the sencond number in long
	 *
	 * @return long value that is the greatest common denominator of the two.
	 *         0 if either of the two numbers is 0 or negative.
	 */
	public static long getGCD(long a, long b) {
		long small = Math.min(a, b);
		long large = Math.max(a, b);
		long temp;

		if (small <= 0) {
			return 1;
		}

		temp = large % small;
		while (temp > 0) {
			large = small;
			small = temp;
			temp = large % small;
		}
		return small;
	}


	/**
	 * Calculate the least common multiple for two longs.
	 *
	 * @param a the first number in long
	 * @param b the sencond number in long
	 *
	 * @return long value that is the least common multiple of the two.
	 *         0 if either of the two numbers is 0 or negative.
	 */
	public static long getLCM(long a, long b) {
		long lcd = getGCD(a, b);
		return (a * b / lcd);
	}


	/**
	 * Adjust the scale of the given BigDecimal to the specified scale.
	 *
	 * @param value the BigDecimal
	 * @param scale the scale, which can't be negative.
	 */
	public static BigDecimal setScale(BigDecimal value, int scale) {
		Assert.assertTrue(
			"Got a negative scale " + Constants.getInteger(scale), scale >= 0);

		try {
			value = value.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		catch (ArithmeticException e) {

		}
		catch (IllegalArgumentException e) {

		}
		return value;
	}

	public static Double setScale(Double value, int scale) {
		Assert.assertTrue(
			"Got a negative scale " + String.valueOf(scale), scale >= 0);

		BigDecimal temp = new BigDecimal(value.doubleValue());
		temp = setScale(temp, scale);

		return new Double(temp.doubleValue());
	}

	/*-- Base 36 Conversion -------------------------------------------------*/

	/**
	 * Converts the given long into a base 36 string.
	 *
	 * @param num the number to convert into base 36
	 *
	 * @return a String representation of <b>num</b> in base 36
	 */
	public static String toBase36(long num) {
		Assert.assertTrue(
			"the long passed MathUtil.toBase36 must be positive.", num >= 0);
		char[] buffer = new char[32];
		int offset = toBase36(num, buffer, 31);
		return new String(buffer, offset + 1, 31 - offset);
	}

	/**
	 * Converts the given long into a base 36 string. The result is
	 * stored in the character array <B>chr</B>, starting with the
	 * character at <B>off</B>, counted from the end of the array.
	 *
	 * @param num the number to convert into base 36
	 * @param chr the character array to insert the base 36
	 *            representation of the number into
	 * @param off the offset into the character array to start writing
	 *
	 * @return the index of the character preceding the last
	 *         character written into the buffer.
	 */
	public static int toBase36(long num, char[] chr, int off) {
		if (num == 0) {
			chr[off] = '0';
			off--;
		} else {
			for (; num > 0; off--) {
				chr[off] = convertTable[(int) (num % 36)];
				num = num / 36;
			}
		}
		return off;
	}

	private static final char convertTable[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * Parse starting with the offset-th character, until reaching an
	 * invalid character.
	 */
	private static final int AlphaDigitOffset = 'a' - 10;
	private static final int Base             = 36;

	/**
	 * Convert a string representation of a base36 number into a
	 * long.
	 *
	 * @param s a String representation of a base 36 number
	 *
	 * @return long the number as a long
	 *
	 * @throws ParseException if the String <b>s</b> could not be
	 *                        parsed
	 */
	public static long fromBase36(String s) throws ParseException {
		for (int i = 0, l = s.length(); i < l; i++) {
			char c = s.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
				throw new ParseException(
					Fmt.S(
						"%s is not a valid Base 36 digit in %s", new Character(c), s), i);
			}
		}
		return fromBase36(s, 0);
	}

	/**
	 * Convert a string representation of a base36 number into a
	 * long. Parsing stops at the first character that can not be
	 * part of a base 36 number.
	 *
	 * @param s      a String containing a representation of a base 36
	 *               number
	 * @param offset the offset of the first character in the string
	 *               to start begin parsing at.
	 *
	 * @return long the number as a long
	 */
	public static long fromBase36(String s, int offset) {
		int len = s.length();
		long sum = 0;
		for (int i = offset; i < len; i++) {
			char c = s.charAt(i);
			int digit;
			if (c >= '0' && c <= '9') {
				digit = c - '0';
			} else if (c >= 'a' && c <= 'z') {
				digit = c - AlphaDigitOffset;
			} else {
				break;
			}
			sum = (sum * Base) + digit;
		}

		return sum;
	}

	// 2-D hashtable cache of permutation sets
	private static final Gridtable PermsCache = new Gridtable();

	/**
	 * Returns a List of Lists, where each sub-List is a list of
	 * Integers representing one permutation of <b>r</b> numbers between 0
	 * and <b>n</b>.
	 */
	public static List permutations(int n, int r) {
		// check cache first
		List perms = (List) PermsCache.get(
			Constants.getInteger(n), Constants.getInteger(r));
		if (perms == null) {
			Object[] alphabet = new Object[n + 1];
			for (int i = 0; i < n; i++) {
				alphabet[i] = Constants.getInteger(i);
			}
			perms = permutations(n, r, alphabet, 0, r);
			PermsCache.put(Constants.getInteger(n), Constants.getInteger(r), perms);
		}

		return perms;
	}

	/*
			Implements a P(n,r) permutations algorithm.  The alphabet <b>a</b> can
			be an array of arbitrary objects, e.g. Integers, Strings, Characters,
			etc.
		*/
	private static List permutations(int n, int r, Object[] a, int s, int max) {
		Assert.assertTrue("MathUtil.permutations: r must be <= n", (r <= n));

		List perms = new ArrayList();

		if (r == 1) {
			for (int i = 0; i < n; i++) {
				Object[] perm = new Object[max];
				perm[0] = a[i + s];
				perms.add(perm);
			}
		}

		// iterate over all n items in the alphabet
		// append each to the permutations of the rest
		else {

			for (int i = 0; i < n; i++) {

				// take the first element as our prefix
				Object prefix = a[s];

				// calculate all the perms of the rest of the alphabet
				List subs = permutations(n - 1, r - 1, a, s + 1, max);

				// for each sub-perm, tack on the suffix element
				for (int j = 0; j < subs.size(); j++) {
					Object[] sub = (Object[]) subs.get(j);
					sub[r - 1] = prefix;
				}

				// save this set of permutations
				perms.add(subs);

				// swap the first element with the next prefix
				a[s] = a[s + i + 1];
				a[s + i + 1] = prefix;
			}

			// alphabet is shifted right one now, shift it back
			System.arraycopy(a, s + 1, a, s, n);
			a[a.length - 1] = null;
		}

		return perms;
	}

	@Test
	public void testToBase36() {
		 System.out.println(MathUtils.toBase36(9999999999L));
	}

	/** prevent people from creating instances of this class */
	public MathUtils() {
	}
}