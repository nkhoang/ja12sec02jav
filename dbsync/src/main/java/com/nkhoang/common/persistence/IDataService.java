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

   /**
    * Remove entity.
    *
    * @param entity the entity to remove.
    * @throws PersistenceException the persistence exception.
    */
   void remove(final T entity) throws PersistenceException;

   /**
    * Insert entities.
    *
    * @param collection a list of entities to be inserted.
    * @return a list of inserted entities.
    * @throws PersistenceException the persistence exception.
    */
   List<T> insert(final List<T> collection) throws PersistenceException;

   /**
    * Execute Query.
    *
    * @param clazz
    * @param queryName
    * @param single
    * @param <T>
    * @return
    * @throws PersistenceException
    */
   <T> Object executeQuery(final Class<T> clazz, final String queryName, final boolean single)
         throws PersistenceException;
}
