package com.nkhoang.dao;

import com.nkhoang.model.WordEntity;
import com.nkhoang.model.WordJson;
import com.nkhoang.model.dictionary.Word;
import com.nkhoang.service.DictionaryLookupService;
import com.nkhoang.service.JsonService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.StringWriter;
import java.sql.Statement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml",
      "/applicationContext-resources.xml", "/applicationContext-service.xml"})
public class WordDataServiceTest {
   private static final Logger LOGGER = LoggerFactory.getLogger(WordDataServiceTest.class.getCanonicalName());
   @Autowired
   private DataSource dataSource;
   @Autowired
   @Qualifier("wordDataService")
   private IWordDataService wordDataService;
   @Autowired
   private JsonService jsonService;
   @Autowired
   @Qualifier("dictionaryLookupService")
   private DictionaryLookupService dictionaryLookupService;

   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
      statement.execute("delete from WORD");
   }

   @Test
   public void testInsert() throws Exception {
      WordJson wJson = jsonService.deserializeFrom(dictionaryLookupService.query(
            "http://mini-dictionary.appspot.com/vocabulary/lookup.html", "get"));

      if (wJson.getData().get("vdict") != null) {

         WordEntity w = wJson.getData().get("vdict");
         w.setKey(null);
         w.setSourceName("vdict");

         StringWriter out = new StringWriter();

         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
         objectMapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);

         objectMapper.writeValue(out, w);
         LOGGER.info(w.toString());

         Word word = new Word();
         word.setData(out.toString());
         word.setWord("get");
         word.setModificationDate(new DateTime());
         wordDataService.insert(word);
      }
   }

}
