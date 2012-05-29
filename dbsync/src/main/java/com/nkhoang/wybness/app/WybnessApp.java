package com.nkhoang.wybness.app;

import com.nkhoang.wybness.dao.BookingTypeDataService;
import com.nkhoang.wybness.dao.ResourceTypeDataService;
import com.nkhoang.wybness.model.*;
import com.nkhoang.wybness.service.IBookingTypeService;
import com.nkhoang.wybness.service.IPricingPolicyService;
import com.nkhoang.wybness.service.IResourceTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WybnessApp {
  private static Logger LOGGER = LoggerFactory.getLogger(WybnessApp.class.getCanonicalName());
  private static IPricingPolicyService pricingPolicyService;
  private static IResourceTypeService resourceTypeService;
  private static IBookingTypeService bookingTypeService;

  public static void main(String[] args) {
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

    pricingPolicyService = (IPricingPolicyService) ctx.getBean(IPricingPolicyService.class);
    resourceTypeService = (IResourceTypeService) ctx.getBean(IResourceTypeService.class);
    bookingTypeService = (IBookingTypeService) ctx.getBean(IBookingTypeService.class);

    // updateData();
    insertData();
  }

  private static void updateData() {
    IBookingType bookingType = bookingTypeService.get(1L);

    LOGGER.info("Booking type = " + bookingType.getName());

    // create pricing policy
    IPricingPolicy pricingPolicy = pricingPolicyService.get(2L);
    LOGGER.info("Pricing policy = " + pricingPolicy.getName());

    IResourceType resourceType = resourceTypeService.get(1L);
    LOGGER.info("Resource type: " + resourceType.getName());

    // create a new Product
    IProduct product = new ProductBean();
    product.setName("Product 2");
    //product.setBookingType(bookingType);
    product.setPricingPolicy(pricingPolicy);
    //product.setResourceType(resourceType);

    bookingType.getProducts().add(product);

    bookingTypeService.update(bookingType);
  }

  private static void insertData() {
    // create pricing Policy
    IPricingPolicy pricingPolicy = new PricingPolicyBean();
    pricingPolicy.setName("Pricing Policy 1");

    pricingPolicyService.insert(pricingPolicy);
    LOGGER.info("############ Pricing policy has id: " + pricingPolicy.getKey());

    IResourceType resourceType = new ResourceTypeBean();
    resourceType.setName("Resource Type 1");
    LOGGER.info("############ Resource type has id: " + resourceType.getKey());

    resourceTypeService.insert(resourceType);

    // create a new product
    IProduct product = new ProductBean();
    product.setPricingPolicy(pricingPolicy);
    product.setResourceType(resourceType);
    product.setName("Product 1");

    IBookingType bookingType = new BookingTypeBean();
    bookingType.setName("Booking Type 1");
    //bookingType.getProducts().add(product);
    //product.setBookingType(bookingType);

    bookingTypeService.insert(bookingType);

    bookingType.getProducts().add(product);
    bookingTypeService.update(bookingType);


    LOGGER.info("############ Booking type has id: " + bookingType.getKey());
  }
}
