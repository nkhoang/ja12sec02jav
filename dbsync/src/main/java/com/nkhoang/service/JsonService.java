package com.nkhoang.service;

import com.nkhoang.exception.JsonServiceException;
import com.nkhoang.model.WordJson;
import org.springframework.stereotype.Service;

@Service
public interface JsonService {
   /**
    * Deserialize WordEntity JSON type from JSON data.
    *
    * @param source the source to parse.
    * @return the returned WordJson.
    */
   WordJson deserializeFrom(String source) throws JsonServiceException;
}
