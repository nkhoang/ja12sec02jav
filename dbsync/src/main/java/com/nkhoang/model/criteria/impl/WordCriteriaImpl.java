package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IWordCriteria;

public class WordCriteriaImpl implements IWordCriteria {

   private Long key;

   public Long getKey() {
      return key;
   }

   public void setKey(Long key) {
      this.key = key;
   }

   public Integer getPageSize() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public Long getPageStart() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setPageSize(Integer value) {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setPageStart(Long value) {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
