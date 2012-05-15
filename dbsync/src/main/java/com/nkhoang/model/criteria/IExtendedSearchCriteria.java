package com.nkhoang.model.criteria;

/**
 * @author hnguyen
 */
public interface IExtendedSearchCriteria extends ISearchCriteria {

	/**
	 * Gets the size of the page of results.
	 * @return the size
	 */
	Integer getPageSize();

	/**
	 * Gets the starting result. Use to choose a page.
	 * @return the start
	 */
	Long getPageStart();

	/**
	 * Sets the size of the page of results. If null, paging is disabled.
	 * @param value the size
	 */
	void setPageSize(Integer value);

	/**
	 * Sets the start. Use to choose a page. If null, paging is disabled.
	 * @param value the start
	 */
	void setPageStart(Long value);
}
