package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * General Dao will be extends by any kind of Dao.
 *
 * @author hoangnk
 */
@Repository
@Transactional
public abstract class GeneralDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralDaoImpl.class);

    @PersistenceContext
    protected EntityManager entityManager;

    // @off

    /**
     * Save an object to DB.
     *
     * @return an saved obj if success
     *         else
     *         null value.
     */
    // @on
    public T save(T e) {
        T result = null;
        entityManager.persist(e);
        entityManager.flush();
        result = e;
        return result;
    }

    // @off

    /**
     * Update an object to DB.
     *
     * @return an saved obj if success
     *         else
     *         null value.
     */
    // @on
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
