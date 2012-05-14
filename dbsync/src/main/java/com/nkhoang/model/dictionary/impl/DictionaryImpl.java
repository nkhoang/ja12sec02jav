package com.nkhoang.model.dictionary.impl;

import com.nkhoang.model.dictionary.IDictionary;

public class DictionaryImpl implements IDictionary {
  /**
   * The identifier.
   */
  private Long _id;

  /**
   * The dictionary _name
   */
  private String _name;

  public Long getKey() {
    return _id;
  }

  public void setKey(Long id) {
    _id = id;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }
}
