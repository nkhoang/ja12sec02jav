package com.nkhoang.gae.action;

import com.nkhoang.gae.exception.GAEException;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.PhoneCardDiscount;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.service.SpreadsheetService;
import com.nkhoang.gae.service.impl.AppCache;
import com.nkhoang.gae.utils.PhoneCardUtils;
import com.nkhoang.gae.view.JSONView;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.*;

@Controller
@RequestMapping("/phonecard")
public class PhoneCardAction {
   private static Logger LOG = LoggerFactory.getLogger(PhoneCardAction.class.getCanonicalName());

   @Autowired
   private AppCache appCache;
   @Autowired
   private SpreadsheetService spreadsheetService;
   @Autowired
   private ApplicationService applicationService;
   @Value("#{appConfig['phonecard.discountShowLimit']}")
   private String discountShowLimitKeyName;
   // need a default
   private int discountShowLimit = 5;


   @RequestMapping("/index")
   public String renderIndexPage() {
      return "phonecard/index";
   }
   @RequestMapping("/order")
   public ModelAndView renderOrderPage() {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("phonecard/orderPage");

      // get configuration setting for showing discount limit
      String discountShowLimitStr = applicationService.getSingleValueAppConfig(discountShowLimitKeyName);
      // convert it to number
      try {
         discountShowLimit = Integer.parseInt(discountShowLimitStr);
         // don't worry because the default is 5;
      } catch (NumberFormatException nfe) {
         LOG.error("Could not convert discountShowLimit from string to number: the value is [" + discountShowLimitStr + "].");
      }

      modelAndView.addObject("discountShowLimit", discountShowLimit);
      return modelAndView;
   }

   @RequestMapping("/getDiscountData")
   public ModelAndView getUserData() {
      ModelAndView modelAndView = new ModelAndView();
      View jsonView = new JSONView();
      modelAndView.setView(jsonView);
      Map<String, Object> jsonData = new HashMap<String, Object>();

      List<PhoneCardDiscount> discountInfo = appCache.getDiscountInfo();
      if (CollectionUtils.isEmpty(discountInfo)) {
         // fetch new one from google docs.
         discountInfo = applicationService.getPhonecardDiscountInfor();
      } else {
         // TODO: do we need to check with current date to fetch new data.
      }

      // get configuration setting for showing discount limit
      String discountShowLimitStr = applicationService.getSingleValueAppConfig(discountShowLimitKeyName);
      // convert it to number
      try {
         discountShowLimit = Integer.parseInt(discountShowLimitStr);
         // don't worry because the default is 5;
      } catch (NumberFormatException nfe) {
         LOG.error("Could not convert discountShowLimit from string to number: the value is [" + discountShowLimitStr + "].");
      }

      jsonData.put("data", discountInfo);
      jsonData.put("success", true);
      List<String> attrs = new ArrayList<String>();
      attrs.addAll(Arrays.asList(PhoneCardDiscount.SKIP_FIELDS_USER));
      modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);
      modelAndView.addObject(GSONStrategy.DATA, jsonData);
      return modelAndView;
   }

   public AppCache getAppCache() {
      return appCache;
   }

   public void setAppCache(AppCache appCache) {
      this.appCache = appCache;
   }

   public SpreadsheetService getSpreadsheetService() {
      return spreadsheetService;
   }

   public void setSpreadsheetService(SpreadsheetService spreadsheetService) {
      this.spreadsheetService = spreadsheetService;
   }

   public ApplicationService getApplicationService() {
      return applicationService;
   }

   public void setApplicationService(ApplicationService applicationService) {
      this.applicationService = applicationService;
   }
}