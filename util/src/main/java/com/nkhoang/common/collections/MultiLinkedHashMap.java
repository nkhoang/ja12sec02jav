package com.nkhoang.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiMap;

/**
 * Linked hash implementation of MultiMap
 *
 * @deprecated use {@link com.hmsonline.common.collections.MultiMap} instead
 */
@Deprecated
@SuppressWarnings("unchecked")
public class MultiLinkedHashMap extends LinkedHashMap implements MultiMap {

	private static final long serialVersionUID = 1L;

	@Override
	public Object put(Object key, Object value) {
		if (value instanceof KeyList) {
			return super.put(key, value);
		}
		KeyList keyList = (KeyList) super.get(key);
		if (keyList == null) {
			keyList = new KeyList(10);
			super.put(key, keyList);
		}
		return keyList.add(value) ? value : null;
	}

	/**
	 * Puts all of the values into the collection with the associated key
	 *
	 * @return true if the list changed as a result of the call
	 */
	public boolean putAll(Object key, Collection values) {
		KeyList keyList = (KeyList) super.get(key);
		if (keyList == null) {
			keyList = new KeyList(10);
			super.put(key, keyList);
		}
		return keyList.addAll(values);
	}

	@Override
	public boolean containsValue(Object value) {
		Set pairs = super.entrySet();
		if (pairs == null) {
			return false;
		}
		Iterator pairsIterator = pairs.iterator();
		while (pairsIterator.hasNext()) {
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			KeyList list = (KeyList) keyValuePair.getValue();
			if (list.contains(value)) {
				return true;
			}
		}
		return false;
	}

	public Object remove(Object key, Object item) {
		KeyList valuesForKey = (KeyList) super.get(key);
		if (valuesForKey == null) {
			return null;
		}
		if (valuesForKey.remove(item)) {
			return item;
		} else {
			return null;
		}
	}

	@Override
	public void clear() {
		Set pairs = super.entrySet();
		Iterator pairsIterator = pairs.iterator();
		while (pairsIterator.hasNext()) {
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			KeyList list = (KeyList) keyValuePair.getValue();
			list.clear();
		}
		super.clear();
	}

	@Override
	public Collection values() {
		ArrayList returnList = new ArrayList(super.size());
		Set pairs = super.entrySet();
		Iterator pairsIterator = pairs.iterator();
		while (pairsIterator.hasNext()) {
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			returnList.addAll((KeyList) keyValuePair.getValue());
		}
		return returnList;
	}

	@Override
	public Object clone() {
		MultiLinkedHashMap cloned = (MultiLinkedHashMap) super.clone();
		// clone each Collection container
		for (Iterator it = cloned.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			Collection coll = (Collection) entry.getValue();
			if (coll != null) {
				KeyList newColl = new KeyList();
				newColl.addAll(coll);
				entry.setValue(newColl);
			}
		}
		return cloned;
	}

	private static class KeyList extends ArrayList {
		private static final long serialVersionUID = 1L;

		/** Initializes a new KeyList */
		public KeyList() {
			super();
		}

		/**
		 * Initializes a new KeyList with the specified initial capacity
		 *
		 * @param initialCapacity
		 */
		public KeyList(int initialCapacity) {
			super(initialCapacity);
		}
	}

}
