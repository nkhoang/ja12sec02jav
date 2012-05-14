package com.nkhoang.exception;

/**
 * @author hnguyen
 */
public class DictionaryLookupServiceException extends Exception {
  public DictionaryLookupServiceException(String message) {
    super(message);
  }

  public DictionaryLookupServiceException(String message, Throwable t) {
    super(message, t);
  }
}
