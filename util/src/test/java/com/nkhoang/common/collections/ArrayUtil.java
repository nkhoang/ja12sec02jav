package com.nkhoang.common.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

/** Utilities for array manipulation. */
public class ArrayUtil {

	/**
	 * Joins the array in the first argument with the array in the
	 * second argument.
	 *
	 * @param obj1 The first array to join
	 * @param obj2 The second array to join
	 *
	 * @return The merged array
	 */
	public static Object[] join(Object[] obj1, Object[] obj2) {
		if (obj1 == null || obj2 == null) {
			throw new IllegalArgumentException("Arguments cannot be null");
		}
		return join(obj1, obj2, new Object[obj1.length + obj2.length]);
	}

	/**
	 * Joins the array in the first argument with the array in the
	 * second argument and puts the values into the array specified
	 * in the third argument.
	 *
	 * @param obj1 The first array to join
	 * @param obj2 The second array to join
	 * @param rv   The array to put the data in.
	 *
	 * @return The merged array
	 */
	public static Object[] join(Object[] obj1, Object[] obj2, Object[] rv) {
		if (obj1 == null || obj2 == null || rv == null) {
			throw new IllegalArgumentException("Arguments cannot be null");
		}

		int count = 0;
		for (int x = 0; x < obj1.length && count < rv.length; x++, count++) {
			rv[count] = obj1[x];
		}

		for (int x = 0; x < obj2.length && count < rv.length; x++, count++) {
			rv[count] = obj2[x];
		}

		return rv;
	}

	/////////////////////////////////////////////////////////////////////////////
	// Fisher-Yates shuffle algorithm ==>
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array list to randomize
	 */
	public static <T> void fisherYatesShuffle(ArrayList<T> array) {
		T tmp;
		for (int i = array.size() - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array.get(i);
			array.set(i, array.get(j));
			array.set(j, tmp);
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(Object array[]) {
		Object tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(int array[]) {
		int tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(long array[]) {
		long tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(float array[]) {
		float tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(double array[]) {
		double tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(char array[]) {
		char tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(boolean array[]) {
		boolean tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(byte array[]) {
		byte tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Randomize the elements in an array using the Fisher-Yates shuffle
	 * algorithm.
	 *
	 * @param array the array to randomize
	 */
	public static void fisherYatesShuffle(short array[]) {
		short tmp;
		for (int i = array.length - 1; i > 0; --i) {
			int j = (int) (Math.random() * i);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}


	/////////////////////////////////////////////////////////////////////////////
	// <== Fisher-Yates shuffle algoirthm
	/////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////
	// Array Reversal Code ==>
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(Object[])}
	 */
	@Deprecated
	public static void quickReverse(Object array[]) {
		Object tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(int[])}
	 */
	@Deprecated
	public static void quickReverse(int array[]) {
		int tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(long[])}
	 */
	@Deprecated
	public static void quickReverse(long array[]) {
		long tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(float[])}
	 */
	@Deprecated
	public static void quickReverse(float array[]) {
		float tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(double[])}
	 */
	@Deprecated
	public static void quickReverse(double array[]) {
		double tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(char[])}
	 */
	@Deprecated
	public static void quickReverse(char array[]) {
		char tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(boolean[])}
	 */
	@Deprecated
	public static void quickReverse(boolean array[]) {
		boolean tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(byte[])}
	 */
	@Deprecated
	public static void quickReverse(byte array[]) {
		byte tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * Quickly reverse the order of an array.
	 *
	 * @param array the array to reverse
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#reverse(short[])}
	 */
	@Deprecated
	public static void quickReverse(short array[]) {
		short tmp;
		for (int i = array.length - 1, j = 0; i > j; --i, ++j) {
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	// isEmpty code
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(Object[])}
	 */
	@Deprecated
	public static boolean isEmpty(Object[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(int[])}
	 */
	@Deprecated
	public static boolean isEmpty(int[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(long[])}
	 */
	@Deprecated
	public static boolean isEmpty(long[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(float[])}
	 */
	@Deprecated
	public static boolean isEmpty(float[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(double[])}
	 */
	@Deprecated
	public static boolean isEmpty(double[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(char[])}
	 */
	@Deprecated
	public static boolean isEmpty(char[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(boolean[])}
	 */
	@Deprecated
	public static boolean isEmpty(boolean[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(byte[])}
	 */
	@Deprecated
	public static boolean isEmpty(byte[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns <code>true</code> if the array is null or has length == 0.
	 *
	 * @param a the array to check
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 *
	 * @deprecated use {@link org.apache.commons.lang.ArrayUtils#isEmpty(short[])}
	 */
	@Deprecated
	public static boolean isEmpty(short[] a) {
		return ((a == null) || (a.length == 0));
	}

	/**
	 * Returns a new array that is <code>array</code> with all of
	 * <code>toBeRemoved</code> removed from it.  Assuming <code>array</code>
	 * contains all elements in <code>toBeRemoved</code> the new array have
	 * <code>array.length - toBeRemoved.length</code> elements.
	 *
	 * @param array       the array to check
	 * @param toBeRemoved the elements to remove from the array
	 *
	 * @return <code>true</code> if the array is null or has length == 0,
	 *         otherwise false
	 */
	public static <T, T1> T[] removeFromArray(T[] array, T1... toBeRemoved) {
		if (ArrayUtil.isEmpty(toBeRemoved)) {
			return array;
		}

		List<T> l = new ArrayList<T>(array.length - toBeRemoved.length);
		List<T1> toBeRemovedList = Arrays.asList(toBeRemoved);
		for (T item : array) {
			if (!toBeRemovedList.contains(item)) {
				l.add(item);
			}
		}

		@SuppressWarnings("unchecked") T[] rtn = (T[]) Array.newInstance(array.getClass().getComponentType(), l.size());

		return l.toArray(rtn);
	}


	/**
	 * Creates a new array, consisting of all elements from the original array,
	 * in order, with the variable arguments appended.
	 *
	 * @param array the original array
	 * @param items variable arguments to append
	 *
	 * @return a new array with all the elements of the original array and the
	 *         variable arguments
	 */
	public static <T, TI extends T> T[] add(T[] array, TI... items) {
		@SuppressWarnings("unchecked") T[] result = (T[]) Array.newInstance(
			array.getClass().getComponentType(), array.length + items.length);
		System.arraycopy(array, 0, result, 0, array.length);
		System.arraycopy(items, 0, result, array.length, items.length);
		return result;
	}

	/**
	 * In-line filtering of an array to a collection based on a given predicate.
	 *
	 * @param pred     a unary boolean function
	 * @param elements the things to be filtered
	 */
	public static <Type> Collection<Type> filter(Predicate pred, Type... elements) {
		if (null == elements) {
			return new ArrayList<Type>();
		}
		List<Type> lst = new ArrayList<Type>(Arrays.asList(elements));
		CollectionUtils.filter(lst, pred);
		return lst;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(int[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(long[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(float[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(double[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(char[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(boolean[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(byte[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(short[] array) {
		return null == array ? 0 : array.length;
	}

	/** @deprecated use {@link org.apache.commons.lang.ArrayUtils#getLength(Object)} */
	@Deprecated
	public static int length(Object[] array) {
		return null == array ? 0 : array.length;
	}

}
