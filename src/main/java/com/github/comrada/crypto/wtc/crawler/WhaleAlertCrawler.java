package com.github.comrada.crypto.wtc.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.resolver.TransactionLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import java.util.function.Consumer;

public class WhaleAlertCrawler implements Consumer<WhaleAlert> {

  private final TransactionLoader transactionLoader;
  private final ResponseParser responseParser;
  private final DetailsSaver detailsSaver;

  public WhaleAlertCrawler(TransactionLoader transactionLoader, ResponseParser responseParser,
      DetailsSaver detailsSaver) {
    this.transactionLoader = requireNonNull(transactionLoader);
    this.responseParser = requireNonNull(responseParser);
    this.detailsSaver = requireNonNull(detailsSaver);
  }

  @Override
  public void accept(WhaleAlert alert) {
    String pageContent = transactionLoader.load(alert.getLink());
    TransactionDetail transactionDetail = responseParser.parse(pageContent);
    detailsSaver.save(alert.getId(), transactionDetail);
  }
}
