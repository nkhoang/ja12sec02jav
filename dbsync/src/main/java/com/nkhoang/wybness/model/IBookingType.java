package com.nkhoang.wybness.model;


import com.nkhoang.model.IDataObject;
import com.nkhoang.model.INamed;

import java.util.List;

/**
 * The Interface IBookingType.
 */
public interface IBookingType extends IDataObject<Long>, INamed {
  public static final String ID = "bookingTypeKey";
  public static final String NAME = "name";

  List<IProduct> getProducts();

  void setProducts(List<IProduct> values);
}
