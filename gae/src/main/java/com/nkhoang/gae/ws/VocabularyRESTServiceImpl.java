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
    @Path("demo/practitioner")
    public String getPractitioners() throws Exception {
        Thread.sleep(2000);
        return "[{\n" +
                "    \"name\": \"SMITH, JAMIE L\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Technologist and Technicians\",\n" +
                "    \"address\": \"PO BOX 7434 xx JACKSON, WY  83002-7434\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, CARY GARNETT\",\n" +
                "    \"npi\": \"2610068627\",\n" +
                "    \"type\": \"Dentist\",\n" +
                "    \"address\": \"1115 MAPLE WAY xx JACKSON, WY 83001 xx Phone: 307-733-7044 xx Fax: 307-734-1409\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, STACEY ALLISON\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Pharmacology\",\n" +
                "    \"address\": \"Pharmacology 29 BLACK COAL DR xx FORT WASHAKIE, WY 82514 \",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"NC\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, TANYA MARIE\",\n" +
                "    \"npi\": \"3229424202\",\n" +
                "    \"type\": \"Assistive Therapy\",\n" +
                "    \"address\": \"317 N FALER AVE xx    PINEDALE, WY 82941 xx    Phone: 307-367-6236\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"OH; WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, BRIAN C\",\n" +
                "    \"npi\": \"1218409847\",\n" +
                "    \"type\": \"Chiropractor\",\n" +
                "    \"address\": \"325 W 18TH ST  xx    CHEYENNE, WY 82001-4403 xx    Phone: 307-634-8011\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, WILLIAM ROBERT\",\n" +
                "    \"npi\": \"4194866324\",\n" +
                "    \"type\": \"Physician\",\n" +
                "    \"address\": \"3070 S BRIDLE DR xxJACKSON, WY 83001-9124\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, PAULA S\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Assistive Therapy\",\n" +
                "    \"address\": \"207 HOLLY AVE xx    SARATOGA, WY 82331\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, STEPHANIE M\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Technologist and Technicians\",\n" +
                "    \"address\": \"501 S BURMA AVE xx GILLETTE, WY 82716-3426\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, GERALD L\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Physician\",\n" +
                "    \"address\": \"803 CUSTER STxx    CHEYENNE, WY 82009-3314     xxPhone: 307-632-7573 \",\n" +
                "    \"status\": \"Inactive\",\n" +
                "    \"licenseState\": \"CO; WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, ANTHONY G\",\n" +
                "    \"npi\": \"\",\n" +
                "    \"type\": \"Assistive Therapy \",\n" +
                "    \"address\": \"135 N GOULD STxx    SHERIDAN, WY 82801-3927 xx    Phone: 307-674-1632\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"VA; WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, MARION NOLAN\",\n" +
                "    \"npi\": \"2659155187\",\n" +
                "    \"type\": \"Physician\",\n" +
                "    \"address\": \"625 ALBANY AVExx    TORRINGTON, WY 82240-1530 xx    Phone: 307-532-4131 xx    Fax: 307-532-5617\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"VA; WY\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SMITH, PAULA\",\n" +
                "    \"npi\": \"816371045\",\n" +
                "    \"type\": \"Assistive Therapy\",\n" +
                "    \"address\": \"PO BOX 1503  xx    CASPER, WY 82602-1503 xx    Phone: 307-258-3633\",\n" +
                "    \"status\": \"Active\",\n" +
                "    \"licenseState\": \"TX\",\n" +
                "    \"vendible\": \"Y\",\n" +
                "    \"numAffil\": 0\n" +
                "  }\n" +
                "]";
    }

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
        if (authenticateRequest(headers) || true) {
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

