package com.github.comrada.crypto.wtc.config;

import static java.util.Collections.singletonMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.comrada.crypto.wtc.crawler.DetailsSaver;
import com.github.comrada.crypto.wtc.crawler.WhaleAlertCrawler;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import com.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import com.github.comrada.crypto.wtc.resolver.HttpClient;
import com.github.comrada.crypto.wtc.resolver.http.OkHttpLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.resolver.parser.html.HtmlParser;
import com.github.comrada.crypto.wtc.resolver.parser.rest.TransactionItemsLoader;
import com.github.comrada.crypto.wtc.scheduling.BlockingTaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.ExecutionProperties;
import com.github.comrada.crypto.wtc.scheduling.ExecutionProperties.DatabaseProperties;
import com.github.comrada.crypto.wtc.scheduling.RetryingTaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import com.github.comrada.crypto.wtc.scheduling.database.DatabasePoller;
import com.github.comrada.crypto.wtc.scheduling.database.DelayGenerator;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

  @Bean
  HttpClient htmlClient() {
    return new OkHttpLoader();
  }

  @Bean
  ResponseParser responseParser() {
    return new HtmlParser();
  }

  @Bean
  Consumer<WhaleAlert> whaleAlertTransactionCrawler(ResponseParser responseParser, DetailsSaver detailsSaver,
      TransactionItemsLoader transactionItemsLoader, @Qualifier("htmlClient") HttpClient htmlClient) {
    return new WhaleAlertCrawler(htmlClient, responseParser, detailsSaver, transactionItemsLoader);
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
  DatabasePoller databasePoller(WhaleAlertRepository alertRepository, DelayGenerator delayGenerator,
      TaskExecutor<WhaleAlert> taskExecutor, ExecutionProperties executionProperties) {
    DatabaseProperties properties = executionProperties.getDatabase();
    long initialDelay = properties.getInitialDelay().getSeconds();
    long noJobDelay = properties.getNoJobDelay().getSeconds();
    return new DatabasePoller(alertRepository, initialDelay, noJobDelay, taskExecutor, delayGenerator);
  }

  @Bean
  DelayGenerator delayGenerator() {
    return new DelayGenerator(6, 16);
  }

  @Bean
  HttpClient restClient(@Value("${app.wa-api-key}") String apiKey) {
    return new OkHttpLoader(singletonMap("X-WA-API-KEY", apiKey));
  }

  @Bean
  TransactionItemsLoader transactionItemsLoader(@Qualifier("restClient") HttpClient restClient) {
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new Jdk8Module());
    return new TransactionItemsLoader(objectMapper, restClient);
  }

  @Bean
  ApiGatewayStatusUpdater apiGatewayStatusUpdater(TransactionItemsLoader transactionItemsLoader) {
    return new ApiGatewayStatusUpdater(transactionItemsLoader);
  }
}
