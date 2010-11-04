package com.nkhoang.gae.model;

import com.nkhoang.gae.utils.DateConverter;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: hnguyen93
 * Date: Nov 4, 2010
 * Time: 11:23:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoldPrice {
    private Date time;
    private String currency;
    private Float priceBuy;
    private Float priceSell;

    public GoldPrice() {
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }   

    public String toString() {
        return "Gold ==> Time: " + DateConverter.parseDate(time, DateConverter.defaultGoldDateFormat) + " buy: " + priceBuy + " sell: " + priceSell;
    }

    public void setPriceBuy(Float priceBuy) {
        this.priceBuy = priceBuy;
    }

    public void setPriceSell(Float priceSell) {
        this.priceSell = priceSell;
    }
}
