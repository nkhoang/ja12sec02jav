package com.nkhoang.wybness.service.impl;

import com.nkhoang.dao.IDataService;
import com.nkhoang.model.IDataObject;
import com.nkhoang.wybness.service.IService;

import java.io.Serializable;

public abstract class AbstractServiceImpl<K extends Serializable, I extends IDataObject<K>, S extends IDataService> implements IService<K, I> {
  public void insert(I entity) {
    getService().insert(entity);
  }


  public void update(I entity) {
     getService().update(entity);
  }
  public I get(K key) {
    return (I) getService().get(key);
  }

  abstract S getService();
}
