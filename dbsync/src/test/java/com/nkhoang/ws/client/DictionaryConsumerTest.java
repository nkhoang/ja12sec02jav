/**
 * Copyright 2012 HOANG K NGUYEN
 */
package com.nkhoang.ws.client;

import com.nkhoang.service.DictionaryLookupService;
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
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class DictionaryConsumerTest {
  @Autowired
  private DictionaryLookupService dictionaryLookupService;

  private final Logger LOG = LoggerFactory.getLogger(DictionaryConsumerTest.class.getCanonicalName());

  @Test
  public void testQuery() throws Exception {
    String response = dictionaryLookupService.query("http://mini-dictionary.appspot.com/vocabulary/lookup.html", "veteran");
    LOG.info("Response: " + response);
    Assert.assertEquals(StringUtils.isNotEmpty(response), true);
  }
}
