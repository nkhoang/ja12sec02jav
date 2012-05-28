package com.nkhoang.wybness.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity(name = "IProduct")
@Table(name = "PRODUCT", uniqueConstraints = @UniqueConstraint(name = "UC_PRODUCT", columnNames = {
      IPricingPolicy.ID, IBookingType.ID, IResourceType.ID
}))
@IdClass(ProductPK.class)
public class ProductBean implements IProduct {
   public static final String NAME = "name";




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



   @Transient
   public Long getKey() {
      return null;
   }

   public void setKey(Long key) {

   }
}