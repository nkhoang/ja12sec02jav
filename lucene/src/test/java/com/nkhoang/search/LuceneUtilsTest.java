package com.nkhoang.search;


import com.google.gson.Gson;
import com.nkhoang.common.FileUtils;
import com.nkhoang.model.Word;
import com.nkhoang.model.WordLucene;
import com.nkhoang.vocabulary.VocabularyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml"})
public class LuceneUtilsTest {
   private static final Logger LOG = LoggerFactory.getLogger(LuceneUtilsTest.class.getCanonicalName());
   private static final String TEST_HOST_NAME = "test-phonecard.appspot.com";

   @Autowired
   private StandardPBEStringEncryptor propertyEncryptor;

   public StandardPBEStringEncryptor getPropertyEncryptor() {
      return propertyEncryptor;
   }

   public void setPropertyEncryptor(StandardPBEStringEncryptor propertyEncryptor) {
      this.propertyEncryptor = propertyEncryptor;
   }

   @Before
   public void prepareLuceneUtils() throws Exception {
      // LuceneUtils.getLuceneWriter();
      // LuceneUtils.getLuceneSearcher();
   }

   @After
   /**
    * After test close all Lucene writer and index searcher.
    */
   public void closeLuceneUtils() throws Exception {
      LuceneUtils.closeLuceneWriter();
      LuceneUtils.closeSearcher();
   }

   @Test
   public void testSearchById() throws Exception {
      Assert.assertTrue("Failed to search existing word", CollectionUtils.isNotEmpty(LuceneSearchUtils.performSearchById("cons")));
   }

   @Test
   public void testSearchByContent() throws Exception {
      List<Document> foundDocs = LuceneSearchUtils.performSearchByContent("nông cạn");
      Assert.assertTrue("Failed to search existing word.",
            CollectionUtils.isNotEmpty(foundDocs));

      StringBuilder foundWords = new StringBuilder();
      for (Document doc : foundDocs) {
         foundWords.append(doc.get(LuceneSearchFields.ID) + " ");
      }

      LOG.info(foundWords.toString());
   }

   @Test
   public void testBuildLuceneFromScratch() throws Exception {
      List<String> wordList = FileUtils.readWordsFromFile("lucene/src/main/resources/fullList.txt");

      if (CollectionUtils.isNotEmpty(wordList)) {
         LOG.info("Total words found: " + wordList.size());
         int index = 62000;
         int size = 2000;
         int nextIndex = index + size;
         Writer writer = new FileWriter(new File("lucene/src/main/resources/wordsJSON.txt"), true);
         for (int i = index; i < wordList.size(); i++) {
            // check word in Lucene if it is existing.
            List<Document> foundDocs = new ArrayList();
            String w = wordList.get(i).trim().toLowerCase();
            if (w.compareToIgnoreCase("services") == 0) {
               continue;
            }
            try {
               foundDocs = LuceneSearchUtils.performSearchById(w);
            } catch (Exception e) {

            }
            if (CollectionUtils.isEmpty(foundDocs)) {
               // it is safe to perform search using our servers.
               Word word = null;
               try {
                  word = VocabularyUtils.lookupWord(TEST_HOST_NAME, w, propertyEncryptor);
                  // save word to file.
               } catch (Exception e) {
                  continue;
               }

               Gson gson = new Gson();
               // check to make sure the server response to the request.
               if (StringUtils.isBlank(word.getDescription()) || CollectionUtils.isEmpty(word.getMeanings())) {
                  LOG.info("Word : [" + w + "] is invalid.");
               } else {
                  writer.append(gson.toJson(word) + "\n");
                  LuceneUtils.writeWordToIndex(word);
               }
            } else {
               LOG.info("Word [" + w + "] is existing. Skipped.");
            }
            LOG.info("Index: " + index + " word: [" + w + "]");
            index++;
            if (index % 100 == 0) {
               LuceneUtils.closeLuceneWriter();
            }
            if (index == nextIndex) {
               writer.close();
               break;
            }
         }
      }
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
      words = VocabularyUtils.getAllWordsByRange("minion-1.appspot.com", index, 1000, "asc");
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
            words = VocabularyUtils.getAllWordsByRange("minion-1.appspot.com", index, 1000, "asc");
         } while (CollectionUtils.isNotEmpty(words));
      } catch (Exception e) {

      } finally {
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
            List<Document> docs = LuceneSearchUtils.performSearchById(s);
            if (CollectionUtils.isNotEmpty(docs) && docs.size() > 1) {
               writer2.append(s + "\n");
            }
            if (CollectionUtils.isEmpty(docs)) {
               writer.append(s + "\n");
            }
         }
      } catch (Exception e) {

      } finally {
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

      } catch (Exception e) {

      } finally {
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
      } catch (Exception e) {
      } finally {
         if (writer != null) writer.close();
      }
      LOG.info("Filtered size : " + wordList.size());
   }

   @Test
   public void testSaveAllLuceneWordToFile() throws Exception {
      List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords(null);
      FileWriter writer = null;
      try {

         writer = new FileWriter(new File("lucene/src/main/resources/LuceneWords.txt"));
         for (WordLucene wl : wordLucenes) {
            writer.append(wl.getWord() + " \n");
         }
      } catch (Exception e) {

      } finally {
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
      Word w = new Word();
      // w.setDescription("~~SOUTH CENTRAL WYO HEALTHCARE & REHAB".toLowerCase());
      w.setDescription("~~Hoang & ***??** C".toLowerCase());
      LOG.info(w.getDescription());
      LuceneUtils.writeWordToIndex(w);
      LuceneUtils.closeLuceneWriter();
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

         LOG.info(documents.get(0).get(LuceneSearchFields.ID));

      } catch (Exception e) {

      } finally {
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
               Word fullyPopulatedWord = VocabularyUtils.lookupWord(TEST_HOST_NAME, w.getDescription(), propertyEncryptor);
               // delete doc
               LOG.info(String.format("Delete doc with term = [%s]", doc.get(LuceneSearchFields.ID)));
               reader.deleteDocuments(new Term(LuceneSearchFields.ID, doc.get(LuceneSearchFields.ID)));
               LuceneUtils.writeWordToIndex(fullyPopulatedWord);
            }
         }
      } catch (Exception e) {

      } finally {
         reader.close();
      }
   }


   @Test
   public void testUpdateLuceneFromScratch() throws Exception {
      try {
         List<String> words = VocabularyUtils.getAllWordsByRange("minion-1.appspot.com", 0, 100, "asc");
         LOG.info("Word size: " + words.size());
         List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords(null);
         LOG.info("WordLucene size: " + wordLucenes.size());
         LOG.info("Working with WordLucene.");
         for (WordLucene wl : wordLucenes) {
            LOG.info(String.format("Processing ...[%s]", wl.getWord()));
            Word w = VocabularyUtils.lookupWord(TEST_HOST_NAME, wl.getWord(), propertyEncryptor);
            if (w != null) {
               LuceneUtils.writeWordToIndex(w);
               // VocabularyUtils.deleteLuceneWord(wl.getId());
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
               // luceneDocuments = LuceneUtils.performSearchByDescription(s.split("-")[0]);
            } else {
               // luceneDocuments = LuceneUtils.performSearchByDescription(s);
            }
            if (CollectionUtils.isEmpty(luceneDocuments)) {
               Word w = VocabularyUtils.lookupWord(TEST_HOST_NAME, s, propertyEncryptor);
               LuceneUtils.writeWordToIndex(w);
               Thread.sleep(400);
            } else {
               LOG.info("Found in Lucene");
            }
            count++;
            LOG.info("Total counted: " + count);
         }
      } catch (Exception e) {

      } finally {
         LuceneUtils.closeLuceneWriter();
      }

   }

   @Test
   public void testGetTotalLuceneWords() throws Exception {
      List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords(null);
      LOG.info("Total : " + wordLucenes.size());
   }

   @Test
   public void testGetAllLuceneWords() throws Exception {
      List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords(null);

      int count = 0;
      for (WordLucene wl : wordLucenes) {
         // get the word description.
         Word w = VocabularyUtils.lookupWord(TEST_HOST_NAME, wl.getWord(), propertyEncryptor);
         LuceneUtils.writeWordToIndex(w);
         // VocabularyUtils.deleteLuceneWord(wl.getId());
         count++;
      }
      LuceneUtils.closeLuceneWriter();
      LOG.info("Total: " + wordLucenes.size());
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
}