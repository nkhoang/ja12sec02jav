package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.MeaningDao;
import com.nkhoang.gae.model.Meaning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.List;

public class MeaningDaoImpl extends GeneralDaoImpl<Meaning, Long> implements MeaningDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeaningDaoImpl.class);

    /**
     * Delete Meaning with id.
     *
     * @return true
     *         or
     *         false.
     */
    public boolean delete(Long id) {
        LOGGER.info("Delete meaning with [id:" + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + Meaning.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete meaning with [id:" + id + "].");
            LOGGER.error("Error", e);
        }
        return result;
    }

    /**
     * Get meaing with id.
     *
     * @return an obj
     *         or
     *         null.
     */
    public Meaning get(Long id) {
        // LOGGER.info("Get meaing [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + Meaning.class.getName()
                    + " t where t.id=:meaningID");
            query.setParameter("meaningID", id);

            Meaning meaning = (Meaning) query.getSingleResult();
            if (meaning != null) {
                return meaning;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to Get meaing [id: " + id + "].");
            LOGGER.error("Error", e);
        }
        return null;
    }

    // @off

    /**
     * Get all meanings.
     *
     * @return a list
     *         or
     *         null.
     */
    // @on
    public List<Meaning> getAll() {
        LOGGER.info("Get all meanings ...");
        List<Meaning> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Meaning.class.getName());
            result = query.getResultList();

        } catch (Exception ex) {
            LOGGER.info("Failed to get all meanings ...");
            LOGGER.error("Error", ex);
        }
        return result;
    }
}
