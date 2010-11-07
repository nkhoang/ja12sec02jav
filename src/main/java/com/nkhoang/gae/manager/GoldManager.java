package com.nkhoang.gae.manager;

import com.nkhoang.gae.dao.CurrencyDao;
import com.nkhoang.gae.dao.GoldPriceDao;
import com.nkhoang.gae.model.Currency;
import com.nkhoang.gae.model.GoldPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Gold price manager.
 */
public class GoldManager implements BaseManager<GoldPrice, Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemManager.class);

    @Autowired
    private GoldPriceDao goldPriceDao;
    @Autowired
    private CurrencyDao currencyDao;

    public GoldPrice save(GoldPrice o) {
        return goldPriceDao.save(o);
    }

    public GoldPrice update(GoldPrice o) {
        return goldPriceDao.update(o);
    }

    public List<GoldPrice> listAll() {
        return goldPriceDao.getAll();
    }

    public Currency getExchangeRate(String c) {
        return currencyDao.getExchangeRate(c);
    }

    public Currency save(Currency c) {
        return currencyDao.save(c);
    }

    public boolean check(Currency c) {
        return currencyDao.check(c);
    }

    public boolean check(GoldPrice o) {
        return goldPriceDao.check(o);
    }

    public boolean clearAll() {
        goldPriceDao.clearAll();
        currencyDao.clearAll();

        return true;
    }

    public List<GoldPrice> getGoldPriceWithRange(String currency, Date from, Date to) {
        return goldPriceDao.getGoldPriceWithRange(currency, from.getTime(), to.getTime());
    }

    public List<GoldPrice> getAllGoldPrice() {
        return goldPriceDao.getAll();
    }


    public GoldPriceDao getGoldPriceDao() {
        return goldPriceDao;
    }

    public void setGoldPriceDao(GoldPriceDao goldPriceDao) {
        this.goldPriceDao = goldPriceDao;
    }

    public CurrencyDao getCurrencyDao() {
        return currencyDao;
    }

    public void setCurrencyDao(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }
}
