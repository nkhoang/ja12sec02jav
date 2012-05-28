package com.nkhoang.wybness.service;

import com.nkhoang.dao.IDataService;
import com.nkhoang.model.IDataObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@Transactional
public interface IService<K extends Serializable, I extends IDataObject<K>> {
  void insert(I entity);

  void update(I entity);

  I get(K value);
}
