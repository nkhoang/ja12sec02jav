package com.nkhoang.gae.ws;

import com.google.gson.Gson;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.WordLuceneDao;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordLucene;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.search.LuceneUtils;
import com.nkhoang.vocabulary.VocabularyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
	private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
	private VocabularyService vocabularyService;
	private VocabularyDao     vocabularyDao;
	private WordLuceneDao     wordLuceneDao;

	@Produces("application/xml")
	@Path("/search/{word}")
	public Word search(@PathParam("word") String word) {
		Word result = null;
		try {
			result = vocabularyService.save(word);
		}
		catch (Exception ex) {
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
	@Produces("application/json")
	@Path("/lucene/getAll")
	public String getAllLuceneWords() {
		List<WordLucene> wordLucenes = wordLuceneDao.getAll();
		List<String> results = new ArrayList<String>();
		try {
			JAXBContext context = JAXBContext.newInstance(WordLucene.class);
			Marshaller marshaller= context.createMarshaller();

			if (CollectionUtils.isNotEmpty(wordLucenes)) {
				for (WordLucene wl : wordLucenes) {
					StringWriter writer = new StringWriter();
					marshaller.marshal(wl, writer);
					results.add(writer.toString());
				}
			}
		}
		catch (JAXBException jaxbe) {
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
	@Path("/getAll")
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
}
