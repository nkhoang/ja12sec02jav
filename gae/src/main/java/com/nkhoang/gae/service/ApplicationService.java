package com.nkhoang.gae.service;

import com.nkhoang.gae.model.AppConfig;

import java.util.List;

public interface ApplicationService {
   List<AppConfig> getApplicationConfiguration();

   AppConfig saveAppConfig(String label, String value);

   boolean deleteAppConfig(Long id);
}
