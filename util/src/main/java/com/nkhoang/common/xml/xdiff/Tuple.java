package com.nkhoang.common.xml.xdiff;

/**
 * Simple Tuple class for use by the XDiff algorithm.
 *
 * @param <T>
 */
class Tuple<T> {
	public T _e1;
	public T _e2;


	public Tuple(T e1, T e2) {
		_e1 = e1;
		_e2 = e2;
	}


	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other.getClass() != Tuple.class) {
			return false;
		} else {
			Tuple<T> tuple = (Tuple<T>) other;
			if (valuesEqual(_e1, tuple._e1) && valuesEqual(_e2, tuple._e2)) {
				return true;
			}
			return false;
		}
	}


	@Override
	public int hashCode() {
		if (_e1 == null) {
			return _e2.hashCode();
		}
		if (_e2 == null) {
			return _e1.hashCode() * 2;
		}
		return _e1.hashCode() * 2 + _e2.hashCode();
	}


	/** Null-safe equals check wrapper */
	private boolean valuesEqual(T v1, T v2) {
		if (v1 == null) {
			if (v2 == null) {
				return true;
			} else {
				return false;
			}
		} else {
			return v1.equals(v2);
		}
	}
}
