package com.nkhoang.model;

import java.util.ArrayList;
import java.util.List;

public class Phrase {
  private Long key;
  private String description;
  private List<Sense> senseList = new ArrayList<Sense>(0);

  public String getDescription() {
    return description;
  }


  public List<Sense> getSenseList() {
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

  public void setSenseList(List<Sense> senseList) {
    this.senseList = senseList;
  }

}
