package com.github.comrada.crypto.wtc.config;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.resolver.parser.rest.TransactionItemsLoader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ApiGatewayStatusUpdater {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayStatusUpdater.class);

  private final TransactionItemsLoader transactionItemsLoader;

  public ApiGatewayStatusUpdater(TransactionItemsLoader transactionItemsLoader) {
    this.transactionItemsLoader = requireNonNull(transactionItemsLoader);
  }

  @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
  void updateTransactionItemsLoaderStatus() {
    LOGGER.info("Updating WhaleAlert API status...");
    transactionItemsLoader.updateStatus();
  }
}
