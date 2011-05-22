package com.nkhoang.gae.test.vocabulary;

import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class VocabularyTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(VocabularyTest.class.getCanonicalName());


    @Autowired
    private VocabularyService vocabularyService;

    @Test
    public void testLookupVN() throws Exception {
        int i = 0;
        String[] testSample = {"take", "come", "hold"};
        do {
            Long start = System.currentTimeMillis();
            Word w = vocabularyService.lookupVN(testSample[i]);
            LOGGER.info("lookup took : " + (System.currentTimeMillis() - start) + "ms");
            i++;
        } while (i < 3);

    }


    @Test
    public void testLookupEN() throws Exception {
        LOGGER.info("LOGGER level : " + LOGGER.isDebugEnabled());
        Word w = new Word();
        w.setDescription("eloquent");
        Long start = System.currentTimeMillis();
        vocabularyService.lookupENLongman(w, w.getDescription());
        LOGGER.info("lookup took : " + (System.currentTimeMillis() - start) + "s");

        assertEquals(true, w.getMeanings().size() > 0);
    }

    @Test
    public void testLookupPron() throws Exception {
        Word w = new Word();
        w.setDescription("zeal");

        vocabularyService.lookupPron(w, w.getDescription());
        LOGGER.info(w.getPron());
        assertNotNull(w.getPron());
    }

    private String showWord(Word w) {
        StringBuilder sb = new StringBuilder();

        sb.append("Word : " + w.getDescription());

        Map<Long, List<Meaning>> m = w.getMeanings();

        Set<Long> mk = m.keySet();

        for (Long k : mk) {
            String kind = Word.WORD_KINDS[Integer.parseInt(k + "")];
            sb.append("\n [" + kind + "]");

            List<Meaning> mm = m.get(k);
            for (Meaning mmm : mm) {
                sb.append("\n");
                sb.append(mmm.toString());
            }
        }

        return sb.toString();
    }


    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
