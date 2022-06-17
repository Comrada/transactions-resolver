package com.github.comrada.crypto.wtc.resolver.parser;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;

public interface ResponseParser {

  TransactionDetail parse(String content);
}
