package com.nkhoang.wybness.service;

import com.nkhoang.model.IDataObject;

import java.io.Serializable;

public interface IService<K extends Serializable, I extends IDataObject<K>> {
   void insert(I entity);

   void update(I entity);

   I get(K value);
}
