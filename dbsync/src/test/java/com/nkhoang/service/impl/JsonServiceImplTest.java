package com.nkhoang.service;


import com.nkhoang.model.WordJson;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class JsonServiceImplTest {
  @Autowired
  @Qualifier("jsonService")
  JsonService jsonService;
  @Autowired
  @Qualifier("dictionaryLookupService")
  DictionaryLookupService dictionaryLookupService;

  @Test
  public void testDeserializeFrom() throws Exception {
    String source = dictionaryLookupService.query("http://mini-dictionary.appspot.com/vocabulary/lookup.html", "get");
    WordJson wordJson = jsonService.deserializeFrom(source);

    Assert.assertTrue(wordJson != null);
  }
}
