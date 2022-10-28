package com.github.comrada.crypto.wtc.resolver.http;

import com.github.comrada.crypto.wtc.exception.Retryable;

public class ResourceNotFoundException extends RuntimeException implements Retryable {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  @Override
  public boolean retry() {
    return false;
  }
}
