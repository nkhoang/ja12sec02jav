package com.nkhoang.common.persistence;

import com.nkhoang.model.IDataObject;
import com.nkhoang.model.criteria.ISearchCriteria;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

public interface IDataService<L extends IDataObject<K>, T extends L, K extends Serializable, C extends ISearchCriteria> {
   /**
    * Find.
    * @param entities the entities
    * @param criteria the criteria
    * @return the long
    * @throws javax.persistence.PersistenceException the persistence exception
    */
   long find(final List<L> entities, final C criteria)
         throws PersistenceException;
}
