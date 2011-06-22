package com.nkhoang.gae.test.vocabulary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class VocabularyTest {
  public static final Logger LOGGER = LoggerFactory.getLogger(VocabularyTest.class.getCanonicalName());


  @Test
  public void testFromGson() {
    Gson gson = null;
    gson = new Gson();

    String s = "{\"pron\":\"/eɪtʃ/\",\"soundSource\":\"playsoundfromflash(\\u0027http://dictionary.cambridge.org/media/british/us_pron/h/h__/h____/h.mp3\\u0027, this)\",\"description\":\"aitch\",\"meanings\":{\"8\":[{\"content\":\" the letter h when written as a word not a letter\",\"kindid\":8,\"examples\":[\"people with cockney accents tend to drop their aitches.\"]},{\"content\":\"drop your aitches\",\"kindid\":8,\"examples\":[\"people with cockney accents tend to drop their aitches.\"],\"type\":\"collo\"}]},\"kindidlist\":[8]}";

    Word w = gson.fromJson(s, Word.class);
    LOGGER.info(w.getSoundSource());

    LOGGER.info(String.format("Word meaning size = %s", w.getMeanings().size()));

  }

  @Test
  public void testSearchWordUsingSpreadsheet() {
    List<Word> words = vocabularyService.lookupWords("abc", "abc", 1, 1, 2);
    LOGGER.info(String.format("Total size received: %s", words.size()));
    Gson gson = null;
    List<String> excludeAttrs = Arrays.asList(Word.SKIP_FIELDS);
    if (excludeAttrs != null && excludeAttrs.size() > 0) {
      gson = new GsonBuilder().setExclusionStrategies(
          new GSONStrategy(excludeAttrs)).create();
    } else {
      gson = new Gson();
    }

    for (int i = 0; i < 10; i++) {
      LOGGER.info(gson.toJson(words.get(i)));
    }

  }

  @Test
  public void testLookupIdiom() throws Exception {
    Word w = new Word();
    w.setDescription("piece");

    vocabularyService.lookupIdiom(w);
  }

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
    w.setDescription("exhaust");
    Long start = System.currentTimeMillis();
    vocabularyService.lookupENLongman(w);
    LOGGER.info("lookup took : " + (System.currentTimeMillis() - start) + "s");

    assertEquals(true, w.getMeanings().size() > 0);
  }

  @Test
  public void testLookupPron() throws Exception {
    Word w = new Word();
    w.setDescription("zeal");

    vocabularyService.lookupPron(w);
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
