package com.nkhoang.common.persistence.impl;

import com.nkhoang.common.persistence.IDataService;
import com.nkhoang.model.IDataObject;
import com.nkhoang.model.IPersistentData;
import com.nkhoang.model.criteria.ISearchCriteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.io.Serializable;

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
   * Insert a new entity.
   *
   * @param entity the entity to insert.
   * @return the saved entity.
   * @throws PersistenceException the persistence exception.
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
    } catch (final Exception e) {
      LOGGER.error("Error", e);
      // TODO: do something to handle exception here.
      result = null;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Query result: " + result);
    }

    return result;
  }


  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
}
