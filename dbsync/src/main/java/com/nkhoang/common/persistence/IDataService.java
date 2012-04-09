package com.nkhoang.common.persistence;

import com.nkhoang.model.IDataObject;
import com.nkhoang.model.criteria.ISearchCriteria;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

public interface IDataService<T extends IDataObject<K>, K extends Serializable, C extends ISearchCriteria> {
  /**
   * Find.
   *
   * @return a list of T.
   * @throws javax.persistence.PersistenceException
   *          the persistence exception
   */
  List<T> find(final C criteria)
      throws PersistenceException;

  /**
   * Insert an value.
   *
   * @param value value to insert.
   * @throws PersistenceException the persistence exception.
   */
  T insert(T value) throws PersistenceException;
}
