package com.nkhoang.dao.impl;

import com.nkhoang.dao.IDataService;
import com.nkhoang.model.IDataObject;
import com.nkhoang.model.IPersistentData;
import com.nkhoang.model.criteria.ISearchCriteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractDataService<T extends IDataObject<K>, K extends Serializable, C extends ISearchCriteria>
      implements IDataService<T, K, C> {

   @PersistenceContext
   protected EntityManager entityManager;

   /**
    * Get Hibernate session.
    *
    * @return the hibernate session.
    */
   protected Session getPersistenceSession() {
      Session session = (Session) entityManager.getDelegate();
      return session;
   }

   private static Logger LOGGER = LoggerFactory.getLogger(AbstractDataService.class.getCanonicalName());

   /**
    * @see IDataService#insert(com.nkhoang.model.IDataObject)
    */
   public T insert(final T entity) throws PersistenceException {
      final long start = System.currentTimeMillis();
      T result = null;

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Inserting entity: " + entity);
      }

      result = doInsertEntity(entity);

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("done in (ms): "
               + (System.currentTimeMillis() - start));
      }

      return result;
   }

   /**
    * @see IDataService#remove(com.nkhoang.model.IDataObject)
    */
   public void remove(final T entity) throws PersistenceException {
      final long start = System.currentTimeMillis();

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Removing entity: " + entity);
      }

      entityManager.remove(entity);

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("done in (ms): "
               + (System.currentTimeMillis() - start));
      }
   }

   /**
    * Insert a list of entities.
    *
    * @param entities a list of entities.
    * @return a list of inserted entities.
    * @throws PersistenceException the persistence exception.
    */
   public List<T> doInsertEntities(final Collection<T> entities) throws PersistenceException {
      final List<T> inserted = new ArrayList<T>(entities.size());

      for (final T entity : entities) {
         try {
            entityManager.persist(entity);
            inserted.add(entity);
         } catch (final IllegalStateException e) {
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(
                     "Trying to update entity instead of inserting it",
                     e);
            }

            inserted.add(entityManager.merge(entity));
         } catch (final Throwable e) {
            LOGGER.error("Could not insert entities.", e);
         }
      }
      return inserted;
   }

   /**
    * @see IDataService#insert(java.util.List)
    */
   public List<T> insert(List<T> entities) throws PersistenceException {
      return doInsertEntities(entities);
   }


   /**
    * @see IDataService#executeQuery(Class, String, boolean)
    */
   public final <T> Object executeQuery(final Class<T> clazz,
                                        final String queryName,
                                        final boolean single) throws PersistenceException {
      final long start = System.currentTimeMillis();
      Object result = null;

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Executing using query: " + queryName + ", class: " + clazz);
      }

      if (queryName == null) {
         throw new IllegalArgumentException("Query name is null");
      }

      flush();

      result = doExecuteQuery(clazz, queryName, single);

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("done in (ms): "
               + (System.currentTimeMillis() - start));
      }

      return result;
   }

   /**
    * Actually execute query.
    *
    * @param clazz
    * @param queryName
    * @param single
    * @param <T>
    * @return
    * @throws PersistenceException
    */
   protected <T> Object doExecuteQuery(final Class<T> clazz, final String queryName, final boolean single) throws PersistenceException {
      Object result = null;

      try {
         final Query query = entityManager.createNamedQuery(queryName);


         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query.toString());
         }

         if (single) {
            result = query.getSingleResult();
         } else {
            result = query.getResultList();
         }
      } catch (final NoResultException e) {
      } catch (final Throwable e) {
         LOGGER.error("Could not query using named query: " + queryName, e);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Query result: " + result);
      }

      return result;
   }

   /**
    * Helper method to insert a new entity.
    *
    * @param entity the entity to insert.
    * @param <T>    the entity type.
    * @return the saved entity
    * @throws PersistenceException the persistence exception.
    */
   protected <T extends IPersistentData<K>> T doInsertEntity(final T entity) throws PersistenceException {
      T result = null;

      try {
         entityManager.persist(entity);
         result = entity;
      } catch (final IllegalStateException e) {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to update entity instead of inserting it", e);
         }

         result = entityManager.merge(entity);
         entityManager.flush();
      } catch (final Throwable e) {
         LOGGER.error("Could not insert entity.", e);

         result = null;
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Query result: " + result);
      }

      return result;
   }

   /**
    * Flush Entity Manager.
    *
    * @throws PersistenceException the persistence exception.
    */
   public void flush() throws PersistenceException {
      if (entityManager == null || !entityManager.isOpen()) {
         LOGGER.warn("Flushing skipped, session is invalid");
      } else {
         try {
            entityManager.flush();
         } catch (final Throwable e) {
            throw new PersistenceException(
                  "An error occurs while flushing session", e);
         }
      }
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}
