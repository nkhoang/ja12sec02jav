package com.nkhoang.app;

import com.nkhoang.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBSync {
  private static final Logger LOGGER = LoggerFactory.getLogger(WordService.class.getCanonicalName());

  public static void main(String[] args) {
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    WordService wordService = ctx.getBean(WordService.class);
    try {
      wordService.query("get");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
