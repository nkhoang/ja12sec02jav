package com.nkhoang.service;

import com.nkhoang.exception.DictionaryLookupServiceException;
import org.springframework.stereotype.Service;

@Service
public interface DictionaryLookupService {
  String query(String resourceUrl, String w) throws DictionaryLookupServiceException;
}
