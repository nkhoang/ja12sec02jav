package com.nkhoang.service;

import com.google.gson.Gson;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml"})
public class VdictLookupServiceTest {
   private static final Logger LOG = LoggerFactory.getLogger(VdictLookupServiceTest.class.getCanonicalName());
   @Autowired
   private LookupService vdictLookupService;

   @Test
   public void testLookup() {
      Word w = vdictLookupService.lookup("help");
      Gson gson = new Gson();
      LOG.info(gson.toJson(w));

   }
}
