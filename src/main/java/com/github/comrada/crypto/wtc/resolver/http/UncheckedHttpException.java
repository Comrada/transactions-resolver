package com.github.comrada.crypto.wtc.resolver.http;

import com.github.comrada.crypto.wtc.exception.Retryable;

public class UncheckedHttpException extends RuntimeException implements Retryable {

  public UncheckedHttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public UncheckedHttpException(String message) {
    super(message);
  }

  @Override
  public boolean retry() {
    return true;
  }
}
