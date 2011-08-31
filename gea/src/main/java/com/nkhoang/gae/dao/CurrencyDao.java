package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Currency;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 5, 2010
 * Time: 12:29:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CurrencyDao extends BaseDao<Currency, Long>{
    Currency getExchangeRate(String currency);
    boolean check(Currency c);
    void clearAll();
}
