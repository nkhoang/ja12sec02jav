package com.nkhoang.model;

import com.nkhoang.common.persistence.PricingPolicyDataService;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "IProduct")
@Table(name = "PRODUCT")
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@DynamicInsert(value = true)
public class ProductBean implements IProduct {
   public static final String NAME = "name";

   private IResourceType resourceType;
   private IBookingType bookingType;
   private IPricingPolicy pricingPolicy;

   /**
    * The bookingType name property
    */
   private String name;

   @Column(name = NAME, length = 128, nullable = false, updatable = true)
   @Index(name = "IDX_PRODUCT_NAME")
   public String getName() {
      return name;
   }

   public void setName(String value) {
      name = value;
   }

   @ManyToOne(targetEntity = ResourceTypeBean.class, fetch = FetchType.LAZY)
   @JoinColumn(name = ResourceTypeBean.ID, referencedColumnName = ResourceTypeBean.ID, nullable = true)
   @ForeignKey(name = "FK_PRODUCT_RESOURCETYPE")
   @Id
   public IResourceType getResourceType() {
      return resourceType;
   }


   @ManyToOne(targetEntity = BookingTypeBean.class, fetch = FetchType.LAZY)
   @JoinColumn(name = IBookingType.ID, referencedColumnName = IBookingType.ID, nullable = true)
   @ForeignKey(name = "FK_PRODUCT_BOOKINGTYPE")
   @Id
   public IBookingType getBookingType() {
      return bookingType;
   }

   @ManyToOne(targetEntity = PricingPolicyBean.class, fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = IPricingPolicy.ID, referencedColumnName = IPricingPolicy.ID, nullable = false)
   @ForeignKey(name = "FK_PRODUCT_PRICINGPOLICY")
   @Id
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

   @Transient
   public Long getKey() {
      return null;
   }

   public void setKey(Long key) {

   }
}