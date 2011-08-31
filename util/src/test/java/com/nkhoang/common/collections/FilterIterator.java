package com.nkhoang.common.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Useful almost concrete Iterator implementation for the situation where you
 * need to filter data from one iterator based on certain criteria.  The
 * {@link #include} method is called on each element as it is returned from
 * the underlying iterator.
 * <p/>
 * Note, while everything in this class is implemented pretty much as
 * expected, the remove method has an additional restriction that the element
 * removed will be affected by both a call to {@link #next} or a call to
 * {@link #hasNext}.
 */
public abstract class FilterIterator<T> implements Iterator<T> {
	/** source data iterator */
	private final Iterator<? extends T> _iter;
	/** whether or the data has been prepared for the current next() call */
	private       boolean               _foundNext;
	private       boolean               _hasNext;
	private       T                     _next;

	public FilterIterator(Iterator<? extends T> iter) {
		_iter = iter;
	}

	private void findNext() {
		// search for the next included key
		_hasNext = false;
		_next = null;
		while (_iter.hasNext()) {
			T next = _iter.next();
			if (include(next)) {
				_hasNext = true;
				_next = next;
				return;
			}
		}
	}

	public boolean hasNext() {
		if (!_foundNext) {
			findNext();
			_foundNext = true;
		}
		return _hasNext;
	}

	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		T cur = _next;
		_foundNext = false;
		return cur;
	}

	public void remove() {
		if (!_hasNext) {
			throw new IllegalStateException();
		}
		_iter.remove();
	}

	/**
	 * Tests the given element from the underlying iterator for inclusion in the
	 * filtered iterator.  Called by the {@link #next} method.
	 *
	 * @param t element test for inclusion
	 *
	 * @return {@code true} if the given element should be included in the final
	 *         results, {@code false} otherwise
	 */
	protected abstract boolean include(T t);

}
