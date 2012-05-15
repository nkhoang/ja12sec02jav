package com.nkhoang.model;

import java.util.Map;

public class WordJson extends AbstractWord {
  private Map<String, WordEntity> data;

  public Map<String, WordEntity> getData() {
    return data;
  }

  public void setData(Map<String, WordEntity> data) {
    this.data = data;
  }
}
