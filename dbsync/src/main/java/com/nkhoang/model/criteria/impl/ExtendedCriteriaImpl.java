package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IExtendedSearchCriteria;

/**
 * The Class ExtendedCriteriaImpl.
 */
public class ExtendedCriteriaImpl extends SearchCriteriaImpl implements IExtendedSearchCriteria {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -5323875552974160396L;

  /**
   * The page start.
   */
  private Long pageStart = null;

  /**
   * The page size.
   */
  private Integer pageSize = null;

  /**
   * The archived flag.
   */
  private Boolean archived = null;

  /**
   * The Constructor.
   */
  public ExtendedCriteriaImpl() {
    super();
  }

  /*
    * (non-Javadoc)
    * @see com.nkhoang.model.criteria.ISearchCriteria#getPageSize()
    */
  public Integer getPageSize() {
    return pageSize;
  }

  /*
    * (non-Javadoc)
    * @see com.nkhoang.model.criteria.ISearchCriteria#getPageStart()
    */
  public Long getPageStart() {
    return pageStart;
  }

  /*
    * (non-Javadoc)
    * @see com.nkhoang.model.criteria.ISearchCriteria#setPageSize(java.lang.Integer)
    */
  public void setPageSize(final Integer value) {
    pageSize = value;
  }

  /*
    * (non-Javadoc)
    * @see com.nkhoang.model.criteria.ISearchCriteria#setPageStart(java.lang.Long)
    */
  public void setPageStart(final Long value) {
    pageStart = value;
  }

  /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();

    builder.append(super.toString());
    builder.append("[pageSize=");
    builder.append(pageSize);
    builder.append(", pageStart=");
    builder.append(pageStart);
    builder.append(", archived=");
    builder.append(archived);
    builder.append("]");

    return builder.toString();
  }
}
