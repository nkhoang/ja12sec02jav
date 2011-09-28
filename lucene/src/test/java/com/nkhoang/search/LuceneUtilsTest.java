package com.nkhoang.search;


import com.nkhoang.common.collections.CollectionUtils;
import com.nkhoang.model.Word;
import com.nkhoang.model.WordLucene;
import com.nkhoang.vocabulary.VocabularyUtils;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import org.apache.lucene.search.Query;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    public void testGetAllLuceneWords() throws Exception {
        List<WordLucene> wordLucenes = VocabularyUtils.getAllLuceneWords();
        Assert.assertTrue(org.apache.commons.collections.CollectionUtils.isNotEmpty(wordLucenes));
    }

    @Test
    public void testDelete() throws Exception {
        VocabularyUtils.deleteLuceneWord(1L);
    }

    @Test
    public void testLucene() throws Exception {
        Query query = LuceneUtils.buildPharseQuery("khuynh hướng");
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
