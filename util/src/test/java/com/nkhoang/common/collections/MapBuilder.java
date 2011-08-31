package com.nkhoang.common.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;


/**
 * For those annoying cases where you need to build a static map, but can't
 * use a static initializer block (such as in an interface).  Use like apache
 * commons builder classes.
 * <p/>
 * Example:
 * <pre>
 * public static final Map<String, Integer> THE_MAP =
 *   new MapBuilder<String, Integer>(new HashMap<String, Integer>())
 *   .put("one", 1)
 *   .put("two", 2)
 *   .put("three", 3)
 *   .toMap();
 * </pre>
 */
public class MapBuilder<KeyType, ValueType> {

	/**
	 * Enum which specifies how to handle collections with differing lengths in
	 * the {@link #putAll(Iterable, Iterable, com.nkhoang.common.collections.MapBuilder.MismatchBehavior)} method.
	 */
	public enum MismatchBehavior {
		/**
		 * Throw an IllegalArgumentException if given collections have different
		 * lengths
		 */
		ERROR,
		/** Ignore any remaining elements in the longer of the two collections */
		IGNORE,
		/**
		 * Insert <code>null</code> for the missing keys/values for the shorter
		 * of the two collections.
		 */
		USE_NULL;
	}

	private Map<KeyType, ValueType> _map;
	private boolean                 _unmodifiable;
	private Collection<?>           _requiredKeys;
	private Collection<?>           _requiredValues;

	/** Initializes a new MapBuilder with a HashMap */
	public MapBuilder() {
		this(new HashMap<KeyType, ValueType>());
	}

	/**
	 * Initializes a new MapBuilder with the given map
	 *
	 * @param map the map to build
	 */
	public MapBuilder(Map<KeyType, ValueType> map) {
		_map = map;
	}

	/**
	 * Adds the given key value pair to the MapBuilder
	 *
	 * @param key   the key to add to the Map
	 * @param value the value to add to the Map
	 *
	 * @return this MapBuilder
	 */
	public MapBuilder<KeyType, ValueType> put(KeyType key, ValueType value) {
		_map.put(key, value);
		return this;
	}

	/**
	 * Takes two parallel collections and pairs the corresponding elements from
	 * each collection as key value pairs.  The given collections must be the
	 * same length.
	 *
	 * @return this MapBuilder
	 */
	public MapBuilder<KeyType, ValueType> putAll(
		Iterable<? extends KeyType> keys, Iterable<? extends ValueType> values) {
		return putAll(keys, values, MismatchBehavior.ERROR);
	}

	/**
	 * Takes two parallel collections and pairs the corresponding elements from
	 * each collection as key value pairs.  Mismatched length collections will
	 * be handled according to the given given mismathBehavior param.
	 *
	 * @return this MapBuilder
	 */
	public MapBuilder<KeyType, ValueType> putAll(
		Iterable<? extends KeyType> keys, Iterable<? extends ValueType> values, MismatchBehavior mismatchBehavior) {
		Iterator<? extends KeyType> keyIter = keys.iterator();
		Iterator<? extends ValueType> valueIter = values.iterator();
		while (keyIter.hasNext() && valueIter.hasNext()) {
			_map.put(keyIter.next(), valueIter.next());
		}
		switch (mismatchBehavior) {
			case ERROR:
				if (keyIter.hasNext() || valueIter.hasNext()) {
					throw new IllegalArgumentException(
						"Keys and Values must be same length");
				}
				break;
			case IGNORE:
				// nothing to do
				break;
			case USE_NULL:
				while (keyIter.hasNext()) {
					_map.put(keyIter.next(), null);
				}
				while (valueIter.hasNext()) {
					_map.put(null, valueIter.next());
				}
				break;
			default:
				throw new RuntimeException(
					"Unknown mismatchBehavior given: " + mismatchBehavior);
		}
		return this;
	}

	/**
	 * Iff <code>true</code>, the returned map will be unmodifiable, otherwise,
	 * the given map will be returned as is.
	 *
	 * @return this MapBuilder
	 */
	public MapBuilder<KeyType, ValueType> setUnmodifiable(boolean unmodifiable) {
		_unmodifiable = unmodifiable;
		return this;
	}

	/**
	 * Can be used to require a specific set of keys in the final map.
	 *
	 * @see #toMap
	 */
	public MapBuilder<KeyType, ValueType> requireKeys(
		Object... requiredKeys) {
		return requireKeys(Arrays.asList(requiredKeys));
	}

	/**
	 * Can be used to require a specific set of keys in the final map.
	 *
	 * @see #toMap
	 */
	public MapBuilder<KeyType, ValueType> requireKeys(
		Collection<?> requiredKeys) {
		_requiredKeys = requiredKeys;
		return this;
	}

	/**
	 * Can be used to require a specific set of values in the final map.
	 *
	 * @see #toMap
	 */
	public MapBuilder<KeyType, ValueType> requireValues(
		Object... requiredValues) {
		return requireValues(Arrays.asList(requiredValues));
	}

	/**
	 * Can be used to require a specific set of values in the final map.
	 *
	 * @see #toMap
	 */
	public MapBuilder<KeyType, ValueType> requireValues(
		Collection<?> requiredValues) {
		_requiredValues = requiredValues;
		return this;
	}


	/**
	 * Verifies that the given found collection contains all elements in the
	 * found collection.
	 *
	 * @param expected elements that should exist
	 * @param found    elements that currently exist
	 *
	 * @throws RuntimeException if any elements from the expected collection are
	 *                          not in the found collection
	 */
	private void requireCollection(
		Collection<?> expected, Collection<?> found) {
		if ((expected != null) && !found.containsAll(expected)) {
			Collection<Object> missing = new ArrayList<Object>(expected);
			missing.removeAll(found);
			throw new RuntimeException("Missing required keys: " + missing);
		}
	}

	/**
	 * Returns the map
	 *
	 * @return this MapBuilder
	 *
	 * @throws RuntimeException if there is a required collection of
	 *                          keys and not all of them are found in the final map
	 */
	public Map<KeyType, ValueType> toMap() {
		requireCollection(_requiredKeys, _map.keySet());
		requireCollection(_requiredValues, _map.values());
		if (_unmodifiable) {
			return Collections.unmodifiableMap(_map);
		}
		return _map;
	}

}
