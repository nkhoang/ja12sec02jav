package com.nkhoang.dao;

import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.dictionary.IWord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("wordDataService")
public interface IWordDataService extends IDataService<IWord, Long, IWordCriteria> {
   public static final String QUERY_FIND_COUNT = "IProduct.selectAll.count";
}
