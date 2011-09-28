package com.nkhoang.gae.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.WordLuceneDao;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordLucene;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
    private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
    private VocabularyService vocabularyService;
    private VocabularyDao vocabularyDao;
    private WordLuceneDao wordLuceneDao;

    @GET
    @Produces("application/xml")
    @Path("/search/{word}")
    public Word search(@PathParam("word") String word) {
        Word result = null;
        try {
            result = vocabularyService.save(word);
        } catch (Exception ex) {
            LOG.error(String.format("WS could not lookup: ", word), ex);
        }

        return result;
    }

    @GET
    @Produces("application/xml")
    @Path("/search/id/{id}")
    public Word searchById(@PathParam("id") Long id) {
        Word result = vocabularyDao.get(id);
        if (result == null) {
            result = new Word();
        }

        return result;
    }

    @GET
    @Produces("application/xml")
    @Path("/lucene/getAll")
    public List<WordLucene> getAllLuceneWords() {
        List<WordLucene> wordLucenes = wordLuceneDao.getAll();
        if (wordLucenes == null) {
            wordLucenes = new ArrayList<WordLucene>();
        }
        return wordLucenes;
    }

    @DELETE
    @Consumes("text/plain")
    @Path("/lucene/delete/{id}")
    public void deleteLuceneWord(@PathParam("id") Long id) {
        wordLuceneDao.delete(id);
    }

    @GET
    @Produces("application/json")
    @Path("/getAll")
    public String getAllWords() {
        List<Word> words = vocabularyService.getAllWords();
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
}
