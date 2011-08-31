package com.nkhoang.common.collections;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Useful for providing an unmodifiable view of multiple collections as one
 * contiguous collection without copying a lot of elements around.  While
 * collections generally make no guarantees on ordering, this implementation
 * will return elements from each collection based on the order the collection
 * was added to this object, and from within each collection based on the
 * order of that collections iterator.  Also, while the Collection
 * implementation is unmodifiable, the {@link #collections} method provides a
 * modifiable view of the underlying collection.
 * <p/>
 * Concurrent usage requirements are those of any other normal collection.
 *
 */
public class CollectionOfCollections<T> extends AbstractCollection<T> implements Serializable {
	private static final long serialVersionUID = -5995040883022063397L;

	private final COCList<T> _collections = new COCList<T>();

	/** Constructs an empty collection of collections. */
	public CollectionOfCollections() {
		this(null);
	}

	/**
	 * Constructs a collection of collections initially containing the given
	 * collection.  {@code null} collections are ignored.
	 *
	 * @param c0 The initial collection
	 */
	public CollectionOfCollections(Collection<? extends T> c0) {
		this(c0, null);
	}

	/**
	 * Constructs a collection of collections initially containing the given
	 * collections.  {@code null} collections are ignored.
	 *
	 * @param c0 The first initial collection
	 * @param c1 The second initial collection
	 */
	public CollectionOfCollections(
		Collection<? extends T> c0, Collection<? extends T> c1) {
		if (c0 != null) {
			_collections.add(c0);
		}
		if (c1 != null) {
			_collections.add(c1);
		}
	}

	/**
	 * Adds a reference to the given collection to this collection of
	 * collections.  {@code null} collections are ignored.  Equivalent to
	 * {@code collections().add(c)}.
	 *
	 * @param c The collection to add
	 *
	 * @return true if the collection of collections changed as a result of
	 *         the call
	 */
	public boolean addCollection(Collection<? extends T> c) {
		return _collections.add(c);
	}

	/**
	 * Adds a collection of the given elements to this collection of
	 * collections.  {@code null} collections are ignored.  Equivalent to
	 * {@code addCollection(Arrays.asList(elements))}.
	 *
	 * @param elements The elements to add
	 *
	 * @return true if the collection of collections changed as a result
	 *         of the call
	 */
	public <SubT extends T> boolean addElements(SubT... elements) {
		if (elements != null) {
			return _collections.add(Arrays.asList(elements));
		}
		return false;
	}

	/**
	 * Provides a modifiable view of the collection of collections backing this
	 * object.
	 *
	 * @return The collection of collections
	 */
	public List<Collection<? extends T>> collections() {
		return _collections;
	}

	@Override
	public int size() {
		int size = 0;
		for (Collection<? extends T> c : _collections) {
			size += c.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (Collection<? extends T> c : _collections) {
			if (!c.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return new COCIterator<T>(_collections);
	}

	/** Subclass of ArrayList which ignores addition of {@code null} elements. */
	private static class COCList<T> extends ArrayList<Collection<? extends T>> implements Serializable {
		private static final long serialVersionUID = 0L;

		@Override
		public boolean add(Collection<? extends T> c) {
			if (c != null) {
				return super.add(c);
			}
			return false;
		}
	}

	/**
	 * Iterator implementation for the collection of collections which iterates
	 * through all the elements of all the collections.
	 */
	private static class COCIterator<T> implements Iterator<T> {
		private final Iterator<Collection<? extends T>> _cIter;
		private       Iterator<? extends T>             _iter;

		private COCIterator(List<Collection<? extends T>> collections) {
			_cIter = collections.iterator();
			if (_cIter.hasNext()) {
				_iter = _cIter.next().iterator();
			}
			findNext();
		}

		private void findNext() {
			while ((_iter != null) && (!_iter.hasNext())) {
				_iter = (_cIter.hasNext() ? _cIter.next().iterator() : null);
			}
		}

		public boolean hasNext() {
			return ((_iter != null) && (_iter.hasNext()));
		}

		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T next = _iter.next();
			findNext();
			return next;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
