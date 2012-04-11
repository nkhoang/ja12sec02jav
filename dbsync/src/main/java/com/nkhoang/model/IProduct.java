package com.nkhoang.model;


/**
 * The Interface IProduct.
 */
public interface IProduct extends IDataObject<Long>, INamed {
   public static final String NAME = "name";

   IPricingPolicy getPricingPolicy();

   void setPricingPolicy(IPricingPolicy value);

   IResourceType getResourceType();

   void setResourceType(IResourceType value);

   IBookingType getBookingType();

   void setBookingType(IBookingType value);
}
