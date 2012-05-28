package com.nkhoang.wybness.model.criteria.impl;

import com.nkhoang.model.criteria.impl.ExtendedCriteriaImpl;
import com.nkhoang.wybness.model.criteria.IBookingTypeCriteria;

/**
 * The Class BookingTypeCriteriaImpl.
 */
public class BookingTypeCriteriaImpl extends ExtendedCriteriaImpl implements IBookingTypeCriteria {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5781804069003150181L;

	/** The name pattern. */
	private String namePattern = null;

	/**
	 * The Constructor.
	 */
	public BookingTypeCriteriaImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.nkhoang.wybness.model.criteria.IBookingTypeCriteria#getNamePattern()
	 */
	public String getNamePattern() {
		return namePattern;
	}

	/*
	 * (non-Javadoc)
	 * @see com.nkhoang.wybness.model.criteria.IBookingTypeCriteria#setNamePattern(java.lang.String)
	 */
	public void setNamePattern(final String value) {
		namePattern = value;
	}

	/*
	 * See super class or interface. (non-Javadoc)
	 * @see com.wybness.common.impl.model.criteria.SearchCriteriaImpl#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append(super.toString());
		builder.append("[namePattern=");
		builder.append(namePattern);
		builder.append("]");

		return builder.toString();
	}
}
