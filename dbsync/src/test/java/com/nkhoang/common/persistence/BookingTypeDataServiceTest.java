package com.nkhoang.common.persistence;

import com.nkhoang.model.BookingTypeBean;
import com.nkhoang.model.BookingTypeTest;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Statement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml", "/applicationContext-service.xml"})
public class BookingTypeDataServiceTest {

   @Autowired
   private DataSource dataSource;

   @Autowired
   private BookingTypeTest bookingTypeTest;

   @Autowired
   private BookingTypeDataService bookingTypeDataService;


   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
   }

   @Test
   public void testInsert() {
      BookingTypeBean bean = new BookingTypeBean();
      bean.setName("bean-" + System.nanoTime());
      BookingTypeBean savedBean = bookingTypeDataService.insert(bean);
      Assert.assertTrue(savedBean.getKey() != null);
   }

   @Test
   public void testUsingBookingTypeTest() throws Exception {
      bookingTypeTest.populate(5);
   }
}
