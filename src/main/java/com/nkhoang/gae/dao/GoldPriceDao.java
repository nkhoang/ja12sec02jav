package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.GoldPrice;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 5, 2010
 * Time: 12:25:49 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GoldPriceDao extends BaseDao<GoldPrice , Long>{
    boolean check(GoldPrice compareO);
    public List<GoldPrice> getGoldPriceWithRange(String currency, Long from, Long to);
    public List<GoldPrice> getAll(String currency);
    void clearAll();
}
