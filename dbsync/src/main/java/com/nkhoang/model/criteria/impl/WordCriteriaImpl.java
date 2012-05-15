package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IWordCriteria;

/**
 * @author hnguyen
 */
public class WordCriteriaImpl extends SortCriteriaImpl implements IWordCriteria {
   private Long key;
   private String word;
   private String dictName;

   public Long getKey() {
      return key;
   }

   public void setKey(Long key) {
      this.key = key;
   }

   public void setWord(String word) {
      this.word = word;
   }

   public String getWord() {
      return word;
   }

   public void setDictName(String dictName) {
      this.dictName = dictName;
   }

   public String getDictName() {
      return dictName;
   }
}
