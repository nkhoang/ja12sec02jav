package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IWordCriteria;

/**
 * @author hnguyen
 */
public class DictionaryCriteriaImpl extends SortCriteriaImpl implements IWordCriteria {
   private Long key;

   public Long getKey() {
      return key;
   }

   public void setKey(Long key) {
      this.key = key;
   }
}
