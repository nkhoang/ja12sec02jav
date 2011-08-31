package com.nkhoang.common.collections;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A typesafe multimap. Multimaps store a collection of values associated with
 * a single key; <code>put()</code> adds an item to the collection, while
 * <code>get()</code> returns the entire collection.
 * <p/>
 * We support this behavior by delegating to a <code>java.util.Map</code>,
 * provided by the caller at construction. Conditions imposed by the delegate
 * (such as <code>TreeMap</code>'s ordered iterator) are exposed by this
 * object.
 * <p/>
 * The default handling of <code>put()</code> is to store the values in a
 * <code>Set</code>. However, you can override this by defining a {@link
 * com.nkhoang.common.collections.MultiMap.CollectionStrategy} at construction.
 * <p/>
 * Note that <code>MultiMap</code> is <em>not</em> a <code>Map</code>. In
 * particular, <code>MultiMap</code> breaks the contract that <code>put()
 * </code> will return the same value added by <code>get()</code>. Jakarta's
 * <code>MultiMap</code> got around that restriction by not being typesafe.
 */
public class MultiMap<K, V> implements Serializable {

	private static final long serialVersionUID = 20080312L;

	/**
	 * This interface defines the strategy that the <code>MultiMap</code> uses
	 * when accessing the mapped collection.
	 */
	public interface CollectionStrategy<T> extends Serializable {
		/** Creates a new collection instance. */
		public Collection<T> create();

		/**
		 * Removes an item from the collection, returning <code>true</code> to
		 * indicate that the item was found in the collection, <code>false</code>
		 * to indicate that it wasn't (and the call was a no-op).
		 */
		public boolean remove(Collection<T> c, T value);
	}


	/**
	 * Helper method that creates a <code>CollectionStrategy</code> that
	 * produces <code>HashSet</code>s. Due to the limitations of Java, this
	 * can't be exposed as a static field.
	 */
	public static <T> CollectionStrategy<T> createSetStrategy() {
		return new CollectionStrategy<T>() {
			private static final long serialVersionUID = 20080312L;

			public Collection<T> create() {
				return new HashSet<T>();
			}

			public boolean remove(Collection<T> c, T value) {
				return c.remove(value);
			}
		};
	}


	/**
	 * Helper method that creates a <code>CollectionStrategy</code> that
	 * produces <code>ArrayList</code>s. Due to the limitations of Java,
	 * this can't be exposed as a static field.
	 * <p/>
	 * In keeping with the spirit of a <code>Map</code>, the <code>remove()
	 * </code> method removes <em>all<em> instances of the value from the list.
	 */
	public static <T> CollectionStrategy<T> createListStrategy() {
		return new CollectionStrategy<T>() {
			private static final long serialVersionUID = 20080312L;

			public Collection<T> create() {
				return new ArrayList<T>();
			}

			public boolean remove(Collection<T> c, T value) {
				boolean ret = false;
				while (c.remove(value)) {
					ret = true;
				}
				return ret;
			}
		};
	}


	/**
	 * Helper method for people who think of MultiMap as just manipulating a
	 * collection, not as managing mappings.
	 */
	public static <T> CollectionStrategy<T> createJAListStrategy() {
		return new CollectionStrategy<T>() {
			private static final long serialVersionUID = 20080312L;

			public Collection<T> create() {
				return new ArrayList<T>();
			}

			public boolean remove(Collection<T> c, T value) {
				return c.remove(value);
			}
		};
	}


	//-----------------------------------------------------------------------------
	//  Instance variables and constructors
	//-----------------------------------------------------------------------------

	private Map<K, Collection<V>> _delegate;
	private CollectionStrategy<V> _strategy;

	/**
	 * Base constructor.
	 *
	 * @param delegate Manages the key-collection mappings underlying this
	 *                 multi-map.
	 * @param strategy Used to create new collection instances.
	 */
	public MultiMap(
		Map<K, Collection<V>> delegate, CollectionStrategy<V> strategy) {
		_delegate = delegate;
		_strategy = strategy;
	}


	/**
	 * Default constructor, which uses <code>HashMap</code> as the underlying
	 * map implementation, and <code>HashSet</code> as the collection type.
	 */
	public MultiMap() {
		this(
			new HashMap<K, Collection<V>>(), MultiMap.<V>createSetStrategy());

	}

	/**
	 * Returns an unmodifiable Map that can be used to pass the values in the
	 * MultiMap to interfaces that take a Map interface
	 */
	public Map<K, Collection<V>> getDelegate() {
		return Collections.unmodifiableMap(_delegate);
	}


	//-----------------------------------------------------------------------------
	//  Maplike methods
	//-----------------------------------------------------------------------------

	/** Clears the delegate map. */
	public void clear() {
		_delegate.clear();
	}


	/**
	 * Returns the number of entries in the delegate map. Does not consider the
	 * number of components in the mapped collections.
	 */
	public int size() {
		return _delegate.size();
	}


	/** Determines whether the delegate map is empty. */
	public boolean isEmpty() {
		return _delegate.isEmpty();
	}


	/**
	 * Determines whether the delegate contains a mapping for the specified
	 * key.
	 */
	public boolean containsKey(Object key) {
		return _delegate.containsKey(key);
	}


	/**
	 * Determines whether the delegate contains the specified value, in any
	 * collection.
	 */
	public boolean containsValue(V value) {
		for (Collection<V> values : _delegate.values()) {
			if (values.contains(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether the delegate contains a given mapping between
	 * key and value
	 */
	public boolean contains(Object key, V value) {
		Collection<V> coll = _delegate.get(key);
		if (coll == null) {
			return false;
		} else {
			return coll.contains(value);
		}
	}

	/**
	 * Returns an unmodifiable <code>Collection</code> containing the values
	 * for the specified key, <code>null</code> if there are no entries for
	 * the key.
	 */
	public Collection<V> get(Object key) {
		Collection<V> internal = _delegate.get(key);
		if (internal == null) {
			return null;
		} else {
			return Collections.unmodifiableCollection(internal);
		}
	}


	/**
	 * Adds a value to the map with the specified key. Unlike a normal <code>
	 * Map</code>, does not return the "previous" value for that key; values
	 * are simply added to the collection.
	 */
	public void put(K key, V value) {
		Collection<V> coll = _delegate.get(key);
		if (coll == null) {
			coll = _strategy.create();
			_delegate.put(key, coll);
		}
		coll.add(value);
	}


	/** Adds all entries in the passed map to this map. */
	public void putAll(Map<K, V> m) {
		for (Map.Entry<K, V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}


	/** Adds all entries in the passed multi-map to this map. */
	public void putAll(MultiMap<K, V> m) {
		for (Map.Entry<K, Collection<V>> entry : m.entrySet()) {
			for (V value : entry.getValue()) {
				put(entry.getKey(), value);
			}
		}
	}


	/**
	 * Removes <em>all</em> values for the specified key. Returns the collection
	 * formerly associated with the key.
	 */
	public Collection<V> remove(Object key) {
		return _delegate.remove(key);
	}


	/**
	 * Removes the specified mapping between key and value. The strategy will
	 * determine what this actually does; the default strategies remove all
	 * instances of the value.
	 * <p/>
	 * If the mapped-to collection becomes empty as a result of this operation,
	 * it is removed (ie, removing the last item in the collection is equivalent
	 * to calling <code>remove(key)</code>.
	 * <p/>
	 * Unlike <code>Map.remove()</code>, this method does <em>not</em> return
	 * the "previous" value for the mapping. Instead, it returns a boolean
	 * indicating whether an object was actually removed.
	 */
	public boolean remove(Object key, V value) {
		Collection<V> coll = _delegate.get(key);
		if (coll == null) {
			return false;
			//false seems like a better return value than NPE
		}
		boolean result = _strategy.remove(coll, value);
		if (coll.size() == 0) {
			_delegate.remove(key);
		}
		return result;
	}


	/** Returns the keys to the delegate map. */
	public Set<K> keySet() {
		return _delegate.keySet();
	}


	/**
	 * Returns a <em>single collection</em> that aggregates all values held
	 * within this multi-map.
	 * <p/>
	 * On the surface, this differs from the behavior of a normal map, where
	 * <code>values()</code> returns an collection of the values from <code>
	 * entrySet()</code>. However, in use I see this an alternative to <code>
	 * containsValue()</code>, so this behavior is more appropriate.
	 */
	public Collection<V> values() {
		Collection<V> result = _strategy.create();
		for (Collection<V> entry : _delegate.values()) {
			result.addAll(entry);
		}
		return result;
	}


	/**
	 * Returns the entries from this map. The entry value is the collection of
	 * values for the entry key.
	 */
	public Set<Map.Entry<K, Collection<V>>> entrySet() {
		return _delegate.entrySet();
	}

	/** Returns a copy of this MultiMap backed by an unmodifiable Map */
	@SuppressWarnings("unchecked")
	public MultiMap<K, V> toUnmodifiableMultiMap() {
		Map<K, Collection<V>> delegate = null;
		try {
			Method clone = _delegate.getClass().getDeclaredMethod("clone");
			delegate = (Map<K, Collection<V>>) clone.invoke(_delegate);
		}
		catch (Exception ignored) {
			// fall through strategy is our default Collection
			delegate = new HashMap<K, Collection<V>>(_delegate);
		}
		for (Map.Entry<K, Collection<V>> entry : delegate.entrySet()) {
			entry.setValue(Collections.unmodifiableCollection(entry.getValue()));
		}
		return new MultiMap<K, V>(Collections.unmodifiableMap(delegate), _strategy);
	}

	//-----------------------------------------------------------------------------
	//  Overrides of Object
	//-----------------------------------------------------------------------------

	/**
	 * Equality of two <code>MultiMap</code>s is determined by their delegate
	 * maps, <em>not considering the collection strategy</em>. Although this
	 * means that there is a possibility that two instances will be considered
	 * equal and then diverge, that can happen with normal collection instances.
	 * The moral is not to depend on mutable objects not mutating.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if ((obj instanceof MultiMap) && (obj.getClass() == this.getClass())) {
			return _delegate.equals(((MultiMap) obj)._delegate);
		}
		return false;
	}


	@Override
	public int hashCode() {
		return _delegate.hashCode();
	}


	@Override
	public String toString() {
		return _delegate.toString();
	}
}
