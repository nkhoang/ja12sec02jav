package com.nkhoang.common.collections;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * A <code>Set</code> implementation backed by an <code>IdentityHashMap</code>,
 * basing equality on object identity rather than the result of <code>equals()
 * </code>.
 * <p/>
 * It is useful for managing listener lists, where you don't want to rely on
 * the objects telling you that they're equal.
 */
public class IdentitySet<E> implements Set<E> {

	private IdentityHashMap<E, Object> _store = new IdentityHashMap<E, Object>();

	public boolean add(E o) {
		if (_store.containsKey(o)) {
			return false;
		}
		_store.put(o, null);
		return true;
	}


	public boolean addAll(Collection<? extends E> c) {
		boolean ret = false;
		for (E obj : c) {
			ret |= add(obj);
		}
		return ret;
	}


	public void clear() {
		_store.clear();
	}


	public boolean contains(Object o) {
		return _store.containsKey(o);
	}


	public boolean containsAll(Collection<?> c) {
		boolean ret = true;
		for (Object obj : c) {
			ret &= contains(obj);
		}
		return ret;
	}


	public boolean isEmpty() {
		return _store.size() == 0;
	}


	public Iterator<E> iterator() {
		return _store.keySet().iterator();
	}


	public boolean remove(Object o) {
		if (_store.containsKey(o)) {
			_store.remove(o);
			return true;
		}
		return false;
	}


	public boolean removeAll(Collection<?> c) {
		boolean ret = false;
		for (Object obj : c) {
			ret |= remove(obj);
		}
		return ret;
	}


	public boolean retainAll(Collection<?> c) {
		boolean ret = false;
		for (Object obj : _store.keySet()) {
			if (!c.contains(obj)) {
				ret |= remove(obj);
			}
		}
		return ret;
	}


	public int size() {
		return _store.size();
	}


	public Object[] toArray() {
		return _store.keySet().toArray();
	}


	public <T> T[] toArray(T[] a) {
		return _store.keySet().toArray(a);
	}
}
