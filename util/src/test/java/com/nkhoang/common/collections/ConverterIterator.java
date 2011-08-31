package com.nkhoang.common.collections;

import java.util.Iterator;

/**
 * Useful almost concrete Iterator implementation for the common situation
 * where you need to convert data from one iterator into data in another
 * iterator.  Everything in this class is implemented as expected, and the
 * {@link #convert} method is called on each element as it is returned from
 * the underlying iterator.
 */
public abstract class ConverterIterator<InType, OutType> implements Iterator<OutType> {
	/** source data iterator */
	private final Iterator<? extends InType> _iter;

	public ConverterIterator(Iterator<? extends InType> iter) {
		_iter = iter;
	}

	public boolean hasNext() {
		return _iter.hasNext();
	}

	public OutType next() {
		return convert(_iter.next());
	}

	public void remove() {
		_iter.remove();
	}

	/**
	 * Converts from the input type to the output type.  Called by the
	 * {@link #next} method.
	 *
	 * @param in element to convert
	 *
	 * @return the input value converted to the output type
	 */
	protected abstract OutType convert(InType in);

}
