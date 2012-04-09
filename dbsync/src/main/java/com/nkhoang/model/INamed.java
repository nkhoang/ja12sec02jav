package com.nkhoang.model;


import org.hibernate.validator.constraints.Length;

/**
 * The Interface INamed.
 */
public interface INamed {

  /** The NAME. */
  String NAME = "name";

  /** The constant MAX_LENGTH_NAME. */
  int MAX_LENGTH_NAME = 128;
  
  /**
   * Gets the name.
   * @return the name
   */
  @Length(max=MAX_LENGTH_NAME)
  String getName();

  /**
   * Sets the name.
   * @param value the name
   */
  void setName(String value);
}
