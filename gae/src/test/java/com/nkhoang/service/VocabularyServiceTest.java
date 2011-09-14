package com.nkhoang.service;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class VocabularyServiceTest {
    @Autowired
    VocabularyService vocabularyService;
    @Test
    public void testGetPron() throws Exception{
        Word w = new Word();
        w.setDescription("innate");
        vocabularyService.lookupPron(w);

        Assert.assertTrue(StringUtils.isNotEmpty(w.getPron()));
    }

    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}


