package com.github.comrada.crypto.wtc.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.dto.TransactionItem;
import com.github.comrada.crypto.wtc.resolver.HttpClient;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.resolver.parser.rest.TransactionItemsLoader;
import java.util.List;
import java.util.function.Consumer;

public class WhaleAlertCrawler implements Consumer<WhaleAlert> {

  private final HttpClient httpClient;
  private final ResponseParser responseParser;
  private final DetailsSaver detailsSaver;
  private final TransactionItemsLoader transactionItemsLoader;

  public WhaleAlertCrawler(HttpClient httpClient, ResponseParser responseParser, DetailsSaver detailsSaver,
      TransactionItemsLoader transactionItemsLoader) {
    this.httpClient = requireNonNull(httpClient);
    this.responseParser = requireNonNull(responseParser);
    this.detailsSaver = requireNonNull(detailsSaver);
    this.transactionItemsLoader = requireNonNull(transactionItemsLoader);
  }

  @Override
  public void accept(WhaleAlert alert) {
    String pageContent = httpClient.load(alert.getLink());
    TransactionDetail transactionDetail = responseParser.parse(pageContent);
    if (transactionItemsLoader.supports(transactionDetail.blockchain())) {
      List<TransactionItem> transactionItems = transactionItemsLoader.load(transactionDetail);
      detailsSaver.save(alert.getId(), transactionDetail, transactionItems);
    }
  }
}
