package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.ISoundCriteria;

/**
 * @author hnguyen
 */
public class SoundCriteriaImpl extends SortCriteriaImpl implements ISoundCriteria {
   private Long key;

   public Long getKey() {
      return key;
   }

   public void setKey(Long key) {
      this.key = key;
   }
}
