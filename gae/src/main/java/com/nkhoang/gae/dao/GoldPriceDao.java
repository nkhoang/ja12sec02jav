package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.GoldPrice;

import java.util.List;

public interface GoldPriceDao extends BaseDao<GoldPrice, Long> {
    boolean check(GoldPrice compareO);

    public List<GoldPrice> getGoldPriceWithRange(String currency, Long from, Long to);

    public List<GoldPrice> getAll(String currency);

    void clearAll();
}
