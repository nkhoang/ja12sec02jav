package com.nkhoang.common.collections;

import java.io.Serializable;


/**
 * Implements <code>equals</code> and <code>hashCode</code> such that only the
 * instance identity of the object is used to identify the object (ignores any
 * custom implementation of equals/hashCode on the object itself).  Useful for
 * HashMaps, HashSets, etc. where only the instance identity of the object is
 * interesting.
 */
public class IdentityKey<ObjType> implements Serializable {
	private static final long serialVersionUID = -4807405948246174713L;

	/** the actual object whose identity is being used by this key */
	private final ObjType _obj;

	/**
	 * Initializes a new IdentityKey
	 *
	 * @param obj The object whose identity will be used by the key
	 */
	public IdentityKey(ObjType obj) {
		_obj = obj;
	}

	/** @return the actual object whose identity is being used by this key */
	public ObjType get() {
		return _obj;
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(_obj);
	}

	@Override
	public final boolean equals(Object o) {
		return ((o instanceof IdentityKey) && (((IdentityKey) o)._obj == _obj));
	}

}
