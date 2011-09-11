package com.nkhoang.gae.ws;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
    private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
    private VocabularyService vocabularyService;

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

    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
