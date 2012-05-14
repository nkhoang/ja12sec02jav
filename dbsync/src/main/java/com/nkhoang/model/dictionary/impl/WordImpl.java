package com.nkhoang.model.dictionary.impl;

import com.nkhoang.model.dictionary.IDictionary;
import com.nkhoang.model.dictionary.IWord;
import org.joda.time.DateTime;

public class WordImpl implements IWord {
  private Long _id;
  private String _data;
  private IDictionary _dictionary;
  private DateTime _creationDate;
  private DateTime _modificationDate;

  public Long getKey() {
    return _id;
  }

  public void setKey(Long id) {
    _id = id;
  }

  public IDictionary getDictionary() {
    return _dictionary;
  }

  public void setDictionary(IDictionary dictionary) {
    _dictionary = dictionary;
  }

  public String getData() {
    return _data;
  }

  public void setData(String data) {
    _data = data;
  }

  public DateTime getCreationDate() {
    return _creationDate;
  }

  public void setCreationDate(DateTime value) {
    _creationDate = value;
  }

  public DateTime getModificationDate() {
    return _modificationDate;
  }

  public void setModificationDate(DateTime modificationDate) {
    _modificationDate = modificationDate;
  }
}
