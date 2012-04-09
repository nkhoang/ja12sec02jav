/**
 * Copyright 2012 HOANG K NGUYEN
 */
package com.nkhoang.ws.client;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class DictionaryConsumerTest {
   @Autowired
   private DictionaryConsumer dictionaryConsumer;
   private final Logger LOG = LoggerFactory.getLogger(DictionaryConsumerTest.class.getCanonicalName());

   @Test
   public void testQuery() {
      String response = dictionaryConsumer.query("veteran");
      LOG.info("Response: " + response);
      Assert.assertEquals(StringUtils.isNotEmpty(response), true);
   }

   public void setDictionaryConsumer(DictionaryConsumer dictionaryConsumer) {
      this.dictionaryConsumer = dictionaryConsumer;
   }
}
