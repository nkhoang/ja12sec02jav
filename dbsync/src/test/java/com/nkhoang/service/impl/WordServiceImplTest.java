package com.nkhoang.service.impl;

import com.nkhoang.service.WordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Statement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml",
      "/applicationContext-resources.xml", "/applicationContext-service.xml"})
public class WordServiceImplTest {
   @Autowired
   private DataSource dataSource;

   @Autowired
   @Qualifier("wordService")
   private WordService wordService;

   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
      statement.execute("delete from WORD");
      statement.execute("delete from SOUND");
   }

   @Test
   public void testQuery() throws Exception {
      wordService.query("get");
   }
}
