package com.github.comrada.wa.resolver;

@FunctionalInterface
public interface TransactionLoader {

  String load(String transactionUrl);
}
