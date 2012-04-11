package com.nkhoang.common.persistence;

import com.nkhoang.model.ResourceTypeBean;
import com.nkhoang.model.ResourceTypeTest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Statement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml", "/applicationContext-service.xml"})
public class ResourceTypeDataServiceTest {

   @Autowired
   private DataSource dataSource;

   @Autowired
   private ResourceTypeTest resourceTypeTest;

   @Autowired
   private ResourceTypeDataService resourceTypeDataService;


   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
   }

   @Test
   public void testInsert() {
      ResourceTypeBean bean = new ResourceTypeBean();
      bean.setName("bean-" + System.nanoTime());
      ResourceTypeBean savedBean = resourceTypeDataService.insert(bean);
      Assert.assertTrue(savedBean.getKey() != null);
   }

   @Test
   public void testUsingResourceTypeTest() throws Exception {
      resourceTypeTest.populate(5);
   }
}
