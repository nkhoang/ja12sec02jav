package com.nkhoang.exception;

/**
 * @author hnguyen
 */
public class ServiceException extends Exception {
  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable t) {
    super(message, t);
  }
}
