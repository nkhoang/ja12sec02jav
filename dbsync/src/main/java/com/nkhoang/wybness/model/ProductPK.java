package com.nkhoang.wybness.model;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ProductPK implements Serializable {
  private IResourceType resourceType;
  private IBookingType bookingType;
  private IPricingPolicy pricingPolicy;

  @ManyToOne(targetEntity = ResourceTypeBean.class, fetch = FetchType.EAGER)
  @JoinColumn(name = ResourceTypeBean.ID, referencedColumnName = ResourceTypeBean.ID, nullable = true)
  @ForeignKey(name = "FK_PRODUCT_RESOURCE_TYPE")
  public IResourceType getResourceType() {
    return resourceType;
  }


  @ManyToOne(targetEntity = BookingTypeBean.class, fetch = FetchType.LAZY)
  @JoinColumn(name = IBookingType.ID, referencedColumnName = IBookingType.ID, nullable = false)
  @ForeignKey(name = "FK_PRODUCT_BOOKING_TYPE")
  public IBookingType getBookingType() {
    return bookingType;
  }

  @ManyToOne(targetEntity = PricingPolicyBean.class, fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = IPricingPolicy.ID, referencedColumnName = IPricingPolicy.ID, nullable = false)
  @ForeignKey(name = "FK_PRODUCT_PRICING_POLICY")
  public IPricingPolicy getPricingPolicy() {
    return pricingPolicy;
  }

  public void setResourceType(IResourceType resourceType) {
    this.resourceType = resourceType;
  }

  public void setBookingType(IBookingType bookingType) {
    this.bookingType = bookingType;
  }

  public void setPricingPolicy(IPricingPolicy pricingPolicy) {
    this.pricingPolicy = pricingPolicy;
  }
}
