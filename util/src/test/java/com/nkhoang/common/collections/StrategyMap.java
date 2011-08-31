package com.nkhoang.common.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * A decorator for <code>java.util.Map</code> that applies a strategy when
 * accessing the delegate map.
 * <p/>
 * Where would this be used? Consider a mapping from <code>Class</code> to
 * a functor. In a polymorphic world, you may actually be handed a subclass
 * instance, so this rules out a direct use of <code>Map&lt;Class,Functor&gt;
 * </code>. However, you can define a strategy where <code>get()</code> will
 * recursively call itself with the passed key's superclass, potentially
 * returning <em>some</em> functor that can handle the object.
 * <p/>
 * Another use is for a map that validates items on entry -- for example,
 * a parameterized map that actually tries to cast its items before they're
 * added, preventing legacy code from adding incorrect objects to a type-
 * erased instance.
 * <p/>
 * You might want strategies that perform type transformation on the objects
 * passed to them -- for example, storing classnames in the delegate map,
 * and returning <code>Class</code> instances. To keep the interface simple,
 * this is <em>not</em> supported by <code>StrategyMap</code>; the delegate
 * map must have the same type parameters as the strategy. However, feel free
 * to add transformation methods to your strategy and call those explicitly
 * from using code.
 * <p/>
 * To use, subclass the {@link com.nkhoang.common.collections.StrategyMap.Strategy} class, and override those methods
 * that apply to your strategy. For example, if you want to perform entry
 * validation, override <code>prePut()</code>; if you want to perform
 * alternative lookups, override <code>get()</code>.
 * <p/>
 * A strategy should provide behavior only, and not maintain instance data.
 * If, for some reason, you do want to maintain instance data, you will need
 * to consider synchronization and also the fact that <code>Strategy</code>
 * is <code>Serializable</code>.
 * <p/>
 * <em>Warning:</em>
 * The strategy is applied to all accesses to the delegate map, <strong>
 * except</strong> for the following: {@link #entrySet}, {@link #keySet},
 * and {@link #values}. Future implementations may remove this limit.
 */
public class StrategyMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = -6483841440062870238L;

	/**
	 * Override this class to implement your strategy.
	 * <p/>
	 * When retrieving items from the delegate map, <code>StrategyMap</code>
	 * first calls {@link #get} to retrieve the item, then calls {@link
	 * #postGet} to transform that item before returning it to the caller.
	 * <p/>
	 * When adding items to the delegate map, <code>StrategyMap</code> first
	 * calls {@link #prePut} on the value, then calls {@link #put} to store
	 * the value in the map, followed {@link #postGet} on the value returned
	 * by the delegate map's <code>put()</code>.
	 */
	public static class Strategy<K, V> implements Serializable {
		private final static long serialVersionUID = 1L;

		/**
		 * Called by <code>StrategyMap.contains()</code> to determine whether
		 * the delegate map contains the specified key, after applying the
		 * strategy. The default implementation calls <code>Map.contains()
		 * </code> on the delegate.
		 * <p/>
		 * <em>Note:</em> Per <code>java.util.Map</code> interface, <code>key
		 * </code> is of type <code>Object</code> not the map's parameterized
		 * key type.
		 *
		 * @return true if the map contains a mapping for the key
		 */
		public boolean containsKey(Map<K, V> map, Object key) {
			return map.containsKey(key);
		}


		/**
		 * This method is called to transform the value before it is stored
		 * in the delegate map. The default implementation returns the
		 * unmodified value.
		 *
		 * @param value The value to transform.
		 *
		 * @return The transformed value.
		 */
		public V prePut(V value) {
			return value;
		}


		/**
		 * This method is called when adding elements to the delegate map.
		 * The default implementation calls <code>Map.put()</code> on the
		 * delegate.
		 *
		 * @param map   The delegate map.
		 * @param key   The key passed to <code>StrategyMap.put()</code>.
		 *              The strategy may call the delegate's <code>put()
		 *              </code> using any value it likes.
		 * @param value The value to store in the delegate map. This value
		 *              has been transformed with {@link #prePut}.
		 *
		 * @return The value returned from calling <code>put()</code> on the
		 *         delegate map
		 */
		public V put(Map<K, V> map, K key, V value) {
			return map.put(key, value);
		}


		/**
		 * This method is called to retrieve elements from the delegate map.
		 * The default implementation calls <code>Map.get()</code> on the
		 * delegate.
		 *
		 * @param map The delegate map.
		 * @param key The key passed to <code>StrategyMap.get()</code>.
		 *            The strategy may call the delegate's <code>get()
		 *            </code> using any value it likes.
		 *            <br><em>Note:</em>
		 *            Per <code>java.util.Map</code> interface, this is
		 *            <code>Object</code>, not a parameterized type.
		 *
		 * @return The value returned by calling <code>get()</code> on the
		 *         delegate map.
		 */
		public V get(Map<K, V> map, Object key) {
			return map.get(key);
		}


		/**
		 * This method is called after a value is retrieved from the delegate
		 * map, to transform that value before returning it to the user. The
		 * default implementation returns the unmodified value.
		 * <p/>
		 * Implementors must handle <code>null</code> values without throwing
		 * an exception, as this method will be called as part of the <code>
		 * StrategyMap.put()</code> operation, and an empty delegate map will
		 * return <code>null</code>.
		 *
		 * @param value The value to transform.
		 *
		 * @return The transformed value.
		 */
		public V postGet(V value) {
			return value;
		}


		/**
		 * This method is called to remove an entry from the underlying map. The
		 * default implementation calls <code>Map.remove()</code> on the delegate.
		 */
		public V remove(Map<K, V> map, Object key) {
			return map.remove(key);
		}
	}


	//-----------------------------------------------------------------------------
	//  Instance variables and constructors
	//-----------------------------------------------------------------------------

	private Map<K, V>      _delegate;
	private Strategy<K, V> _strategy;

	/**
	 * Initializes a new StrategyMap
	 *
	 * @param delegate
	 * @param strategy
	 */
	public StrategyMap(Map<K, V> delegate, Strategy<K, V> strategy) {
		_delegate = delegate;
		_strategy = strategy;
	}


	//-----------------------------------------------------------------------------
	//  Implementation of Map
	//-----------------------------------------------------------------------------

	/** Removes all entries from the delegate map. */
	public void clear() {
		_delegate.clear();
	}


	/**
	 * Determines whether the map contains the specified key, using the
	 * strategy's <code>containsKey()</code> method.
	 */
	public boolean containsKey(Object key) {
		return _strategy.containsKey(_delegate, key);
	}


	/**
	 * Determines whether the map contains a mapping to the specified value,
	 * after applying the strategy. More formally, the map contains some key
	 * <code>k</code>, such that <code>get(k).equals(value)</code> or <code>
	 * (value == null) && (get(k) == null)</code>.
	 */
	public boolean containsValue(Object value) {
		for (K key : keySet()) {
			V mapValue = get(key);
			if ((value == null) && (mapValue == null)) {
				return true;
			} else if ((value != null) && value.equals(mapValue)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns the <code>entrySet</code> of the delegate map, <em>without
	 * transforming any values</em>.
	 */
	public Set<Entry<K, V>> entrySet() {
		return _delegate.entrySet();
	}

	/**
	 * Applies the strategy's <code>get()</code> method to retrieve the value
	 * from the map, then transforms that value using the strategy's <code>
	 * postGet()</code> method.
	 *
	 * @return The transformed value.
	 */
	public V get(Object key) {
		return _strategy.postGet(
			_strategy.get(_delegate, key));
	}


	/**
	 * Determines whether the map is empty. This is a simple pass-through
	 * to the delegate.
	 */
	public boolean isEmpty() {
		return _delegate.isEmpty();
	}


	/** Returns the <code>keySet</code> of the delegate map. */
	public Set<K> keySet() {
		return _delegate.keySet();
	}


	/**
	 * Transforms the passed value using the strategy's <code>prePut()</code>
	 * method, then stores it in the delegate map using the strategy's <code>
	 * put()</code> method.
	 *
	 * @return The value previously in the delegate map, transformed using
	 *         the strategy's <code>postGet()</code> method.
	 */
	public V put(K key, V value) {
		return _strategy.postGet(
			_strategy.put(
				_delegate, key, _strategy.prePut(value)));
	}


	/**
	 * Inserts all entries from the passed map into the map, first applying
	 * the strategy's <code>prePut()</code> method to the value, then using
	 * the strategy's <code>put()</code> method to insert the value into the
	 * delegate.
	 * <p/>
	 * In consideration of the fact that the passed map might itself be a
	 * <code>StrategyMap</code>, iterates that map's keyset rather than its
	 * entryset.
	 */
	public void putAll(Map<? extends K, ? extends V> map) {
		for (K key : map.keySet()) {
			put(key, map.get(key));
		}
	}


	/**
	 * Applies the strategy to remove the mapping for the specified key.
	 *
	 * @return The value previously in the delegate map, transformed using
	 *         the strategy's <code>postGet()</code> method.
	 */
	public V remove(Object key) {
		return _strategy.postGet(
			_strategy.remove(_delegate, key));
	}


	/**
	 * Returns the number of entries in the map. This is a simple pass-through
	 * to the delegate.
	 */
	public int size() {
		return _delegate.size();
	}


	/**
	 * Returns the <code>values</code> of the delegate map, <em>without
	 * any transformation</em>.
	 */
	public Collection<V> values() {
		return _delegate.values();
	}

}
