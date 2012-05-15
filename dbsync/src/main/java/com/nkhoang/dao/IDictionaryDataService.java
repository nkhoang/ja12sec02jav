package com.nkhoang.dao;

import com.nkhoang.model.criteria.IDictionaryCriteria;
import com.nkhoang.model.dictionary.Dictionary;
import com.nkhoang.model.dictionary.IDictionary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;

@Repository
@Qualifier("dictionaryDataService")
public interface IDictionaryDataService extends IDataService<IDictionary, Long, IDictionaryCriteria> {
   public static final String QUERY_FIND_COUNT = "IDictionary.selectAll.count";
   public static final String QUERY_FIND_BY_NAME = "IDictionary.findByName";

   /**
    * Find Dictionary by name.
    *
    * @param dictName
    * @return
    * @throws PersistenceException
    */
   Dictionary findByName(String dictName) throws PersistenceException;
}
