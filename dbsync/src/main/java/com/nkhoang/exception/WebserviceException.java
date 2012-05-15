package com.nkhoang.exception;

public class WebserviceException extends Exception {
  public WebserviceException(String message) {
    super(message);
  }

  public WebserviceException(String message, Throwable t) {
    super(message, t);
  }
}
