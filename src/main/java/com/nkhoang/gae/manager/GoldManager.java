package com.nkhoang.gae.manager;

import com.nkhoang.gae.dao.GoldPriceDao;
import com.nkhoang.gae.model.GoldPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Gold price manager.
 */
public class GoldManager implements BaseManager<GoldPrice, Long>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemManager.class);

    @Autowired
    private GoldPriceDao goldPriceDao;

    public GoldPrice save(GoldPrice o) {
        return goldPriceDao.save(o);
    }

    public GoldPrice update(GoldPrice o) {
        return goldPriceDao.update(o);
    }

    public List<GoldPrice> listAll() {
        return goldPriceDao.getAll();
    }

    public boolean clearAll() {
        LOGGER.info("Clear all gold price ...");
        boolean result = false;
        List<GoldPrice> items = listAll();
        for (GoldPrice item : items) {
            goldPriceDao.delete(item.getId());
        }
        return true;
    }

    public GoldPriceDao getGoldPriceDao() {
        return goldPriceDao;
    }

    public void setGoldPriceDao(GoldPriceDao goldPriceDao) {
        this.goldPriceDao = goldPriceDao;
    }
}
