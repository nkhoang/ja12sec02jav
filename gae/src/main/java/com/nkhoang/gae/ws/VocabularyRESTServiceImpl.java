package com.nkhoang.gae.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.dao.DictionaryDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.WordLuceneDao;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.Dictionary;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordLucene;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
    private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
    private VocabularyService vocabularyService;
    private VocabularyDao vocabularyDao;
    private WordLuceneDao wordLuceneDao;
    private DictionaryDao dictionaryDao;
    private AppConfigDao appConfigDao;

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
        if (updateAppConfig.getId() != null && updateAppConfig.getId() != 0) {
            AppConfig dbAppConfig = appConfigDao.get(updateAppConfig.getId());
            dbAppConfig.setLabel(dbAppConfig.getLabel());
            dbAppConfig.setValue(dbAppConfig.getValue());
            appConfigDao.update(dbAppConfig);
            return gson.toJson(dbAppConfig);
        } else {
            appConfigDao.save(updateAppConfig);
            return gson.toJson(updateAppConfig);
        }
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
    public Word search(@PathParam("word") String word) {
        Map<String, Word> wordMap = vocabularyService.lookup(word);
        if (MapUtils.isNotEmpty(wordMap)) {
            return wordMap.values().iterator().next();
        }
        return null;
    }

    @GET
    @Produces("application/xml")
    @Path("vocabulary/search/id/{id}")
    public Word searchById(@PathParam("id") Long id) {
        Word result = vocabularyDao.get(id);
        if (result == null) {
            result = new Word();
        }

        return result;
    }

    @GET
    @Produces("application/json")
    @Path("/lucene/getAll")
    public String getAllLuceneWords() {
        List<WordLucene> wordLucenes = wordLuceneDao.getAll();
        List<String> results = new ArrayList<String>();
        try {
            JAXBContext context = JAXBContext.newInstance(WordLucene.class);
            Marshaller marshaller = context.createMarshaller();

            if (CollectionUtils.isNotEmpty(wordLucenes)) {
                for (WordLucene wl : wordLucenes) {
                    StringWriter writer = new StringWriter();
                    marshaller.marshal(wl, writer);
                    results.add(writer.toString());
                }
            }
        } catch (JAXBException jaxbe) {
            LOG.error("Could not initialize JAXBContext.", jaxbe);
        }
        Gson gson = new Gson();
        return gson.toJson(results);
    }

    @DELETE
    @Consumes("text/plain")
    @Path("/lucene/delete/{id}")
    public void deleteLuceneWord(@PathParam("id") Long id) {
        wordLuceneDao.delete(id);
    }

    @GET
    @Produces("application/json")
    @Path("vocabulary/getAll")
    public String getAllWords(@QueryParam("offset") int offset, @QueryParam("size") int size, @QueryParam("direction") String direction) {
        List<Word> words = vocabularyService.getAllWordsByRange(offset, size, direction, false);
        List<String> wordStrings = new ArrayList<String>();
        for (Word w : words) {
            wordStrings.add(w.getDescription());
        }

        Gson gson = new Gson();
        return gson.toJson(wordStrings);
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

    public void setWordLuceneDao(WordLuceneDao wordLuceneDao) {
        this.wordLuceneDao = wordLuceneDao;
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
}
