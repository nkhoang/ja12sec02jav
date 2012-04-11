package com.nkhoang.common.persistence;

import com.nkhoang.model.BookingTypeBean;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml"})
public class BookingTypeDataServiceTest {

   @Autowired
   private BookingTypeDataService bookingTypeDataService;


   @Test
   public void testInsert() {
      BookingTypeBean bean = new BookingTypeBean();
      bean.setName("bean-" + System.nanoTime());
      BookingTypeBean savedBean = bookingTypeDataService.insert(bean);
      Assert.assertTrue(savedBean.getKey() != null);
   }
}
