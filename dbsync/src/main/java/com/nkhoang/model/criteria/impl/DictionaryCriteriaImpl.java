package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IDictionaryCriteria;

/**
 * @author hnguyen
 */
public class DictionaryCriteriaImpl extends SortCriteriaImpl implements IDictionaryCriteria {
   private Long key;

   public Long getKey() {
      return key;
   }

   public void setKey(Long key) {
      this.key = key;
   }
}
