package com.nkhoang.service.impl;

import com.nkhoang.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author hnguyen
 */
public class MessageServiceImpl implements MessageService {
  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  public String getMessage(String errorCode) {
    return getMessage(errorCode, null);
  }

  public String getMessage(String errorCode, Object[] args) {
    return messageSource.getMessage(errorCode, args, Locale.getDefault());
  }
}
