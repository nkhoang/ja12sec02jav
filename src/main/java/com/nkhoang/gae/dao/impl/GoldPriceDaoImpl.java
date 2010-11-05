package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.GoldPriceDao;
import com.nkhoang.gae.model.GoldPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * GoldPrice dao implementation.
 * 
 * @author hnguyen93
 * 
 */
@Transactional
public class GoldPriceDaoImpl extends GeneralDaoImpl<GoldPrice, Long> implements GoldPriceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldPriceDaoImpl.class);

    // @off

    @Override
    public GoldPrice save(GoldPrice e) {
        return super.save(e);
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Get an gold price from DB.
     * @param id: Gold Price id.
     * @return an object 
     *         or
     *         null value.
     */
    // @on
    public GoldPrice get(Long id) {
        LOGGER.info("Get gold price [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.id=:goldPriceID");
            query.setParameter("goldPriceID", id);

            GoldPrice price = (GoldPrice) query.getSingleResult();
            if (price != null) {
                return price;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get Gold price from DB.");
            LOGGER.error("Error", e);
        }
        return null;
    }

    // @off
    /**
     * Get all gold prices from DB.
     * @return a list 
     *         or
     *         null value.
     */
    // @on
    public List<GoldPrice> getAll() {
        LOGGER.info("Get all gold price ...");
        List<GoldPrice> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName());

            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to load gold prices from DB.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    // @off
    /**
     * Delete an gold price from DB.
     * @param id: gold price id.
     */
    // @on
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        LOGGER.info("Delete gold price with [id: " + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + GoldPrice.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();

            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete gold price with [id:" + id + "]");
            LOGGER.error("Error", e);
        }
        return result;
    }
}
