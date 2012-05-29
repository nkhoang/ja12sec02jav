package com.nkhoang.wybness.model;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ProductPK implements Serializable {
  private Long bookingTypeKey;
  private Long resourceTypeKey;
  private Long pricingPolicyKey;

  @Column(name = IBookingType.ID)
  public Long getBookingTypeKey() {
    return bookingTypeKey;
  }

  public void setBookingTypeKey(Long bookingTypeKey) {
    this.bookingTypeKey = bookingTypeKey;
  }

  @Column(name = IResourceType.ID)
  public Long getResourceTypeKey() {
    return resourceTypeKey;
  }

  public void setResourceTypeKey(Long resourceTypeKey) {
    this.resourceTypeKey = resourceTypeKey;
  }

  @Column(name = IPricingPolicy.ID)
  public Long getPricingPolicyKey() {
    return pricingPolicyKey;
  }

  public void setPricingPolicyKey(Long pricingPolicyKey) {
    this.pricingPolicyKey = pricingPolicyKey;
  }
}
