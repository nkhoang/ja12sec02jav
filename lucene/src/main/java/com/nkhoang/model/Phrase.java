package com.nkhoang.model;

import java.util.ArrayList;
import java.util.List;

public class Phrase {
   private String id;
   private String description;
   private List<Sense> senseList = new ArrayList<Sense>(0);


   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<Sense> getSenseList() {
      return senseList;
   }

   public void setSenseList(List<Sense> senseList) {
      this.senseList = senseList;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
