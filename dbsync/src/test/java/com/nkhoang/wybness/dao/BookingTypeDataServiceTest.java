package com.nkhoang.wybness.dao;

import com.nkhoang.wybness.model.criteria.IBookingTypeCriteria;
import com.nkhoang.wybness.model.criteria.impl.BookingTypeCriteriaImpl;
import com.nkhoang.wybness.model.*;
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
public class BookingTypeDataServiceTest {

   @Autowired
   private DataSource dataSource;

   @Autowired
   private BookingTypeTest bookingTypeTest;

   @Autowired
   private ResourceTypeTest resourceTypeTest;

   @Autowired
   private PricingPolicyTest pricingPolicyTest;

   @Autowired
   private BookingTypeDataService bookingTypeDataService;


   @Before
   public void cleanDB() throws Exception {
      Statement statement = dataSource.getConnection().createStatement();

      statement.execute("delete from BOOKING_TYPE");
      statement.execute("delete from RESOURCE_TYPE");
      statement.execute("delete from PRICING_POLICY");
      statement.execute("delete from PRODUCT");
   }

   @Test
   public void testInsert() {
      BookingTypeBean bean = new BookingTypeBean();
      bean.setName("bean-" + System.nanoTime());
      IBookingType savedBean = bookingTypeDataService.insert(bean);
      Assert.assertTrue(savedBean.getKey() != null);
   }

   @Test
   public void testUsingBookingTypeTest() throws Exception {
      bookingTypeTest.populate(5);
   }


   @Test
   public void testSaveCascadeProduct() throws Exception{
      List<IPricingPolicy> pricingPolicies = pricingPolicyTest.populate(1);
      List<IResourceType> resourceTypes = resourceTypeTest.populate(1);
      List<IBookingType> bookingTypes = bookingTypeTest.create(1);

      IProduct product = new ProductBean();
      product.setPricingPolicy(pricingPolicies.get(0));
      product.setResourceType(resourceTypes.get(0));
      product.setBookingType(bookingTypes.get(0));
      product.setName("Product-" + System.nanoTime());

      bookingTypes.get(0).getProducts().add(product);

      // save
      bookingTypeDataService.insert(bookingTypes.get(0));

      // then test get.
      IBookingTypeCriteria criteria = new BookingTypeCriteriaImpl();
      criteria.setNamePattern("booking%");
      List<IBookingType> result = bookingTypeDataService.find(criteria);
      Assert.assertTrue(CollectionUtils.isNotEmpty(result));

      // test get Product

      Assert.assertTrue(CollectionUtils.isNotEmpty(result.get(0).getProducts()));
      IProduct testProduct = result.get(0).getProducts().get(0);
      Assert.assertTrue(testProduct.getBookingType() != null);
      Assert.assertTrue(testProduct.getBookingType().getKey() != null);
   }


}
