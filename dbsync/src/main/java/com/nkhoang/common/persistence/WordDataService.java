package com.nkhoang.common.persistence;

import com.nkhoang.model.Word;
import com.nkhoang.model.criteria.IWordCriteria;

public interface WordDataService extends IDataService<Word, Long, IWordCriteria> {
   public static final String QUERY_FIND_COUNT = "IProduct.selectAll.count";
}
