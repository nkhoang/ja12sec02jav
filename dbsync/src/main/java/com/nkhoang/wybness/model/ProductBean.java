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
   /**
    * The bookingType name property
    */
   private String name;
   private IResourceType resourceType;
   private IBookingType bookingType;
   private IPricingPolicy pricingPolicy;

   private Long productKey;

   public ProductBean() {
   }


   @Column(name = NAME, length = 128, nullable = false, updatable = true)
   @Index(name = "IDX_PRODUCT_NAME")
   public String getName() {
      return name;
   }

   public void setName(String value) {
      name = value;
   }


   @ManyToOne(targetEntity = ResourceTypeBean.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = IResourceType.ID, referencedColumnName = IResourceType.ID, insertable = true, updatable = true, nullable = true)
   @ForeignKey(name = "FK_PRODUCT_RESOURCE_TYPE")
   public IResourceType getResourceType() {
      return resourceType;
   }

   @ManyToOne(targetEntity = BookingTypeBean.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = IBookingType.ID, referencedColumnName = IBookingType.ID, insertable = true, updatable = true, nullable = true)
   @ForeignKey(name = "FK_PRODUCT_BOOKING_TYPE")
   public IBookingType getBookingType() {
      return bookingType;
   }

   @ManyToOne(targetEntity = PricingPolicyBean.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = IPricingPolicy.ID, referencedColumnName = IPricingPolicy.ID, insertable = true, updatable = true, nullable = false)
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

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "productKey")
   public Long getKey() {
      return productKey;
   }

   public void setKey(Long productKey) {
      this.productKey = productKey;
   }
}