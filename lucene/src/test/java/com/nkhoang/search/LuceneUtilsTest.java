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
     * Test write word to Lucene. Perform looking up using the main site service.
     */
    public void testWriteWord() throws Exception {
        // LOG.info("Total Docs: " + LuceneUtils.getTotalDocs());
        Word w = VocabularyUtils.lookupWord("complain");

        LuceneUtils.writeWordToIndex(w);
        LuceneUtils.closeLuceneWriter();
    }

    @Test
    public void testGetAllWords() throws Exception {
        LOG.info("Caution: This only works when the size is small. ");
        List<String> words = VocabularyUtils.getAllWordsByRange(0, 100, "asc");
        LOG.info("Total words in the main site is :" + words.size());
    }

    @Test
    public void testSaveAllWordsToLucene() throws Exception {
        try {
            // get the first 1000.
            List<String> words = VocabularyUtils.getAllWordsByRange(7000, 1000, "asc");
            LOG.info("Total size : " + words.size());
            for (String s : words) {
                Word w = VocabularyUtils.lookupWord(s);
                List<Document> foundDocs = LuceneUtils.performSearchByDescription(w.getDescription());
                if (CollectionUtils.isNotEmpty(foundDocs)) {
                    LOG.info(String.format("Found word [%s], hits: [%s]", w.getDescription(), foundDocs.size()));
                    // should delete it from datastore ? too risky.
                } else {
                    LOG.info(String.format("Saving [%s] to Lucene....", w.getDescription()));
                    LuceneUtils.writeWordToIndex(w);
                }
            }
        } catch (Exception ex) {
            LOG.error("Failed to save to Lucene....", ex);
        } finally {
            LuceneUtils.closeLuceneWriter();
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
                    Word fullyPopulatedWord = VocabularyUtils.lookupWord(w.getDescription());
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
    public void testSaveAllWordLucenes
            () throws Exception {
        try {
            List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
            LOG.info("WordLucene size: " + wordLucenes.size());
            LOG.info("Working with WordLucene.");
            int count = 0;
            for (WordLucene wl : wordLucenes) {
                LOG.info(String.format("Processing ...[%s].... Number: %s", wl.getWord(), count));
                Word w = VocabularyUtils.lookupWord(wl.getWord());
                if (w != null) {
                    LuceneUtils.writeWordToIndex(w);
                    VocabularyUtils.deleteLuceneWord(wl.getId());
                }
                count++;
            }
        } catch (Exception ex) {
            LOG.error("Failed to save to Lucene....", ex);
        } finally {
            LuceneUtils.closeLuceneWriter();
        }
    }


    @Test
    public void testUpdateLuceneFromScratch
            () throws Exception {
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
        } catch (Exception e) {

        } finally {
            LuceneUtils.closeLuceneWriter();
        }

    }

    @Test
    public void testGetTotalLuceneWords
            () throws Exception {
        List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
        LOG.info("Total : " + wordLucenes.size());
    }

    @Test
    public void testGetAllLuceneWords
            () throws Exception {
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
    public void testDelete
            () throws Exception {
        VocabularyUtils.deleteLuceneWord(1L);
    }


    @Test
    public void testGetMaxDoc
            () throws Exception {
        int maxDoc = LuceneUtils.getTotalDocs();
        LOG.info(maxDoc + "");
    }

    @Test
    public void testGetNumDocs() throws Exception {
        int numDocs = LuceneUtils.getIndexReader().numDocs();
        LOG.info("NumDocs : " + numDocs);
    }


    @Test
    public void testLuceneSearch
            () throws Exception {
        Query query = LuceneUtils.buildPharseQuery("gia đình");
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
