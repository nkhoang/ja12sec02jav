package com.nkhoang.model;

import com.nkhoang.common.persistence.IDataService;
import com.nkhoang.common.persistence.impl.AbstractDataService;
import com.nkhoang.model.criteria.ISearchCriteria;
import org.hibernate.Session;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractBeanTest<T extends IDataObject> {

   /**
    * Creates the.
    *
    * @param count the count
    * @return the collection
    * @throws Exception the exception
    */
   public final List<T> create(final int count) throws Exception {
      return doCreate(count);
   }


   /**
    * Actually do the insert.
    *
    * @param items
    * @return
    */
   protected abstract List<T> doInsert(List<T> items);

   /**
    * Populate.
    *
    * @param count the count
    * @return the collection
    * @throws Exception the exception
    */
   public List<T> populate(final int count) throws Exception {
      final long initialCount = count();
      final List<T> items = create(count);
      final List<T> inserted = doInsert(items);

      Assert.assertEquals("Not all the items were inserted", count, count() - initialCount);

      return inserted;
   }


   protected abstract long doCount();

   /**
    * Returns the number of rows.
    *
    * @return the number of rows
    * @throws javax.persistence.PersistenceException
    *          the persistence exception
    */
   protected long count() throws PersistenceException {
      return doCount();
   }


   /**
    * Creates the.
    *
    * @param count the count
    * @return the collection
    * @throws Exception the exception
    */
   protected abstract List<T> doCreate(final int count) throws Exception;

   /**
    * Gets the entity class.
    *
    * @return the entity class
    */
   public abstract Class<? extends T> getEntityClass();

   /**
    * Gets the entity name.
    *
    * @return the entity name.
    */
   public abstract String getEntityName();

}
