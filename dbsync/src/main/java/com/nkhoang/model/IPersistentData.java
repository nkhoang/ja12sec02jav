package com.nkhoang.model;

import java.io.Serializable;

/**
 * The Interface IPersistentData.
 * @param <K>
 */
public interface IPersistentData<K extends Serializable> extends Serializable {
  /**
   * Gets the key.
   * @return the key
   */
  K getKey();
}
