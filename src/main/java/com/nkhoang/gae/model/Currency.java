package com.nkhoang.gae.model;

import com.nkhoang.gae.utils.DateConverter;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: hnguyen93
 * Date: Nov 4, 2010
 * Time: 12:21:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Currency {
    private Date time;
    private String currency;
    private Float priceBuy;
    private Float priceSell;


    public Float getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(Float priceBuy) {
        this.priceBuy = priceBuy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String toString() {
        return "Currency [" + currency + "] ==> time: " + DateConverter.parseDate(time, DateConverter.defaultCurrencyDateFormat) + " buy: " + priceBuy + " sell: " + priceSell;
    }

    public Float getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Float priceSell) {
        this.priceSell = priceSell;
    }
}
