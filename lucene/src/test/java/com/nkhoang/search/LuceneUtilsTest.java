package com.nkhoang.search;


import com.nkhoang.model.Word;
import com.nkhoang.model.WordLucene;
import com.nkhoang.vocabulary.VocabularyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class LuceneUtilsTest {
	private static final Logger LOG = LoggerFactory.getLogger(LuceneUtilsTest.class.getCanonicalName());

	@After
	/**
	 * After test close all Lucene writer and index searcher.
	 */
	public void closeSearcher() throws Exception {
		LuceneUtils.closeLuceneWriter();
		LuceneUtils.closeSearcher();
	}

	@Test
	/**
	 * Last index = 26106.
	 */
	public void testSaveAllWordsToFile() throws Exception {
		List<String> words = new ArrayList<String>();
		List<String> wordList = new ArrayList<String>();
		// first call.
		int index = 0;
		int total = 0;
		LOG.info("checking with index =" + index);
		words = VocabularyUtils.getAllWordsByRange(index, 1000, "asc");
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File("lucene/src/main/resources/listALlWords.txt"));
			do {
				wordList.addAll(words);
				index += 1000;
				LOG.info("checking with index =" + index);
				for (String w : words) {
					writer.append(w + " \n");
				}

				total += words.size();
				words = VocabularyUtils.getAllWordsByRange(index, 1000, "asc");
			} while (CollectionUtils.isNotEmpty(words));
		}
		catch (Exception e) {

		}
		finally {
			if (writer != null) writer.close();
		}

		LOG.info("total: " + wordList.size());
	}

	@Test
	public void testGetWordsNotInLuceneIndex() throws Exception {

		FileReader reader = null;
		FileWriter writer = null, writer2 = null;
		LineNumberReader lineReader = null;
		try {
			List<String> wordList = new ArrayList<String>();

			reader = new FileReader(new File("lucene/src/main/resources/WordList.txt"));
			lineReader = new LineNumberReader(reader);
			String line = "";
			while (StringUtils.isNotEmpty(line = lineReader.readLine())) {
				wordList.add(line.trim());
			}

			LOG.info("WordList size: " + wordList.size());
			writer = new FileWriter(new File("lucene/src/main/resources/NotInLuceneList.txt"));
			writer2 = new FileWriter(new File("lucene/src/main/resources/Duplicates.txt"));
			for (String s : wordList) {
				List<Document> docs = LuceneUtils.performSearchByDescription(s);
				if (CollectionUtils.isNotEmpty(docs) && docs.size() > 1) {
					writer2.append(s + "\n");
				}
				if (CollectionUtils.isEmpty(docs)) {
					writer.append(s + "\n");
				}
			}
		}
		catch (Exception e) {

		}
		finally {
			reader.close();
			lineReader.close();
			writer.close();
		}
	}

	@Test
	public void testGetWordsNotInLuceneList() throws Exception {
		List<String> wordList = new ArrayList<String>();
		List<String> wordLuceneList = new ArrayList<String>();
		FileReader reader = null;
		LineNumberReader lineReader = null;
		try {
			reader = new FileReader(new File("lucene/src/main/resources/Words.txt"));
			lineReader = new LineNumberReader(reader);
			String line = "";
			while (StringUtils.isNotEmpty(line = lineReader.readLine())) {
				wordList.add(line.trim());
			}
			reader = new FileReader(new File("lucene/src/main/resources/LuceneWords.txt"));
			lineReader = new LineNumberReader(reader);
			while (StringUtils.isNotEmpty(line = lineReader.readLine())) {
				wordLuceneList.add(line.trim());
			}

		}
		catch (Exception e) {

		}
		finally {
			reader.close();
			lineReader.close();
		}
		LOG.info("WordList size: " + wordList.size());
		LOG.info("WordLuceneList size: " + wordLuceneList.size());

		wordList.removeAll(wordLuceneList);
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File("lucene/src/main/resources/FilteredWords.txt"));
			for (String w : wordList) {
				writer.append(w + "\n");
			}
		}
		catch (Exception e) {
		}
		finally {
			if (writer != null) writer.close();
		}
		LOG.info("Filtered size : " + wordList.size());
	}

	@Test
	public void testSaveAllLuceneWordToFile() throws Exception {
		List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
		FileWriter writer = null;
		try {

			writer = new FileWriter(new File("lucene/src/main/resources/LuceneWords.txt"));
			for (WordLucene wl : wordLucenes) {
				writer.append(wl.getWord() + " \n");
			}
		}
		catch (Exception e) {

		}
		finally {
			if (writer != null) writer.close();
		}
		LOG.info("WordLucene size: " + wordLucenes.size());
	}

	@Test
	/**
	 * Test write word to Lucene. Perform looking up using the main site service.
	 */
	public void testWriteWord() throws Exception {
		// LOG.info("Total Docs: " + LuceneUtils.getTotalDocs());
		Word w = VocabularyUtils.lookupWord("oblational");

        LOG.info(w.getDescription());
		// LuceneUtils.writeWordToIndex(w);
		// LuceneUtils.closeLuceneWriter();
	}


	@Test
	public void testSaveAllWordsToLucene() throws Exception {
		try {
			// get the first 1000.
			List<String> words = VocabularyUtils.getAllWordsByRange(55000, 1000, "asc");
			LOG.info("Total size : " + words.size());
			int count = 0;
			for (String s : words) {
				List<Document> foundDocs = LuceneUtils.performSearchByDescription(s);
				if (CollectionUtils.isNotEmpty(foundDocs)) {
					LOG.info(String.format("Found word [%s], hits: [%s]", s, foundDocs.size()));
				} else {
					Word w = VocabularyUtils.lookupWord(s);
					if (w != null) {
						LOG.info("Count: " + count++);
						LOG.info(String.format("Saving [%s] to Lucene....", w.getDescription()));
						LuceneUtils.writeWordToIndex(w);
					}
				}
			}
		}
		catch (Exception ex) {
			LOG.error("Failed to save to Lucene....", ex);
		}
		finally {
			LuceneUtils.closeLuceneWriter();
			LuceneUtils.closeSearcher();
		}
	}

	@Test
	/**
	 * Check to make sure that Lucene index does not contain any duplicates.
	 */
	public void testLuceneCheckDuplicates() throws Exception {
		IndexReader reader = LuceneUtils.getIndexReader();
		try {
			int indexSize = reader.numDocs();
			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < indexSize; i++) {
				if (!reader.isDeleted(i)) {
					LOG.info("Doc: " + i);
					documents.add(reader.document(i));
				}
			}

			// check to make sure that word description is stored in each document.
			LOG.info(documents.get(0).get(LuceneSearchFields.WORD_DESCRIPTION));
			LOG.info(documents.get(0).get(LuceneSearchFields.ID));

		}
		catch (Exception e) {

		}
		finally {
			reader.close();
		}
	}


	@Test
	public void testReupdateIndex() throws Exception {
		IndexReader reader = LuceneUtils.getIndexReader();
		try {
			int indexSize = reader.numDocs();
			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < indexSize; i++) {
				if (!reader.isDeleted(i)) {
					LOG.info("Doc: " + i);
					documents.add(reader.document(i));
				}
			}
			for (Document doc : documents) {
				String wordId = doc.get(LuceneSearchFields.ID);
				LOG.info("Lookup word with id : " + wordId);
				Word w = VocabularyUtils.lookupWordById(wordId);
				if (w != null && StringUtils.isNotEmpty(w.getDescription())) {
					Word fullyPopulatedWord = VocabularyUtils.lookupWord(w.getDescription());
					// delete doc
					LOG.info(String.format("Delete doc with term = [%s]", doc.get(LuceneSearchFields.ID)));
					reader.deleteDocuments(new Term(LuceneSearchFields.ID, doc.get(LuceneSearchFields.ID)));
					LuceneUtils.writeWordToIndex(fullyPopulatedWord);
				}
			}
		}
		catch (Exception e) {

		}
		finally {
			reader.close();
		}
	}

	@Test
	public void testSaveAllWordLucenes() throws Exception {
		try {
			List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
			LOG.info("WordLucene size: " + wordLucenes.size());
			LOG.info("Working with WordLucene.");
			int count = 0;
			for (WordLucene wl : wordLucenes) {
				LOG.info(String.format("Processing ...[%s].... Number: %s", wl.getWord(), count));
				List<Document> docs = LuceneUtils.performSearchByDescription(wl.getWord());
				if (CollectionUtils.isEmpty(docs)) {
					Word w = VocabularyUtils.lookupWord(wl.getWord());
					if (w != null) {
						LuceneUtils.writeWordToIndex(w);
					}
				}
				VocabularyUtils.deleteLuceneWord(wl.getId());
				count++;
			}
		}
		catch (Exception ex) {
			LOG.error("Failed to save to Lucene....", ex);
		}
		finally {
			LuceneUtils.closeLuceneWriter();
		}
	}

	@Test
	public void getCloseWriter() throws Exception {
		LuceneUtils.closeLuceneWriter();
	}


	@Test
	public void testUpdateLuceneFromScratch() throws Exception {
		try {
			List<String> words = VocabularyUtils.getAllWordsByRange(0, 100, "asc");
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
			int count = 0;
			for (String s : words) {
				LOG.info(String.format("Processing ...[%s]", s));
				List<Document> luceneDocuments = new ArrayList<Document>();
				if (s.split("-").length > 1) {
					LOG.info("Word with - : " + s);
					luceneDocuments = LuceneUtils.performSearchByDescription(s.split("-")[0]);
				} else {
					luceneDocuments = LuceneUtils.performSearchByDescription(s);
				}
				if (CollectionUtils.isEmpty(luceneDocuments)) {
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
	public void testGetTotalLuceneWords() throws Exception {
		List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
		LOG.info("Total : " + wordLucenes.size());
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
	public void testGetMaxDoc() throws Exception {
		int maxDoc = LuceneUtils.getTotalDocs();
		LOG.info(maxDoc + "");
	}

	@Test
	public void testGetNumDocs() throws Exception {
		int numDocs = LuceneUtils.getIndexReader().numDocs();
		LOG.info("NumDocs : " + numDocs);
	}


	@Test
	public void testLuceneSearch() throws Exception {
		Query query = LuceneUtils.buildPharseQuery("cà ");
		List<Document> documents = LuceneUtils.performSearch(query);
		for (Document doc : documents) {
			Word w = VocabularyUtils.lookupWordById(doc.get(LuceneSearchFields.ID));
			if (w != null) {
				LOG.info(w.getDescription());
			}
		}
		Assert.assertNotNull(documents);
	}
}
