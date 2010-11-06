package com.nkhoang.gae.model;

import com.nkhoang.gae.utils.DateConverter;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)    
    private Long id;
    @Basic
    private Date time;
    @Basic
    private String currency;
    @Basic
    private Float priceBuy;
    @Basic
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
