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
        LOGGER.info("Get gold price [" + currency + "] from range from:" + from.toString() + " to:" + to.toString());
        List<GoldPrice> list = new ArrayList<GoldPrice>();
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.currency = :currency and t.time >= :fromDate and t.time <= :toDate ");
            query.setParameter("currency", currency);
            query.setParameter("fromDate", from);
            query.setParameter("toDate", to);

            list = query.getResultList();

            LOGGER.info("===============> Found " + list.size());
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
        try {
            List<GoldPrice> list = getAll();
            for (int i = 0; i < list.size(); i++) {
                delete(list.get(i).getId());
            }
        } catch (Exception e) {
            LOGGER.error("Could not delete all gold price.", e);
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
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.currency=:currency and t.time=:priceTime order by t.time DESC");

            query.setParameter("currency", o.getCurrency());
            query.setParameter("priceTime", o.getTime());
            query.setFirstResult(0);
            query.setMaxResults(1);

            GoldPrice price = null;

            price = (GoldPrice) query.getSingleResult();

            if (price != null) {
                LOGGER.info("===================> Found.");
                // then compare with the one we would like to insert.
                if (o.getPriceBuy() != 0 && o.getPriceSell() != 0) {
                    if (o.getPriceBuy() != price.getPriceBuy() || price.getPriceSell() != o.getPriceSell()) {
                        result = true;
                    }
                }
            }
        } catch (Exception empty) {
            // error or something.
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
    public GoldPrice get(Long id) {
        LOGGER.info("Get gold price [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " t where t.id=:goldPriceID");
            query.setParameter("goldPriceID", id);

            GoldPrice price = (GoldPrice) query.getSingleResult();
            if (price != null) {
                LOGGER.info("===============> Found");
                return price;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get Gold price from DB.", e);
        }
        return null;
    }

    /**
     * Get all gold prices from DB.
     *
     * @return a list
     *         or
     *         null value.
     */
    public List<GoldPrice> getAll() {
        LOGGER.info("Get all gold price ...");
        List<GoldPrice> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName());

            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.error("Failed to load gold prices from DB.", ex);
        }
        return result;
    }

    /**
     * Get all gold price of a currency type.
     * @param currency currency to be retrieved.
     * @return a list.
     */
    public List<GoldPrice> getAll(String currency) {
        LOGGER.info("Get all gold price with currency=[" + currency + "] ...");
        List<GoldPrice> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + GoldPrice.class.getName() + " where currency=:currency");
            query.setParameter("currency", currency);

            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.error("Failed to load gold prices from DB.", ex);
        }
        return result;
    }

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
            LOGGER.error("Failed to delete gold price with [id:" + id + "]", e);
        }
        return result;
    }
}
