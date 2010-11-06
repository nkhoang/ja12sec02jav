package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.CurrencyDao;
import com.nkhoang.gae.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Currency dao implementation.
 *
 * @author hnguyen93
 */
@Transactional
public class CurrencyDaoImpl extends GeneralDaoImpl<Currency, Long> implements CurrencyDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDaoImpl.class);

    @Override
    public Currency save(Currency e) {
        return super.save(e);
    }

    public boolean check(Currency c) {
        boolean result = false;

        Query query = entityManager.createQuery("Select from " + Currency.class.getName() + " t where t.currency=:currency and t.priceBuy=:priceBuy and t.priceSell=:priceSell");
        query.setParameter("currency", c.getCurrency());
        query.setParameter("priceBuy", c.getPriceBuy());
        query.setParameter("priceSell", c.getPriceSell());
        Currency currency = null;
        try {
            currency = (Currency) query.getSingleResult();
        } catch (Exception empty) {

        }

        if (currency != null) {
            result = true;
        }
        return result;

    }


    @Override
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Get an exchange rate from DB.
     *
     * @param id: Currency id.
     * @return an object
     *         or
     *         null value.
     */
    public Currency get(Long id) {
        LOGGER.info("Get exchange rate [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + Currency.class.getName() + " t where t.id=:currencyID");
            query.setParameter("currencyID", id);

            Currency unit = (Currency) query.getSingleResult();
            if (unit != null) {
                return unit;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get Exchange rate from DB.");
            LOGGER.error("Error", e);
        }
        return null;
    }

    /**
     * Get the latest currency.
     *
     * @param currency currency: USD, EUR
     * @return Currency object.
     */
    public Currency getExchangeRate(String currency) {
        LOGGER.info("Get exchange rate [currency: " + currency + "].");
        try {
            Query query = entityManager.createQuery("Select from " + Currency.class.getName() + " t where t.currency=:currencyID order by t.time DESC");
            query.setParameter("currencyID", currency);
            Currency unit = null;

            List<Currency> list =  query.getResultList();
            if (list != null && list.size() > 0) {
                unit = list.get(0);
            }
            if (unit != null) {
                LOGGER.debug("Found: " + unit.toString());
                return unit;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get Exchange rate from DB.");
            LOGGER.error("Error", e);
        }
        return null;
    }


    // @off

    /**
     * Get all exchange rates from DB.
     *
     * @return a list
     *         or
     *         null value.
     */
    // @on
    public List<Currency> getAll() {
        LOGGER.info("Get all exchange rates ...");
        List<Currency> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Currency.class.getName());

            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to load exchange rates from DB.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    // @off

    /**
     * Delete an exchange rate from DB.
     *
     * @param id: exchange rate id.
     */
    // @on
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        LOGGER.info("Delete exchange rate with [id: " + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + Currency.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();

            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to exchange rate with [id:" + id + "]");
            LOGGER.error("Error", e);
        }
        return result;
    }
}
