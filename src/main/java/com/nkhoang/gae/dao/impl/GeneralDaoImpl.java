package com.nkhoang.gae.dao.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nkhoang.gae.dao.BaseDao;

/**
 * General Dao will be extends by any kind of Dao.
 * 
 * @author hoangnk
 * 
 */
@Repository
@Transactional
public abstract class GeneralDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {
    private static final Logger LOGGER = Logger.getLogger(GeneralDaoImpl.class);

    @PersistenceContext
    protected EntityManager entityManager;

    // @off
    /**
     * Save an object to DB.
     * @return an saved obj if success 
     *         else
     *         null value.
     */
    // @on
    public T save(T e) {
        T result = null;
        try {
            entityManager.persist(e);
            entityManager.flush();
            result = e;
        } catch (Exception ex) {
            LOGGER.info("Failed to save object to DB");
            LOGGER.error(ex);
        }
        return result;
    }

    // @off
    /**
     * Update an object to DB.
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
            LOGGER.error(ex);
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
