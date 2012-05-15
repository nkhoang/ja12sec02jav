package com.nkhoang.service;

import com.nkhoang.exception.DictionaryLookupServiceException;
import org.springframework.stereotype.Service;

@Service
public interface DictionaryLookupService {
  public static final String DICT_VDICT = "vdict";
  public static final String DICT_OXFORD = "oxford";

  String query(String resourceUrl, String w) throws DictionaryLookupServiceException;
}
