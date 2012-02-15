package com.nkhoang.service;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml"})
public class CambridgeLookupServiceTest {
   private static final Logger LOG = LoggerFactory
         .getLogger(CambridgeLookupServiceTest.class.getCanonicalName());

   @Autowired
   private LookupService cambridgeLookupService;

   @Test
   public void testLookup() {
      Word w = cambridgeLookupService.lookup("come");
      
      LOG.info("Sound source: " + w.getSoundSource());
      assertTrue("Pron could not empty", StringUtils.isNotEmpty(w.getPron()));
      assertTrue("Sound source could not empty", StringUtils.isNotBlank(w.getSoundSource()));;
   }
}
