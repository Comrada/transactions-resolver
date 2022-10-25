package com.github.comrada.crypto.wtc.resolver;

@FunctionalInterface
public interface TransactionLoader {

  String load(String transactionUrl);
}
