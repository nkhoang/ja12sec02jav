package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CentralLookupService {
    Map<String, LookupService> lookupServices = new HashMap<String, LookupService>();

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
        } else {
            // WebUtils.sendMail(messageBody, mail, "Remove Duplicates report", "nkhoang.it@gmail.com");
        }
        return foundMap;
    }

    public Map<String, LookupService> getLookupServices() {
        return lookupServices;
    }

    public void setLookupServices(Map<String, LookupService> lookupServices) {
        this.lookupServices = lookupServices;
    }
}
