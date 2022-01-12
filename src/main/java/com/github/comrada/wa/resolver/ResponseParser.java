package com.github.comrada.wa.resolver;

import com.github.comrada.wa.dto.TransactionDetail;

@FunctionalInterface
public interface ResponseParser {

  TransactionDetail parse(String content);
}
