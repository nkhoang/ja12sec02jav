package com.nkhoang.model.criteria;


/**
 * @author hnguyen
 */
public interface IWordCriteria extends IExtendedSearchCriteria {
   Long getKey();

   void setKey(Long key);

   void setWord(String word);

   String getWord();

   void setDictName(String dictName);

   String getDictName();
}
