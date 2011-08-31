package com.nkhoang.common.collections;

import java.util.Iterator;

/**
 * Adaptation of {@link MapBuilder} for {@link MultiMap}s (since they are
 * NOT an implementation of {@link java.util.Map})
 *
 */
public class MultiMapBuilder<KeyType, ValueType> {

	/**
	 * Enum which specifies how to handle collections with differing lengths in
	 * the {@link com.nkhoang.common.collections.MultiMapBuilder#putAll(Iterable, Iterable,
	 * com.hmsonline.common.collections.MultiMapBuilder.MismatchBehavior)}
	 * method.
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

	private MultiMap<KeyType, ValueType> _map;
	private boolean                      _unmodifiable;

	/**
	 * Initializes a new MapBuilder with the default MultiMap implementation
	 * (currently mapped by a HashMap)
	 */
	public MultiMapBuilder() {
		this(new MultiMap<KeyType, ValueType>());
	}

	/**
	 * Initializes a new MapBuilder with the given map
	 *
	 * @param map the map to build
	 */
	public MultiMapBuilder(MultiMap<KeyType, ValueType> map) {
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
	public MultiMapBuilder<KeyType, ValueType> put(KeyType key, ValueType value) {
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
	public MultiMapBuilder<KeyType, ValueType> putAll(
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
	public MultiMapBuilder<KeyType, ValueType> putAll(
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
	public MultiMapBuilder<KeyType, ValueType> setUnmodifiable(boolean unmodifiable) {
		_unmodifiable = unmodifiable;
		return this;
	}

	/**
	 * Returns the map
	 *
	 * @return this MapBuilder
	 */
	public MultiMap<KeyType, ValueType> toMultiMap() {
		if (_unmodifiable) {
			return _map.toUnmodifiableMultiMap();
		}
		return _map;
	}

}
