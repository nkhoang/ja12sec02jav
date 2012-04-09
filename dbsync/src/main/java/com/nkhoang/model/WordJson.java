package com.nkhoang.model;

import java.util.Map;

public class WordJson extends AbstractWord {
   private Map<String, Word> data;

   public Map<String, Word> getData() {
      return data;
   }

   public void setData(Map<String, Word> data) {
      this.data = data;
   }
}
