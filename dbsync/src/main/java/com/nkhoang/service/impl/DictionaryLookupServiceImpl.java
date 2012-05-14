/**
 * Copyright 2012 HOANG K NGUYEN
 */
package com.nkhoang.service.impl;

import com.nkhoang.exception.DictionaryLookupServiceException;
import com.nkhoang.service.DictionaryLookupService;
import com.nkhoang.util.ErrorMessages;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author hoangknguyen
 */
public class DictionaryLookupServiceImpl implements DictionaryLookupService {
  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;
  public static final String DICTIONARY_WORD_PARAM = "word";
  public static final String DICTIONARY_UPDATE_IF_NEED_PARAM = "updateIfNeed";

  public String query(String resourceUrl, String w) throws DictionaryLookupServiceException {
    try {
      Client client = Client.create();
      WebResource wr = client.resource(resourceUrl);

      MultivaluedMap queryParams = new MultivaluedMapImpl();
      queryParams.add(DICTIONARY_WORD_PARAM, w);
      queryParams.add(DICTIONARY_UPDATE_IF_NEED_PARAM, "false");
      // wr.accept("application/json");
      wr.type(MediaType.APPLICATION_JSON_TYPE);
      String response = wr.queryParams(queryParams).get(String.class);
      return response;
    } catch (UniformInterfaceException UIEx) {
      throw new DictionaryLookupServiceException(
          messageSource.getMessage(ErrorMessages.ERR_DICT_LOOKUP, null, null), UIEx);
    }
  }
}
