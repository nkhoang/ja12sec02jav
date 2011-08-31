package com.nkhoang.common.collections;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;


/** Collection of utility classes for dealing with Collections */
public class CollectionUtils {

	/**
	 * Deep isEmpty method for collections.
	 * <br />
	 * *NOTE* <code>null</code> object is considered as a deep empty collection. Therefore
	 * a collection of nulls is also a deep empty collection.
	 *
	 * @param collection - a collection of things
	 *
	 * @return true if
	 *         <li><code>CollectionUtils.isEmpty(collection)</code></li>
	 *         <li><code>collection</code> is a collection of (deep) empty collections</li>
	 *         <li>false otherwise</li>
	 *
	 * @deprecated replaced with {@link #isDeepEmpty(java.util.Collection)}
	 */
	@Deprecated
	public static boolean IsDeepEmpty(Collection<?> collection) {
		return isDeepEmpty(collection);
	}

	/**
	 * Deep isEmpty method for collections.
	 * <br />
	 * *NOTE* <code>null</code> object is considered as a deep empty collection. Therefore
	 * a collection of nulls is also a deep empty collection.
	 *
	 * @param collection - a collection of things
	 *
	 * @return true if
	 *         <li><code>CollectionUtils.isEmpty(collection)</code></li>
	 *         <li><code>collection</code> is a collection of (deep) empty collections</li>
	 *         <li>false otherwise</li>
	 */
	public static boolean isDeepEmpty(Collection<?> collection) {
		if (org.apache.commons.collections.CollectionUtils.isEmpty(collection)) {
			return true;
		}
		for (Object o : collection) {
			if (o instanceof Collection) {
				Collection c = (Collection) o;
				if (!isDeepEmpty(c)) {
					return false;
				}
			} else if (o != null) {
				return false; //found a non empty object, dont care anymore
			}
		}
		return true;
	}


	/**
	 * Split a list into multiple lists of size <code>splitSize</code>.
	 * <br>
	 * The returned collection is a set of sublists of the input list
	 * (objects are not copied).
	 *
	 * @param listToSplit The list to split
	 * @param splitSize   Size of a sublist
	 *
	 * @return null if input list is null <br>
	 *         A collection of list of size <code>splitSize</code>.
	 *         <br>
	 *         If the size of <code>listToSplit</code> is not an integer multiple of
	 *         <code>splitSize</code>, then the last list in the return collection will
	 *         have a size of <code>listToSplit.size()%splitSize</code>
	 */
	public static <T> List<List<T>> splitList(List<T> listToSplit, int splitSize) {
		if (listToSplit == null) {
			return null;
		}
		if (splitSize <= 0) {
			List<List<T>> ret = new ArrayList<List<T>>();
			ret.add(listToSplit);
			return ret;
		}

		int size = listToSplit.size() / splitSize;
		List<List<T>> ret = new ArrayList<List<T>>(size + 1);
		for (int i = 0; i < size; i++) {
			ret.add(listToSplit.subList(i * splitSize, (i + 1) * splitSize));
		}
		int remain = listToSplit.size() % splitSize;
		if (remain != 0) {
			// add the remain if there's any
			ret.add(listToSplit.subList(size * splitSize, size * splitSize + remain));
		}

		return ret;
	}


	/**
	 * Use a Typesafe Map similar to a commons MultiMap.
	 * <p/>
	 * If <code>key</code> is already in the map, it will add <code>value</code> to the list of
	 * values.  If key is not in the map, it will create a new list with <code>value</code>
	 * in it and put the <code>key</code> and the new list into the map.
	 * <p/>
	 * When new lists are created, they are created as ArrayLists.
	 *
	 * @return <code>true</code> (as per the general contract of the
	 *         <code>Collection.add</code> method).
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> boolean multiPutList(Map<K, ? super List<V>> map, K key, V value) {
		Collection<V> values = (Collection<V>) map.get(key);
		if (values == null) {
			values = new ArrayList<V>();
			map.put(key, (ArrayList<V>) values);
		}
		return values.add(value);
	}

	/**
	 * Use a Typesafe Map similar to a commons MultiMap.
	 * <p/>
	 * If <code>key</code> is already in the map, it will add <code>value</code> to the list of
	 * values.  If key is not in the map, it will create a new set with <code>value</code>
	 * in it and put the <code>key</code> and the new set into the map.
	 * <p/>
	 * When new sets are created, they are created as HashSet.
	 *
	 * @return <code>true</code> if the value was added to the value set, <code>false</code> if it wasn't
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> boolean multiPutSet(Map<K, ? super Set<V>> map, K key, V value) {
		Collection<V> values = (Collection<V>) map.get(key);
		if (values == null) {
			values = new HashSet<V>();
			map.put(key, (HashSet<V>) values);
		}
		return values.add(value);
	}

	/**
	 * Puts the given value in the concurrent map if none currently exists and
	 * returns whatever value exists in the map at the end of the operation.
	 *
	 * @return the value which is in the map for the given key (either the given
	 *         value if none was previously in the map, or an old value)
	 */
	public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
		V oldValue = map.putIfAbsent(key, value);
		return ((oldValue != null) ? oldValue : value);
	}

	/**
	 * Similar to {@link java.util.Collection#addAll}, except that the src can be an
	 * Iterable instead of a Collection.  If the given src is a Collection, the
	 * relevant method on dst will be used directly. If the given src is
	 * {@code null} it will be treated as an empty collection.
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean addAll(
		Collection<T> dst, Iterable<? extends T> src) {
		if (src == null) {
			return false;
		}
		if (src instanceof Collection) {
			return dst.addAll((Collection<? extends T>) src);
		}
		boolean modified = false;
		for (T t : src) {
			modified |= dst.add(t);
		}
		return modified;
	}

	/**
	 * Similar to {@link java.util.Collection#containsAll}, except that the src can be an
	 * Iterable instead of a Collection.  If the given src is a Collection, the
	 * relevant method on dst will be used directly. If the given src is
	 * {@code null} it will be treated as an empty collection.
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean containsAll(
		Collection<T> dst, Iterable<? extends T> src) {
		if (src == null) {
			return true;
		}
		if (src instanceof Collection) {
			return dst.containsAll((Collection<? extends T>) src);
		}
		for (T t : src) {
			if (!dst.contains(t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Similar to {@link java.util.Collection#removeAll}, except that the src can be an
	 * Iterable instead of a Collection.  If the given src is a Collection, the
	 * relevant method on dst will be used directly. If the given src is
	 * {@code null} it will be treated as an empty collection.
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean removeAll(
		Collection<T> dst, Iterable<? extends T> src) {
		if (src == null) {
			return false;
		}
		if (src instanceof Collection) {
			return dst.removeAll((Collection<? extends T>) src);
		}
		boolean modified = false;
		for (T t : src) {
			modified |= dst.remove(t);
		}
		return modified;
	}

	/**
	 * Similar to {@link java.util.Collection#retainAll}, except that the src can be an
	 * Iterable instead of a Collection.  If the given src is a Collection, the
	 * relevant method on dst will be used directly. If the given src is
	 * {@code null} it will be treated as an empty collection.
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean retainAll(
		Collection<T> dst, Iterable<? extends T> src) {
		if (src == null) {
			boolean modified = !dst.isEmpty();
			dst.clear();
			return modified;
		}
		if (!(src instanceof Collection)) {
			List<T> contained = new ArrayList<T>();
			for (T t : src) {
				if (dst.contains(t)) {
					contained.add(t);
				}
			}
			// swap in new collection which only contains elements from src
			// contained in dst
			src = contained;
		}
		return dst.retainAll((Collection<? extends T>) src);
	}

	/**
	 * Null safe <code>isEmpty</code> check.  A <code>null</code> collection is empty.
	 *
	 * @deprecated use {@link org.apache.commons.collections.CollectionUtils#isEmpty(java.util.Collection)} instead
	 */
	@Deprecated
	public static boolean isEmpty(Collection<?> c) {
		return (c == null || c.isEmpty());
	}

	/**
	 * Null safe <code>isEmpty</code> check.  A <code>null</code> map is empty.
	 *
	 * @deprecated use {@link org.apache.commons.collections.MapUtils#isEmpty(java.util.Map)} instead
	 */
	@Deprecated
	public static boolean isEmpty(Map<?, ?> m) {
		return (m == null || m.isEmpty());
	}

	/** Null safe <code>clear</code>. */
	public static void clear(Collection<?> c) {
		if (c != null) {
			c.clear();
		}
	}

	/** Null safe <code>clear</code>. */
	public static void clear(Map<?, ?> m) {
		if (m != null) {
			m.clear();
		}
	}

	/** Null save <code>contains</code>. */
	public static boolean contains(Collection<?> c, Object o) {
		return ((c != null) ? c.contains(o) : false);
	}

	/**
	 * @return the given Iterable iff non-{@code null}, otherwise an empty
	 *         Iterable
	 */
	public static <T> Iterable<T> iterable(Iterable<T> iterable) {
		return ((iterable != null) ? iterable : CollectionUtils.<T>emptyIterable());
	}

	/**
	 * Null safe, bounds safe <code>get</code>.  If index is out of bounds,
	 * <code>null</code> will be returned, otherwise the value at the given
	 * index will be returned.
	 */
	public static <T> T getIgnoreBounds(List<T> c, int idx) {
		if (isEmpty(c)) {
			return null;
		}
		return (((idx >= 0) && (idx < c.size())) ? c.get(idx) : null);
	}

	/**
	 * When trying to build a <code>Map</code> keyed by a primative sometimes the decision is made
	 * to convert the primitive to a <code>String</code> rather than the proper wrapper object around
	 * the primitive.
	 * <p/>
	 * When interacting with these systems, it's often desirable to use a safer, less confusing
	 * object as the key like <code>Integer</code>, or <code>Float</code>.
	 * <p/>
	 * This method will copy any keyed <code>Map</code> to a <code>String</code> keyed <code>Map</code>.
	 * <p/>
	 * Note: the implementation of the <code>Map</code> is a <code>HashMap</code>
	 */
	public static <V> Map<String, V> copyToStringKeyedMap(Map<? extends Object, V> map) {
		Map<String, V> hashMap = new HashMap<String, V>(map.size());
		for (Map.Entry<? extends Object, V> entry : map.entrySet()) {
			hashMap.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		return hashMap;
	}

	/**
	 * Verifies that all elements in the passed typesafe <code>List</code> are
	 * members of the specified type, and returns a typesafe <code>List</code>
	 * appearing to be the new type.
	 * <p/>
	 * Yes, this method exists to prevent compiler warnings, and yes, you'll
	 * get a bunch of warnings when you compile it.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> cast(List<?> list, Class<T> klass) {
		for (Object obj : list) {
			klass.cast(obj);
		}
		return (List<T>) list;
	}


	/**
	 * Verifies that all elements in the passed typesafe <code>List</code> are
	 * members of the specified type, and returns a typesafe <code>List</code>
	 * appearing to be the new type.
	 * <p/>
	 * Yes, this method exists to prevent compiler warnings, and yes, you'll
	 * get a bunch of warnings when you compile it.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> cast(Set<?> set, Class<T> klass) {
		for (Object obj : set) {
			klass.cast(obj);
		}
		return (Set<T>) set;
	}


	/**
	 * Creates a typesafe modifiable <code>List</code>  from explicitly-
	 * specified values.
	 */
	public static <T> List<T> createList(T... elems) {
		List<T> result = new ArrayList<T>(elems.length);
		for (T elem : elems) {
			result.add(elem);
		}
		return result;
	}


	/**
	 * Creates a typesafe modifiable <code>Set</code> from explicitly-specified
	 * values.
	 */
	public static <T> Set<T> createSet(T... elems) {
		Set<T> result = new HashSet<T>(elems.length);
		for (T elem : elems) {
			result.add(elem);
		}
		return result;
	}


	/**
	 * Creates a modifiable <code>Map</code> from explicitly-specified values.
	 * This is useful when you need to pass a <code>Map</code> as a parameter,
	 * particularly as a superclass ctor parameter.
	 *
	 * @param elems Elements to add to the map. Each key must be followed by
	 *              its value. An odd number of parameters throws.
	 */
	public static Map<Object, Object> createMap(Object... elems) {
		if (elems.length % 2 == 1) {
			throw new IllegalArgumentException("must pass even number of arguments");
		}
		Map<Object, Object> result = new HashMap<Object, Object>();
		for (int ii = 0; ii < elems.length; ii += 2) {
			result.put(elems[ii], elems[ii + 1]);
		}
		return result;
	}


	/**
	 * Adds all elements from one collection to another, ensuring that the source
	 * elements meet the type safety requirements of the destination collection.
	 */
	public static <T> void addAll(Collection<?> source, Collection<T> dest, Class<T> klass) {
		for (Object obj : source) {
			dest.add(klass.cast(obj));
		}
	}


	/**
	 * Takes <i>N</i> collections and returns a <i>new</i> collection containing
	 * the intersection of values of the passed in collections
	 * <br/>
	 * <b>Note: </b> It is highly recommended, but not required, that the passed in collections
	 * are sets or other collections with fast contains() methods.
	 * <br/>
	 * The intersect tool, performs intersections in order from the smallest list to the
	 * largest list in order to get the fastest speed.
	 * <br/>
	 * The resulting collection will <b>not</b> contain duplicate entries (as defined by the
	 * internal object equals method) and will <b>not</b> maintain order
	 */
	public static <T> Collection<T> intersect(Collection<? extends T>... collections) {
		if (ArrayUtils.isEmpty(collections)) {
			//intersection of nothing is an empty set
			return Collections.emptySet();
		}
		//if size is 1 or more, we're golden
		Arrays.sort(collections, SIZE_COMPARATOR);

		//copy the first, we're going to be modifying this
		Set<T> result = new HashSet<T>(collections[0]);

		//skip the first
		for (int i = 1; i < collections.length; i++) {
			//there is some debate as to whether or not this should do an instanceof set
			//and copy to a hashset if it's not a set to get a fast retainAll
			//I can't decide.
			result.retainAll(collections[i]);
		}
		return result;
	}

	private static final SizeComparator SIZE_COMPARATOR = new SizeComparator();

	private static class SizeComparator implements Comparator<Collection<?>> {
		public int compare(Collection<?> arg0, Collection<?> arg1) {
			return arg0.size() - arg1.size();
		}
	}

	/**
	 * Adapts an Enumeration to an Iterable (for use in the jdk 1.5 "foreach"
	 * construct).  Handles <code>null</code> enumerations (returns
	 * non-<code>null</code>, empty Iterable).  The remove operation is not
	 * supported.
	 * <p/>
	 * Note, although commons-collections has an EnumerationIterator, it does
	 * not use generics, nor is it Iterable, hence this implementation.
	 *
	 * @return a valid Iterable for the given Enumeration
	 */
	public static <T> Iterable<T> toIterable(
		final Enumeration<T> enumeration) {
		return ((enumeration != null) ? (new IterableIterator<T>() {
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			public T next() {
				return enumeration.nextElement();
			}
		}) : Collections.<T>emptyList());
	}

	/**
	 * Handy class for adapting other classes to Iterable/Iterator combinations.
	 * The iterator method returns <code>this</code> and the remove operation
	 * throws <code>UnsupportedOperationException</code> by default.
	 * <p/>
	 * TODO: Move to hmsCommon-util when we open-source it
	 */
	public static abstract class IterableIterator<T> implements Iterable<T>, Iterator<T> {
		public Iterator<T> iterator() {
			return this;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}


	/** Concatenates multiple type-safe <code>List</code>s, producing a new list. */
	public static <T> List<T> concat(List<? extends T>... sources) {
		int presize = 0;
		if (sources != null) {
			for (List<? extends T> source : sources) {
				presize += source.size();
			}
		}
		List<T> result = new ArrayList<T>(presize);
		if (sources != null) {
			for (List<? extends T> source : sources) {
				result.addAll(source);
			}
		}
		return result;
	}


	/**
	 * Returns the passed object if it's not <code>null</code>, an empty
	 * collection if it is. This avoids a null check, and is particularly
	 * useful with collections of collections.
	 */
	public static <T> Collection<T> emptyIfNull(Collection<T> src) {
		return (src != null) ? src : Collections.<T>emptyList();
	}

	/**
	 * Sorts the given list in place using {@link java.util.Collections#sort(java.util.List)} and returns
	 * it.  (allows using the sort inline with the creation).
	 */
	public static <T extends Comparable<? super T>> List<T> sort(List<T> l) {
		Collections.sort(l);
		return l;
	}

	/** Copies the given Collection into an ArrayList, sorts it, and returns it. */
	public static <T extends Comparable<? super T>> ArrayList<T> sortedList(
		Collection<T> c) {
		return (ArrayList<T>) sort(new ArrayList<T>(c));
	}

	/**
	 * Returns the "first" element (as defined by Iterator order) of the given
	 * Iterable if there is exactly one element and it is non-<code>null</code>.
	 * Otherwise, throws an exception (> 1 elements, 0 elements, or null
	 * element).
	 *
	 * @throws IllegalStateException if the given Iterable has more than 1
	 *                               element
	 */
	public static <T> T uniqueNonNullValue(Iterable<T> c) {
		T value = uniqueValue(c);
		if (value == null) {
			throw new IllegalStateException("Expected single non-null value");
		}
		return value;
	}

	/**
	 * Returns the "first" element (as defined by Iterator order) of the given
	 * Iterable if there is one element and <code>null</code> if there are no
	 * elements.  Otherwise, throws an exception (> 1 elements).
	 *
	 * @throws IllegalStateException if the given Iterable has more than 1
	 *                               element
	 */
	public static <T> T uniqueValue(Iterable<T> c) {
		// by using the Iterator here, we should get close to constant time
		// performance for most collections.  Some collections (e.g. HashSet) are
		// not constant time even for iteration, but, what can you do?
		T value = null;
		if (c != null) {
			Iterator<T> iter = c.iterator();
			if (iter.hasNext()) {
				value = iter.next();
			}
			if (iter.hasNext()) {
				throw new IllegalStateException("Collection size expected to be <= 1");
			}
		}
		return value;
	}

	/**
	 * Returns the "first" element (as defined by Iterator order) of the given
	 * Iterable if there is at least one element and <code>null</code> if there
	 * are no elements.
	 */
	public static <T> T firstValue(Iterable<T> c) {
		// by using the Iterator here, we should get close to constant time
		// performance for most collections.  Some collections (e.g. HashSet) are
		// not constant time even for iteration, but, what can you do?
		T value = null;
		if (c != null) {
			Iterator<T> iter = c.iterator();
			if (iter.hasNext()) {
				value = iter.next();
			}
		}
		return value;
	}


	/**
	 * @return the iterator for the given Iterable if it is not null, otherwise
	 *         an empty iterator.
	 */
	public static <T> Iterator<T> iterator(Iterable<T> c) {
		return iterable(c).iterator();
	}


	/**
	 * Takes an existing <code>String</code>-keyed <code>Map</code>, and creates
	 * a new <code>Map</code> where all the strings have been lowercased. Useful
	 * when you have no guarantees how the map was filled. Of course, if there
	 * are multiple keys that reduce to the same lowercase string, you're going
	 * to lose data.
	 * <p/>
	 * Since this method has to iterate the map, it's only efficient if you're
	 * retrieving multiple values from the map.
	 */
	public static <T> Map<String, T> createLowercasedKeyMap(Map<String, T> src) {
		Map<String, T> dst = new HashMap<String, T>();
		for (Map.Entry<String, T> entry : src.entrySet()) {
			dst.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		return dst;
	}

	/**
	 * @return an Iterable with no elements.
	 *
	 * @deprecated use {@link java.util.Collections#emptySet()}
	 */
	@Deprecated
	public static <T> Iterable<T> emptyIterable() {
		return Collections.emptySet();
	}

	/** @return an Iterator with no elements. */
	public static <T> Iterator<T> emptyIterator() {
		return Collections.<T>emptySet().iterator();
	}

	private static final WeakReference<?> EMPTY_WEAK_REF = new WeakReference<Object>(null);
	private static final SoftReference<?> EMPTY_SOFT_REF = new SoftReference<Object>(null);

	/**
	 * @return a WeakReference with no referent (<code>get</code> will return
	 *         <code>null</code>).  Useful for initial WeakReference values.
	 */
	@SuppressWarnings("unchecked")
	public static <T> WeakReference<T> emptyWeakReference() {
		return (WeakReference<T>) EMPTY_WEAK_REF;
	}

	/**
	 * @return a SoftReference with no referent (<code>get</code> will return
	 *         <code>null</code>).  Useful for initial SoftReference values.
	 */
	@SuppressWarnings("unchecked")
	public static <T> SoftReference<T> emptySoftReference() {
		return (SoftReference<T>) EMPTY_SOFT_REF;
	}

	/** @return an empty SortedSet (immutable).  This SortedSet is serializable. */
	@SuppressWarnings("unchecked")
	public static <T> SortedSet<T> emptySortedSet() {
		return (SortedSet<T>) EMPTY_SORTED_SET;
	}

	/** @return an empty MultiMap (immutable). */
	@SuppressWarnings("unchecked")
	public static <K, V> MultiMap<K, V> emptyMultiMap() {
		return (MultiMap<K, V>) EMPTY_MULTI_MAP;
	}

	/**
	 * Adds <code>item</code> to <code>collection</code> iff <code>item</code>
	 * is not <code>null</code>.
	 */
	public static <T> void addItemIfNotNull(Collection<T> collection, T item) {
		if (item != null) {
			collection.add(item);
		}
	}

	/** singleton returned by <code>emptySortedSet</code> */
	@SuppressWarnings("unchecked")
	private static final SortedSet EMPTY_SORTED_SET = new EmptySortedSet();

	/** singleton returned by <code>emptyMultiMap</code> */
	@SuppressWarnings("unchecked")
	private static final MultiMap EMPTY_MULTI_MAP = new EmptyMultiMap();

	/** Implementation of an empty, immutable SortedSet. */
	private static class EmptySortedSet extends AbstractSet<Object> implements SortedSet<Object>, Serializable {
		private static final long serialVersionUID = 0L;

		@Override
		public Iterator<Object> iterator() {
			return new Iterator<Object>() {
				public boolean hasNext() {
					return false;
				}

				public Object next() {
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean contains(Object obj) {
			return false;
		}

		public Comparator<? super Object> comparator() {
			return null;
		}

		public Object first() {
			throw new NoSuchElementException();
		}

		public Object last() {
			throw new NoSuchElementException();
		}

		public SortedSet<Object> headSet(Object toElement) {
			return this;
		}

		public SortedSet<Object> tailSet(Object fromElement) {
			return this;
		}

		public SortedSet<Object> subSet(Object fromElement, Object toElement) {
			return this;
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_SORTED_SET;
		}

	}

	private static class EmptyMultiMap extends MultiMap<Object, Object> {

		private static final long serialVersionUID = 20080312L;

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public boolean contains(Object key, Object value) {
			return false;
		}

		@Override
		public Collection<Object> get(Object key) {
			return Collections.<Object>emptyList();
		}

		@Override
		public void put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<Object, Object> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(MultiMap<Object, Object> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<Object> remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Object> keySet() {
			return Collections.<Object>emptySet();
		}

		@Override
		public Collection<Object> values() {
			return Collections.<Object>emptySet();
		}

		@Override
		public Set<Map.Entry<Object, Collection<Object>>> entrySet() {
			return Collections.<Map.Entry<Object, Collection<Object>>>emptySet();
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			return (o instanceof MultiMap) && ((MultiMap) o).size() == 0;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_MULTI_MAP;
		}

	}

}
