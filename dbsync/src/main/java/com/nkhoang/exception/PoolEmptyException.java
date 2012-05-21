package com.nkhoang.exception;

public class PoolEmptyException extends Exception {
  public PoolEmptyException(String message) {
    super(message);
  }

  public PoolEmptyException(String message, Throwable t) {
    super(message, t);
  }

}
