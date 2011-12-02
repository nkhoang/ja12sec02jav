package com.nkhoang.gae.exception;

public class GAEException extends Exception {
  // hold the error code.
  private int errorCode;

  public GAEException(String message, int errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public GAEException(String message, int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

}
