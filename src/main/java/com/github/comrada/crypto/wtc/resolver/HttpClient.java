package com.github.comrada.crypto.wtc.resolver;

@FunctionalInterface
public interface HttpClient {

  String load(String transactionUrl);
}
