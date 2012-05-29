package com.nkhoang.wybness.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity(name = "IProduct")
@Table(name = "PRODUCT", uniqueConstraints = @UniqueConstraint(name = "UC_PRODUCT", columnNames = {
    IPricingPolicy.ID, IBookingType.ID, IResourceType.ID
}))

public class ProductBean implements IProduct {
  public static final String NAME = "name";
  @EmbeddedId
  public ProductPK key;
  /**
   * The bookingType name property
   */
  private String name;
  private IResourceType resourceType;
  private IBookingType bookingType;
  private IPricingPolicy pricingPolicy;

  public ProductBean() {
    key = new ProductPK();
  }


  @Column(name = NAME, length = 128, nullable = false, updatable = true)
  @Index(name = "IDX_PRODUCT_NAME")
  public String getName() {
    return name;
  }

  public void setName(String value) {
    name = value;
  }


  @EmbeddedId
  public ProductPK getKey() {
    return key;
  }

  public void setKey(ProductPK pk) {
    this.key = pk;
  }

  @MapsId("resourceTypeKey")
  @ManyToOne(targetEntity = ResourceTypeBean.class, fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = IResourceType.ID, referencedColumnName = IResourceType.ID, insertable = true, updatable = true)
  public IResourceType getResourceType() {
    return resourceType;
  }

  @MapsId("bookingTypeKey")
  @ManyToOne(targetEntity = BookingTypeBean.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
  @JoinColumn(name = IBookingType.ID, referencedColumnName = IBookingType.ID, insertable = true, updatable = true)
  public IBookingType getBookingType() {
    return bookingType;
  }

  @MapsId("pricingPolicyKey")
  @ManyToOne(targetEntity = PricingPolicyBean.class, fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = IPricingPolicy.ID, referencedColumnName = IPricingPolicy.ID, insertable = true, updatable = true)
  public IPricingPolicy getPricingPolicy() {
    return pricingPolicy;
  }

  public void setResourceType(IResourceType resourceType) {
    this.resourceType = resourceType;
  }

  public void setBookingType(IBookingType bookingType) {
    this.key.setBookingTypeKey(bookingType.getKey());
  }

  public void setPricingPolicy(IPricingPolicy pricingPolicy) {
    this.key.setPricingPolicyKey(pricingPolicy.getKey());
  }

}