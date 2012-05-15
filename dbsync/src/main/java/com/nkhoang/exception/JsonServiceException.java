package com.nkhoang.exception;

/**
 * Exception for Json services.
 */
public class JsonServiceException extends WebserviceException {

  public JsonServiceException(String description, Throwable t) {
    super(description, t);
  }
}
