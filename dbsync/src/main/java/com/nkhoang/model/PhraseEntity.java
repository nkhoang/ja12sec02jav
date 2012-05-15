package com.nkhoang.model;

import java.util.ArrayList;
import java.util.List;

public class PhraseEntity {
  private Long key;
  private String description;
  private List<SenseEntity> senseList = new ArrayList<SenseEntity>(0);

  public String getDescription() {
    return description;
  }


  public List<SenseEntity> getSenseList() {
    return senseList;
  }

  public Long getKey() {
    return key;
  }

  public void setKey(Long value) {
    key = value;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setSenseList(List<SenseEntity> senseList) {
    this.senseList = senseList;
  }

}
