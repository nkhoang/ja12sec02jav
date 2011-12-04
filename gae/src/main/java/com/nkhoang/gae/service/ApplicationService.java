package com.nkhoang.gae.service;

import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.PhoneCardDiscount;

import java.util.List;

public interface ApplicationService {
   List<AppConfig> getApplicationConfiguration();

   AppConfig saveAppConfig(String label, String value);

   boolean deleteAppConfig(Long id);

   List<String> getAppConfig(String configKey, String delimiter);

   String getSingleValueAppConfig(String configKey);

   List<PhoneCardDiscount> getPhonecardDiscountInfor();
}
