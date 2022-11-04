package io.github.comrada.crypto.wtc.resolver.parser;

import io.github.comrada.crypto.wtc.dto.TransactionDetail;

public interface ResponseParser {

  TransactionDetail parse(String content);
}
