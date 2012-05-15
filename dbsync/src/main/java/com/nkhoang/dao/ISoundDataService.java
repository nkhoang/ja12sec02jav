package com.nkhoang.dao;

import com.nkhoang.model.criteria.ISoundCriteria;
import com.nkhoang.model.dictionary.ISound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("soundDataService")
public interface ISoundDataService extends IDataService<ISound, Long, ISoundCriteria> {
   public static final String QUERY_FIND_COUNT = "IProduct.selectAll.count";
}
