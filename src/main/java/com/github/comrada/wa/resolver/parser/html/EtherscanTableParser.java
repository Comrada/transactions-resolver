package com.github.comrada.wa.resolver.parser.html;

import static com.github.comrada.wa.resolver.parser.html.HtmlUtils.parseUrl;
import static com.github.comrada.wa.resolver.parser.html.HtmlUtils.select;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.resolver.parser.ResponseParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class EtherscanTableParser implements ResponseParser {

  private static final String BASE_URL = "https://etherscan.io";
  private static final String SELECTOR_FROM_ADDRESS = "a#addressCopy";
  private static final String SELECTOR_TO_ADDRESS = "a#contractCopy";

  @Override
  public TransactionDetail parse(String content) {
    Document doc = Jsoup.parse(content);
    return new TransactionDetail(
        null, null, null, null, null, null, null, null,
        parseFromAddress(doc),
        null,
        BASE_URL + parseUrl(doc, SELECTOR_FROM_ADDRESS),
        parseToAddress(doc),
        null,
        BASE_URL + parseUrl(doc, SELECTOR_TO_ADDRESS)
    );
  }

  private String parseFromAddress(Document doc) {
    return select(doc, SELECTOR_FROM_ADDRESS).text();
  }

  private String parseToAddress(Document doc) {
    return select(doc, SELECTOR_TO_ADDRESS).text();
  }
}
