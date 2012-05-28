package com.nkhoang.wybness.model;


import com.nkhoang.model.IDataObject;
import com.nkhoang.model.INamed;

/**
 * The Interface IPricingPolicy.
 */
public interface IPricingPolicy extends IDataObject<Long>, INamed {
  public static final String ID = "pricingPolicyKey";
  public static final String NAME = "name";
}
