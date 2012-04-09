package com.nkhoang.service;

import com.nkhoang.exception.JsonServiceException;
import com.nkhoang.model.WordJson;

public interface JsonService {
   /**
    * Deserialize Word JSON type from JSON data.
    *
    * @param source the source to parse.
    * @return the returned WordJson.
    */
   WordJson deserializeFrom(String source) throws JsonServiceException;
}
