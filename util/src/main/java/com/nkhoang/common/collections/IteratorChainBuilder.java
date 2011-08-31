package com.nkhoang.common.collections;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.SingletonIterator;

/**
 * Builder around {@link org.apache.commons.collections.iterators.IteratorChain}
 * which also makes it JDK 1.5 friendly (uses generics and Iterable).
 */
public class IteratorChainBuilder<E> implements Iterable<E>, Iterator<E> {

	/**
	 * maintains the various Iterators and manages iterating over the contained
	 * elements
	 */
	private IteratorChain _chain = new IteratorChain();

	/**
	 * Adds the Iterator of the given Iterable to the IteratorChain if
	 * non-<code>null</code>.
	 *
	 * @param iterator The iterator to add to the IteratorChain
	 *
	 * @return This IteratorChaingBuilder
	 */
	public IteratorChainBuilder<E> add(Iterator<? extends E> iterator) {
		if (iterator != null) {
			_chain.addIterator(iterator);
		}
		return this;
	}

	/**
	 * Adds the Iterator of the given Iterable to the IteratorChain if
	 * non-<code>null</code>.
	 *
	 * @param iterable The iterable whose iterator will be added to the
	 *                 IteratorChain
	 *
	 * @return This IteratorChaingBuilder
	 */
	public IteratorChainBuilder<E> add(Iterable<? extends E> iterable) {
		if (iterable != null) {
			add(iterable.iterator());
		}
		return this;
	}

	/**
	 * Adds the Iterator of the given Iterable to the IteratorChain if
	 * non-<code>null</code>.
	 *
	 * @param arr The iterables whose iterators will be added to the
	 *            IteratorChain
	 *
	 * @return This IteratorChaingBuilder
	 */
	public IteratorChainBuilder<E> add(E... arr) {
		if (arr != null) {
			add(Arrays.asList(arr).iterator());
		}
		return this;
	}

	/**
	 * Convenience method to add an Iterator containing the given single element
	 * to this IteratorChain.
	 *
	 * @param e The iterable whose iterator will be added to the
	 *          IteratorChain
	 *
	 * @return This IteratorChaingBuilder
	 */
	public IteratorChainBuilder<E> addElement(E e) {
		_chain.addIterator(new SingletonIterator(e));
		return this;
	}

	/**
	 * Convenience method to add an Iterator containing the given single element
	 * to this IteratorChain iff the given element is not <code>null</code>.
	 *
	 * @param e The iterable whose iterator will be added to the
	 *          IteratorChain
	 *
	 * @return This IteratorChaingBuilder
	 */
	public IteratorChainBuilder<E> addElementIfNotNull(E e) {
		if (e != null) {
			addElement(e);
		}
		return this;
	}

	public Iterator<E> iterator() {
		return this;
	}

	public boolean hasNext() {
		return _chain.hasNext();
	}

	@SuppressWarnings("unchecked")
	public E next() {
		return (E) _chain.next();
	}

	public void remove() {
		_chain.remove();
	}

	/** @return result of calling {@link org.apache.commons.collections.iterators.IteratorChain#isLocked} */
	public boolean isLocked() {
		return _chain.isLocked();
	}

}
