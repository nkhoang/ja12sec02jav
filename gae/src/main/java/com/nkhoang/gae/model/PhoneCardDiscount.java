package com.nkhoang.gae.model;

import org.joda.time.DateTime;

import java.util.Map;

/**
 * Hold information about what kind of phone card, how many percentage do the seller have
 * and how many percentage do the buyer receive.
 */
public class PhoneCardDiscount {
  // for example: vina100
  private String type;
  // for example: 100,000.
  private Integer price;
  // hold the fetching date.
  private DateTime date;
  // the seller discount rate.
  private float sellerDiscountRate;
  // the buyer discount rate.
  private Map<String, Float> buyDiscountRates;
  public static final String[] SKIP_FIELDS_USER = {"sellerDiscountRate"};


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public float getSellerDiscountRate() {
    return sellerDiscountRate;
  }

  public void setSellerDiscountRate(float sellerDiscountRate) {
    this.sellerDiscountRate = sellerDiscountRate;
  }

  public Map<String, Float> getBuyDiscountRates() {
    return buyDiscountRates;
  }

  public void setBuyDiscountRates(Map<String, Float> buyDiscountRates) {
    this.buyDiscountRates = buyDiscountRates;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }
}
