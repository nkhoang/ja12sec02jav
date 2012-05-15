package com.nkhoang.service;

import com.nkhoang.exception.ServiceException;
import com.nkhoang.exception.WebserviceException;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;

@Service
public interface WordService {
   void query(String word) throws WebserviceException, ServiceException, PersistenceException;
}
