package com.nkhoang.gae.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppCache {
   private static Logger LOG = LoggerFactory.getLogger(AppCache.class.getCanonicalName());
   private Map<String, List<String>> appProperties = new HashMap<String, List<String>>();

   public List<String> getProperty(String key) {
      return appProperties.get(key);
   }

   public void removeProperty(String propertyName) {
      LOG.info("Removing property: " + propertyName);
      if (appProperties.get(propertyName) != null) {
         appProperties.remove(propertyName);
      }
   }

   public void addProperty(String propertyName, List<String> values) {
      LOG.info("Saving property: " + propertyName);
      // override.
      appProperties.put(propertyName, values);
   }
}
