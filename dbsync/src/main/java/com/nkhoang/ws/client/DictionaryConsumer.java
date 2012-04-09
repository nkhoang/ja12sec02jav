/**
 * Copyright 2012 HOANG K NGUYEN
 */
package com.nkhoang.ws.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author hoangknguyen
 */
public class DictionaryConsumer {
   public static final String DICTIONARY_WORD_PARAM = "word";
   public static final String DICTIONARY_UPDATE_IF_NEED_PARAM = "updateIfNeed";
   private WebResource wr;

   public DictionaryConsumer(String resourceUrl) {
      Client client = Client.create();
      wr = client.resource(resourceUrl);
   }

   public String query(String w) {
      MultivaluedMap queryParams = new MultivaluedMapImpl();
      queryParams.add(DICTIONARY_WORD_PARAM, w);
      queryParams.add(DICTIONARY_UPDATE_IF_NEED_PARAM, "false");
      // wr.accept("application/json");
      wr.type(MediaType.APPLICATION_JSON_TYPE);
      String response = wr.queryParams(queryParams).get(String.class);
      return response;
   }
}
