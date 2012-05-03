package com.nkhoang.common.persistence;

import com.nkhoang.model.PricingPolicyBean;
import com.nkhoang.model.PricingPolicyTest;
import com.nkhoang.model.Word;
import com.nkhoang.model.WordJson;
import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.criteria.impl.WordCriteriaImpl;
import com.nkhoang.service.JsonService;
import com.nkhoang.ws.client.DictionaryConsumer;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml", "/applicationContext-service.xml"})
public class WordDataServiceTest {

   @Autowired
   private DataSource dataSource;


   @Autowired
   private WordDataService wordDataService;

   @Autowired
   private JsonService jsonService;

   @Autowired
   private DictionaryConsumer dictionaryConsumer;

   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
   }

   @Test
   public void testInsert() throws Exception{
      WordJson wJson = jsonService.deserializeFrom(dictionaryConsumer.query("get"));
      if (wJson.getData().get("vdict") != null) {
         Word w = wJson.getData().get("vdict");
         w.setKey(null);
         wordDataService.insert(w);
      }
      if (wJson.getData().get("oxford") != null) {
         Word w = wJson.getData().get("oxford");
         w.setKey(null);
         wordDataService.insert(w);
      }


      // test get after
      IWordCriteria criteria = new WordCriteriaImpl();
      criteria.setKey(1L);
      List<Word> wordList = wordDataService.find(criteria);
      Assert.assertTrue(CollectionUtils.isNotEmpty(wordList));
   }

}
