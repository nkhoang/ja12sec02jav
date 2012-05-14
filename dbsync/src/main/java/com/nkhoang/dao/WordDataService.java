package com.nkhoang.dao;

import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.dictionary.Word;

public interface WordDataService extends IDataService<Word, Long, IWordCriteria> {
  public static final String QUERY_FIND_COUNT = "IProduct.selectAll.count";
}
