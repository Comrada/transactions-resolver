package io.github.comrada.crypto.wtc.resolver.parser;

import io.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.util.Map;
import java.util.Set;
import org.jsoup.nodes.Element;

public interface TransactionTableParser {

  TransactionDetail parse(Map<String, Element> rows);

  boolean supported(Set<String> columns);
}
