package com.nkhoang.gae.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.dao.DictionaryDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.exception.GAEException;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.Dictionary;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.utils.WebUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
  private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
  private VocabularyService vocabularyService;
  private VocabularyDao vocabularyDao;
  private DictionaryDao dictionaryDao;
  private AppConfigDao appConfigDao;
  private ApplicationService applicationService;
  private StandardPBEStringEncryptor propertyEncryptor;
  private static final String HEADER_KEY = "key";
  private static final String HEADER_SIGNATURE = "signature";
  private static final long WS_ACCEPT_INTERVAL = 1 * 60 * 1000; // in minute


  @GET
  @Produces("application/json")
  @Path("appConfig/getAll")
  public String getAllAppConfig() {
    List<String> excludeAttrs = Arrays.asList(AppConfig.SKIP_FIELDS);

    Gson gson = new GsonBuilder().setExclusionStrategies(
        new GSONStrategy(excludeAttrs)).create();

    return gson.toJson(appConfigDao.getAll());
  }


  @POST
  @Path("appConfig/saveAppConfig")
  @Consumes("application/json")
  @Produces("application/json")
  public String postAppConfig(String data) {
    Gson gson = new Gson();
    AppConfig updateAppConfig = gson.fromJson(data, AppConfig.class);
    gson = new GsonBuilder().setExclusionStrategies(
        new GSONStrategy(Arrays.asList(Dictionary.SKIP_FIELDS))).create();
    return gson.toJson(applicationService.saveAppConfig(updateAppConfig.getLabel(), updateAppConfig.getValue()));
  }

  @POST
  @Path("appConfig/deleteAppConfig")
  public void deleteAppConfig(String data) {
    Gson gson = new Gson();
    Dictionary appConfig = gson.fromJson(data, Dictionary.class);
    if (appConfig != null && appConfig.getId() != null) {
      appConfigDao.delete(appConfig.getId());
    }
  }

  @GET
  @Produces("application/json")
  @Path("dictionary/getAll")
  public String getAllDictionary() {
    List<String> excludeAttrs = Arrays.asList(Dictionary.SKIP_FIELDS);

    Gson gson = new GsonBuilder().setExclusionStrategies(
        new GSONStrategy(excludeAttrs)).create();

    return gson.toJson(dictionaryDao.getAll());
  }


  @POST
  @Path("dictionary/saveDictionary")
  @Consumes("application/json")
  @Produces("application/json")
  public String postDictionary(String data) {
    Gson gson = new Gson();
    Dictionary updatedDict = gson.fromJson(data, Dictionary.class);
    gson = new GsonBuilder().setExclusionStrategies(
        new GSONStrategy(Arrays.asList(Dictionary.SKIP_FIELDS))).create();
    if (updatedDict.getId() != null && updatedDict.getId() != 0) {
      Dictionary dbDict = dictionaryDao.get(updatedDict.getId());
      dbDict.setName(updatedDict.getName());
      dbDict.setDescription(updatedDict.getDescription());
      dictionaryDao.update(dbDict);
      return gson.toJson(dbDict);
    } else {
      dictionaryDao.save(updatedDict);
      return gson.toJson(updatedDict);
    }
  }

  @POST
  @Path("dictionary/deleteDictionary")
  public void deleteDictionary(String data) {
    Gson gson = new Gson();
    Dictionary dict = gson.fromJson(data, Dictionary.class);
    if (dict != null && dict.getId() != null) {
      dictionaryDao.delete(dict.getId());
    }
  }


  @GET
  @Produces("application/xml")
  @Path("vocabulary/search/{word}")
  public Word search(@PathParam("word") String word, @Context HttpHeaders headers) {
     if (authenticateRequest(headers)) {
      Map<String, Word> wordMap = vocabularyService.lookup(word);
      if (MapUtils.isNotEmpty(wordMap)) {
        return wordMap.values().iterator().next();
      }
    } else {
      LOG.debug("Authentication failed.");
    }
    return new Word();
  }

  private boolean authenticateRequest(HttpHeaders headers) {
    List<String> signatures = headers.getRequestHeader(HEADER_SIGNATURE);
    List<String> keys = headers.getRequestHeader(HEADER_KEY);
    String signature = null;
    String key = null;
    if (CollectionUtils.isNotEmpty(signatures)) {
      signature = signatures.get(0);
    }
    if (CollectionUtils.isNotEmpty(keys)) {
      key = keys.get(0);
    }
    boolean result = false;
    if (StringUtils.isNotEmpty(signature) && StringUtils.isNotEmpty(key)) {
      // decrypt key
      String requestTime = propertyEncryptor.decrypt(key);
      try {
        if (System.currentTimeMillis() - Long.parseLong(requestTime) < WS_ACCEPT_INTERVAL) {
          result = WebUtils.verifySignature("GAE", signature);
        }
      } catch (NumberFormatException nbex) {
        LOG.error("Incorrect Key");
      } catch (GAEException gaeEx) {
        LOG.error(gaeEx.getMessage());
      }
    } else {
      LOG.debug("Invalid request.");
    }
    return result;
  }


  public VocabularyService getVocabularyService() {
    return vocabularyService;
  }


  public void setVocabularyService(VocabularyService vocabularyService) {
    this.vocabularyService = vocabularyService;
  }

  public VocabularyDao getVocabularyDao() {
    return vocabularyDao;
  }

  public void setVocabularyDao(VocabularyDao vocabularyDao) {
    this.vocabularyDao = vocabularyDao;
  }


  public DictionaryDao getDictionaryDao() {
    return dictionaryDao;
  }

  public void setDictionaryDao(DictionaryDao dictionaryDao) {
    this.dictionaryDao = dictionaryDao;
  }

  public AppConfigDao getAppConfigDao() {
    return appConfigDao;
  }

  public void setAppConfigDao(AppConfigDao appConfigDao) {
    this.appConfigDao = appConfigDao;
  }

  public ApplicationService getApplicationService() {
    return applicationService;
  }

  public void setApplicationService(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  public StandardPBEStringEncryptor getPropertyEncryptor() {
    return propertyEncryptor;
  }

  public void setPropertyEncryptor(StandardPBEStringEncryptor propertyEncryptor) {
    this.propertyEncryptor = propertyEncryptor;
  }
}

