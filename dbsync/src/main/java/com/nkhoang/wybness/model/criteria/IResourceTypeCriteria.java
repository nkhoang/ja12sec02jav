package com.nkhoang.wybness.model.criteria;


import com.nkhoang.model.criteria.IExtendedSearchCriteria;

/**
 * The Interface IResourceTypeCriteria.
 */
public interface IResourceTypeCriteria extends IExtendedSearchCriteria {

  /**
   * Gets the name pattern.
   *
   * @return the name pattern
   */
  String getNamePattern();

  /**
   * Sets the name pattern. If null, pattern is ignored.
   *
   * @param value the name pattern
   */
  void setNamePattern(String value);
}
