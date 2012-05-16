package com.nkhoang.service;

import com.nkhoang.exception.ServiceException;
import com.nkhoang.exception.WebserviceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;

@Service
@Transactional
public interface WordService {
  /**
   * Query word.
   *
   * @param word the word to query.
   * @throws WebserviceException
   * @throws ServiceException
   * @throws PersistenceException
   */
  @Transactional(rollbackFor = {ServiceException.class, PersistenceException.class})
  void query(String word) throws WebserviceException, ServiceException, PersistenceException;
}
