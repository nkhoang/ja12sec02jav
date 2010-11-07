package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.GoldPriceDao;
import com.nkhoang.gae.model.GoldPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * GoldPrice dao implementation.
 *
 * @author hnguyen93
 */
@Transactional
public class GoldPriceDaoImpl extends GeneralDaoImpl<GoldPrice, Long> implements GoldPriceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldPriceDaoImpl.class);

    @Override
    public GoldPrice save(GoldPrice e) {
        return super.save(e);
    }

    /**
     * Get Gold Price from a range for a specific currency.
     *
     * @param currency Currency can be: USD or VND
     * @param from     from Date in Long.
     * @param to       to Date in Long.
     * @return a found list.
     */
    public List<GoldPrice> getGoldPriceWithRange(String currency, Long from, Long to) {
        LOGGER.info("Get gold price from range from:" + from.toString() + " to:" + to.toString());
        List<GoldPrice> list = new ArrayList<GoldPrice>();
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.currency = :currency and t.time >= :fromDate and t.time <= :toDate ");
            query.setParameter("currency", currency);
            query.setParameter("fromDate", from);
            query.setParameter("toDate", to);

            list = query.getResultList();

            LOGGER.info("Found " + list.size());
        } catch (Exception ex) {
            LOGGER.error("Could not query gold price.", ex);
        }
        return list;
    }

    /**
     * Clear all data. Not suitable for GAE. Take too long to finish.
     */
    public void clearAll() {
        LOGGER.info("Deleting all gold price...");
        List<GoldPrice> list = getAll();
        for (int i = 0; i < list.size(); i++) {
            delete(list.get(i).getId());
        }
    }

    /**
     * Check gold price existence.
     *
     * @param o object to be compare in database.
     * @return true if object existed.
     */
    public boolean check(GoldPrice o) {
        LOGGER.info("Checking gold price from DB: " + o.toString());
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.currency=:currency and t.priceBuy=:priceBuy and t.priceSell=:priceSell ");

            query.setParameter("currency", o.getCurrency());
            query.setParameter("priceBuy", o.getPriceBuy());
            query.setParameter("priceSell", o.getPriceSell());

            GoldPrice price = null;

            price = (GoldPrice) query.getSingleResult();


            if (price != null) {
                LOGGER.info("Found");
                result = true;
            }
        } catch (Exception empty) {
            
        }
        return result;
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Get an gold price from DB.
     *
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
        }
        return null;
    }

    // @off

    /**
     * Get all gold prices from DB.
     *
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
        }
        return result;
    }

    // @off

    /**
     * Delete an gold price from DB.
     *
     * @param id: gold price id.
     */

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
        }
        return result;
    }
}
