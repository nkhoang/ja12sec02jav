package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The central lookup service that contains all available services which may be used to perform a word lookup. */
public class CentralLookupService {
   private Map<String, LookupService> lookupServices = new HashMap<String, LookupService>();
   private LookupService cambridgeLookupService;

   /**
    * The central action of the service.
    *
    * @param word           the word to process.
    * @param configServices the service names in string and separated by a delimiter.
    * @return
    */
   public Map<String, Word> lookup(String word, List<String> configServices) {
      Map<String, Word> foundMap = new HashMap<String, Word>();
      if (StringUtils.isNotBlank(word) && CollectionUtils.isNotEmpty(configServices)) {
         for (String serviceName : configServices) {
            LookupService service = lookupServices.get(serviceName);
            if (service != null) {
               Word w = service.lookup(word);
               if (w != null) {
                  foundMap.put(serviceName, w);
               }
            }
         }
      }
      return foundMap;
   }

   /**
    * Search sound source for a word.
    *
    * @param word word to search.
    * @return the string represents sound source.
    */
   public String searchSoundSource(String word) {
      Word w = cambridgeLookupService.lookup(word);
      if (w != null && StringUtils.isNotBlank(w.getSoundSource())) {
         return w.getSoundSource();
      }
      return null;
   }

   public Map<String, LookupService> getLookupServices() {
      return lookupServices;
   }

   public void setLookupServices(Map<String, LookupService> lookupServices) {
      this.lookupServices = lookupServices;
   }

   public void setCambridgeLookupService(LookupService cambridgeLookupService) {
      this.cambridgeLookupService = cambridgeLookupService;
   }
}
