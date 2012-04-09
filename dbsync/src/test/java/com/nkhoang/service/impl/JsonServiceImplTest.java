package com.nkhoang.service;


import com.nkhoang.model.WordJson;
import com.nkhoang.ws.client.DictionaryConsumer;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class JsonServiceImplTest {
   @Autowired
   JsonService jsonService;
   @Autowired
   DictionaryConsumer dictionaryConsumer;

   @Test
   public void testDeserializeFrom() throws Exception {
      String source = dictionaryConsumer.query("get");
      WordJson wordJson = jsonService.deserializeFrom(source);

      Assert.assertTrue(wordJson != null);
   }
}
