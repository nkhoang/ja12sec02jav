package com.nkhoang.common.persistence;

import com.nkhoang.dao.PricingPolicyDataService;
import com.nkhoang.model.PricingPolicyBean;
import com.nkhoang.model.PricingPolicyTest;
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
public class PricingPolicyDataServiceTest {

   @Autowired
   private DataSource dataSource;

   @Autowired
   private PricingPolicyTest pricingPolicyTest;

   @Autowired
   private PricingPolicyDataService pricingPolicyDataService;


   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
   }

   @Test
   public void testInsert() {
      PricingPolicyBean bean = new PricingPolicyBean();
      bean.setName("bean-" + System.nanoTime());
      PricingPolicyBean savedBean = pricingPolicyDataService.insert(bean);
      Assert.assertTrue(savedBean.getKey() != null);
   }

   @Test
   public void testUsingpricingPolicyTest() throws Exception {
      pricingPolicyTest.populate(5);
   }
}
