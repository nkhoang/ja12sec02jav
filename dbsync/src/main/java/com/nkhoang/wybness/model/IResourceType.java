package com.nkhoang.wybness.model;


import com.nkhoang.model.IDataObject;
import com.nkhoang.model.INamed;

/**
 * The Interface IResourceType.
 */
public interface IResourceType extends IDataObject<Long>, INamed {
  public static final String ID = "resourceTypeKey";
  public static final String NAME = "name";
}
