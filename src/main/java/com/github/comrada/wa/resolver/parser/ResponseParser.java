package com.github.comrada.wa.resolver.parser;

import com.github.comrada.wa.dto.TransactionDetail;

public interface ResponseParser {

  TransactionDetail parse(String content);
}
