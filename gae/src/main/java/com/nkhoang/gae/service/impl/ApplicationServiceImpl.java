package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.service.ApplicationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("applicationService")
public class ApplicationServiceImpl implements ApplicationService {
   @Autowired
   public AppConfigDao appConfigDao;
   @Autowired
   public AppCache appCache;
   @Value("#{appConfig.delimiter}")
   private String delimiter;

   public List<AppConfig> getApplicationConfiguration() {
      return appConfigDao.getAll();
   }

   public boolean deleteAppConfig(Long id) {
      AppConfig appConfig = appConfigDao.get(id);
      appCache.removeProperty(appConfig.getLabel());

      return appConfigDao.delete(id);
   }


   public AppConfig getAppConfig(String configKey) {
      return appConfigDao.getAppConfigByLabel(configKey);
   }

   public List<String> getAppConfig(String configKey, String delimiter) {
      AppConfig config = getAppConfig(configKey);
      if (config != null && StringUtils.isNotBlank(config.getValue())) {
         return Arrays.asList(config.getValue().split(delimiter));
      }
      return null;
   }

   public AppConfig saveAppConfig(String label, String value) {
      AppConfig savedAppConfig = appConfigDao.getAppConfigByLabel(label);
      if (savedAppConfig != null) {
         savedAppConfig.setLabel(label);
         savedAppConfig.setValue(value);
         // update
         appConfigDao.update(savedAppConfig);
      } else {
         savedAppConfig = new AppConfig();
         List<String> values = new ArrayList<String>();
         values.add(value);
         savedAppConfig.setLabel(label);
         savedAppConfig.setValue(value);
         appConfigDao.save(savedAppConfig);
      }
      appCache.addProperty(savedAppConfig.getLabel(), Arrays.asList(savedAppConfig.getValue().split(delimiter)));
      return savedAppConfig;
   }

   public AppConfigDao getAppConfigDao() {
      return appConfigDao;
   }

   public void setAppConfigDao(AppConfigDao appConfigDao) {
      this.appConfigDao = appConfigDao;
   }

   public AppCache getAppCache() {
      return appCache;
   }

   public void setAppCache(AppCache appCache) {
      this.appCache = appCache;
   }
}
