package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * The implementation of {@link BaseDao}.
 *
 * @author hoangnk
 */
@Repository
@Transactional
public abstract class BaseDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDaoImpl.class);
    @PersistenceContext
    protected EntityManager entityManager;

    // Extender must implement this method to provide the class name.
    public abstract String getClassName();

    public boolean delete(Long id) {
        LOGGER.info("Delete " + getClassName() + " with [id:" + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + getClassName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete " + getClassName() + " with [id:" + id + "].");
            LOGGER.error("Error", e);
        }
        return result;
    }


    public T get(Long id) {
        try {
            Query query = entityManager.createQuery("Select from " + getClassName() + " t where t.id=:valueId");
            query.setParameter("valueId", id);

            T o = (T) query.getSingleResult();
            if (o != null) {
                return o;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get " + getClassName() + " with [id:" + id + "].");
            LOGGER.error("Error", e);
        }
        return null;
    }

    public List<T> getAll() {
        LOGGER.info("Get all " + getClassName() + "s ...");
        List<T> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + getClassName());
            result = query.getResultList();

        } catch (Exception ex) {
            LOGGER.info("Failed to get all " + getClassName() + "s ...");
            LOGGER.error("Error", ex);
        }
        return result;
    }


    /**
     * Save an object to DB.
     *
     * @return an saved obj if success
     *         else
     *         null value.
     */
    public T save(T e) {
        T result = null;
        entityManager.persist(e);
        entityManager.flush();
        result = e;
        return result;
    }

    /**
     * Update an object to DB.
     *
     * @return an saved obj if success
     *         else
     *         null value.
     */
    public T update(T e) {
        T result = null;
        try {
            entityManager.merge(e);
            entityManager.flush();
            result = e;
        } catch (Exception ex) {
            LOGGER.info("Failed to update to DB.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
