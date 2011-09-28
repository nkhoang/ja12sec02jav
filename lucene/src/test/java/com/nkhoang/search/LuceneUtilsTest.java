package com.nkhoang.search;


import com.nkhoang.model.Word;
import com.nkhoang.model.WordLucene;
import com.nkhoang.vocabulary.VocabularyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.Query;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LuceneUtilsTest {

	private static final Logger LOG = LoggerFactory.getLogger(LuceneUtilsTest.class.getCanonicalName());

	@Test
	public void testGetWord() throws Exception {
		Word w = VocabularyUtils.lookupWord("complain");

		LuceneUtils.writeWordToIndex(w);
		LuceneUtils.closeLuceneWriter();
	}

	@Test
	public void testGetAllWords() throws Exception {
		List<String> words = VocabularyUtils.getAllWords();
		LOG.info(words.get(words.size()-1));
		LOG.info(words.size() + "");
	}

	@Test
	public void testSaveAllWordsToLucene() throws Exception {
		List<String> words = VocabularyUtils.getAllWords();
		for (String s : words) {
			Word w = VocabularyUtils.lookupWord(s);
			LOG.info(String.format("Saving [%s] to Lucene....", w.getDescription()));
			LuceneUtils.writeWordToIndex(w);
		}

		LuceneUtils.closeLuceneWriter();
	}

	@Test
	public void testUpdateLuceneFromScratch() throws Exception {
		try {
			List<String> words = VocabularyUtils.getAllWords();
			LOG.info("Word size: " + words.size());
			List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
			LOG.info("WordLucene size: " + wordLucenes.size());
			LOG.info("Working with WordLucene.");
			for (WordLucene wl : wordLucenes) {
				LOG.info(String.format("Processing ...[%s]", wl.getWord()));
				Word w = VocabularyUtils.lookupWord(wl.getWord());
				if (w != null) {
					LuceneUtils.writeWordToIndex(w);
					VocabularyUtils.deleteLuceneWord(wl.getId());
					words.remove(wl.getWord());
				}
			}
			LOG.info("Word size after WordLucene: " + words.size());
			int count=0;
			for (String s : words) {
				LOG.info(String.format("Processing ...[%s]", s));
				List<String> luceneIds = new ArrayList<String>();
				if (s.split("-").length > 1) {
					LOG.info("Word with - : " + s);
					luceneIds = LuceneUtils.performSearchByWord(s.split("-")[0]);
				} else {
					luceneIds = LuceneUtils.performSearchByWord(s);
				}
				if (CollectionUtils.isEmpty(luceneIds)) {
					Word w = VocabularyUtils.lookupWord(s);
					LuceneUtils.writeWordToIndex(w);
					Thread.sleep(400);
				} else {
					 LOG.info("Found in Lucene");
				 }
				count++;
				LOG.info("Total counted: " + count);
			}
		}
		catch (Exception e) {

		}
		finally {
			LuceneUtils.closeLuceneWriter();
		}

	}

	@Test
	public void testGetAllLuceneWords() throws Exception {
		List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();

		int count = 0;
		for (WordLucene wl : wordLucenes) {
			// get the word description.
			Word w = VocabularyUtils.lookupWord(wl.getWord());
			LuceneUtils.writeWordToIndex(w);
			VocabularyUtils.deleteLuceneWord(wl.getId());
			count++;
		}
		LuceneUtils.closeLuceneWriter();
		LOG.info("Total: " + wordLucenes.size());
	}

	@Test
	public void testDelete() throws Exception {
		VocabularyUtils.deleteLuceneWord(1L);
	}

	@Test
	public void testSearchByWord() throws Exception {
		List<String> ids = LuceneUtils.performSearchByWord("knock");
		LOG.info(ids.size() + "");
	}

	@After
	public void closeSearcher() throws Exception{
		LuceneUtils.closeLuceneWriter();
	}

	@Test
	public void testGetMaxDoc() throws Exception {
		int maxDoc = LuceneUtils.getTotalDocs();
		LOG.info(maxDoc + "");
	}

	@Test
	public void testLucene() throws Exception {
		Query query = LuceneUtils.buildPharseQuery("cá tính");
		List<String> result = LuceneUtils.performSearch(query);
		for (String s : result) {
			Word w = VocabularyUtils.lookupWordById(s);
			if (w != null) {
				LOG.info(w.getDescription());
			}
		}
		Assert.assertNotNull(result);
	}
}
