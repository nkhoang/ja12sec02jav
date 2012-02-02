package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.exception.GAEException;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.PhoneCardDiscount;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.utils.PhoneCardUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("applicationService")
public class ApplicationServiceImpl implements ApplicationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class.getCanonicalName());
  @Autowired
  public AppConfigDao appConfigDao;
  @Autowired
  SpreadsheetServiceImpl spreadsheetService;
  @Autowired
  public AppCache appCache;
  @Value("#{appConfig.delimiter}")
  private String delimiter;

  /**
   * Get the phone card discount information. Either from appcache or fetch it directly from Google Docs.
   *
   * @return the phone card discount information.
   */
  public List<PhoneCardDiscount> getPhonecardDiscountInfor() {
    if (CollectionUtils.isNotEmpty(appCache.getDiscountInfo())) {
      return appCache.getDiscountInfo();
    } else {
      List<PhoneCardDiscount> discountInfo = new ArrayList<PhoneCardDiscount>();
      try {
        discountInfo = PhoneCardUtils.getLatestPhoneCard(spreadsheetService);
        // save to appCache.
        appCache.setDiscountInfo(discountInfo);
      } catch (GAEException gaeEx) {
        LOGGER.error(gaeEx.getMessage(), gaeEx.getCause());
        // TODO: should send an email here ?
      }
      return discountInfo;
    }
  }

  public List<AppConfig> getApplicationConfiguration() {
    return appConfigDao.getAll();
  }

  /**
   * Delete an AppConfig by id.
   *
   * @param id the id to identify the AppConfig to be deleted.
   * @return true/false.
   */
  public boolean deleteAppConfig(Long id) {
    AppConfig appConfig = appConfigDao.get(id);
    appCache.removeProperty(appConfig.getLabel());

    return appConfigDao.delete(id);
  }


  /**
   * Get AppConfig by config key.
   *
   * @param configKey the config key.
   * @return the AppConfig.
   */
  private AppConfig getAppConfig(String configKey) {
    return appConfigDao.getAppConfigByLabel(configKey);
  }

  /**
   * Get Application configuration by configuration key.
   * First data will be fetched from AppCache, then if AppCache have not yet store the data
   * then the real value will be fetched directly from DB.
   *
   * @param configKey the configuration key.
   * @param delimiter the configuration value delimiter (',' for example).
   * @return a list of configuration values.
   */
  public List<String> getAppConfig(String configKey, String delimiter) {
    // check in appCache first.
    if (CollectionUtils.isNotEmpty(appCache.getProperty(configKey))) {
      return appCache.getProperty(configKey);
    }
    // if not exist then
    AppConfig config = getAppConfig(configKey);
    if (config != null && StringUtils.isNotBlank(config.getValue())) {
      String configValue = config.getValue();
      // remove all spaces
      configValue.replaceAll(" +", "");
      List<String> result = Arrays.asList(config.getValue().split(delimiter));
      appCache.addProperty(configKey, result);
      return result;
    }
    return null;
  }

  public String getSingleValueAppConfig(String configKey) {
    if (CollectionUtils.isNotEmpty(appCache.getProperty(configKey))) {
      return appCache.getProperty(configKey).get(0);
    }
    List<String> result = getAppConfig(configKey, delimiter);
    if (CollectionUtils.isNotEmpty(result)) {
      return result.get(0);
    }
    return null;
  }


  /**
   * Save an AppConfig.
   *
   * @param label the AppConfig label.
   * @param value the AppConfig value.
   * @return the saved AppConfig.
   */
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

  public SpreadsheetServiceImpl getSpreadsheetService() {
    return spreadsheetService;
  }

  public void setSpreadsheetService(SpreadsheetServiceImpl spreadsheetService) {
    this.spreadsheetService = spreadsheetService;
  }
}
