package com.github.comrada.crypto.wtc.config;

import com.github.comrada.crypto.wtc.crawler.DetailsSaver;
import com.github.comrada.crypto.wtc.crawler.WhaleAlertCrawler;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import com.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import com.github.comrada.crypto.wtc.resolver.TransactionLoader;
import com.github.comrada.crypto.wtc.resolver.http.OkHttpLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.resolver.parser.TransactionTableParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.EtherscanTableParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.HtmlParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.NftSaleParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.SingleAddressParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.TransferParser;
import com.github.comrada.crypto.wtc.scheduling.BlockingTaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.ExecutionProperties;
import com.github.comrada.crypto.wtc.scheduling.ExecutionProperties.DatabaseProperties;
import com.github.comrada.crypto.wtc.scheduling.RetryingTaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.database.DatabasePoller;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  TransactionLoader transactionLoader() {
    return new OkHttpLoader();
  }

  @Bean
  ResponseParser responseParser(TransactionLoader transactionLoader) {
    Map<String, TransactionTableParser> transactionTableParsers = Map.of(
        "Transfer", new TransferParser(),
        "Mint", new SingleAddressParser(),
        "Burn", new SingleAddressParser(),
        "Lock", new SingleAddressParser(),
        "Unlock", new SingleAddressParser(),
        "NFT Sale", new NftSaleParser(transactionLoader, new EtherscanTableParser())
    );
    return new HtmlParser(transactionTableParsers);
  }

  @Bean
  Consumer<WhaleAlert> whaleAlertTransactionCrawler(TransactionLoader transactionLoader,
      ResponseParser responseParser, DetailsSaver detailsSaver) {
    return new WhaleAlertCrawler(transactionLoader, responseParser, detailsSaver);
  }

  @Bean
  DetailsSaver detailsSaver(AlertDetailRepository alertDetailRepository, WalletRepository walletRepository) {
    return new DetailsSaver(alertDetailRepository, walletRepository);
  }

  @Bean
  TaskExecutor<WhaleAlert> alertResolvingTaskExecutor(Consumer<WhaleAlert> whaleAlertCrawler,
      ExecutionProperties properties) {
    return new RetryingTaskExecutor<>(new BlockingTaskExecutor<>(whaleAlertCrawler),
        properties.getMaxRetryAttempts());
  }

  @Bean
  @ConditionalOnProperty("app.execution.database.poll")
  DatabasePoller databasePoller(WhaleAlertRepository alertRepository,
      TaskExecutor<WhaleAlert> taskExecutor, ExecutionProperties executionProperties) {
    DatabaseProperties properties = executionProperties.getDatabase();
    long initialDelay = properties.getInitialDelay().getSeconds();
    long noJobDelay = properties.getNoJobDelay().getSeconds();
    return new DatabasePoller(alertRepository, initialDelay, noJobDelay, taskExecutor);
  }
}
