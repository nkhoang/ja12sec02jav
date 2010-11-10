package com.nkhoang.gae.model;

import com.nkhoang.gae.utils.DateConverter;

import javax.persistence.*;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: hnguyen93
 * Date: Nov 4, 2010
 * Time: 11:23:42 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class GoldPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private Long time;
    @Basic
    private String currency;
    @Basic
    private Float priceBuy = 0f;
    @Basic
    private Float priceSell = 0f;

    public static final String SKIP_FIELDS[] = {
        "jdoDetachedState" 
    };

    public GoldPrice() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }   

    public String toString() {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Bangkok"));
        calendar.setTimeInMillis(time);

        return "Gold ==> Time: " + DateConverter.parseDate(calendar.getTime(), DateConverter.defaultGoldDateFormat) + " buy: " + priceBuy + " sell: " + priceSell;
    }

    public void setPriceBuy(Float priceBuy) {
        this.priceBuy = priceBuy;
    }

    public void setPriceSell(Float priceSell) {
        this.priceSell = priceSell;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPriceBuy() {
        return priceBuy;
    }

    public Float getPriceSell() {
        return priceSell;
    }
}
