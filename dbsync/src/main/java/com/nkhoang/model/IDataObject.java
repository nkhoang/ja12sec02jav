package com.nkhoang.model;


import java.beans.PropertyChangeEvent;
import java.io.Serializable;

/**
 * The Interface IDataObject.
 *
 * @param <K>
 */
public interface IDataObject<K extends Serializable> extends IBasicDataObject, IPersistentData<K> {

   /**
    * The KEY.
    */
   String KEY = "key";

   /**
    * Gets the key.
    *
    * @return the key
    */
   K getKey();

   /**
    * Sets the key.
    *
    * @param key the key
    */
   void setKey(K key);

}
