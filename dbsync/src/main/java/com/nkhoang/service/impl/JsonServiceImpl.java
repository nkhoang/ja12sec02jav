package com.nkhoang.service.impl;

import com.nkhoang.exception.JsonServiceException;
import com.nkhoang.model.WordJson;
import com.nkhoang.service.JsonService;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonServiceImpl implements JsonService {
   private ObjectMapper objectMapper;

   public JsonServiceImpl() {
      objectMapper = new ObjectMapper();
   }

   public WordJson deserializeFrom(String source) throws JsonServiceException {
      try {
         return objectMapper.readValue(source, WordJson.class);
      } catch (JsonParseException jsonE) {
         throw new JsonServiceException("Could not parse the JSON.", jsonE);
      } catch (JsonMappingException jsonMappingE) {
         throw new JsonServiceException("JSON mapping error.", jsonMappingE);
      } catch (IOException ioe) {
         throw new JsonServiceException("IO Exception.", ioe);
      }
   }
}
