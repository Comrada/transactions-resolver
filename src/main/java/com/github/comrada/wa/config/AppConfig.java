package com.github.comrada.wa.config;

import com.github.comrada.wa.crawler.WhaleAlertCrawler;
import com.github.comrada.wa.model.WhaleAlert;
import com.github.comrada.wa.repository.AlertDetailRepository;
import com.github.comrada.wa.repository.WhaleAlertRepository;
import com.github.comrada.wa.resolver.TransactionLoader;
import com.github.comrada.wa.resolver.http.OkHttpLoader;
import com.github.comrada.wa.resolver.parser.ResponseParser;
import com.github.comrada.wa.resolver.parser.html.HtmlParser;
import com.github.comrada.wa.scheduling.BlockingTaskExecutor;
import com.github.comrada.wa.scheduling.ExecutionProperties;
import com.github.comrada.wa.scheduling.ExecutionProperties.DatabaseProperties;
import com.github.comrada.wa.scheduling.RetryingTaskExecutor;
import com.github.comrada.wa.scheduling.TaskExecutor;
import com.github.comrada.wa.scheduling.database.DatabasePoller;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ExecutionProperties.class)
public class AppConfig {

  @Bean
  TransactionLoader transactionLoader() {
    return new OkHttpLoader();
  }

  @Bean
  ResponseParser responseParser() {
    return new HtmlParser();
  }

  @Bean
  Consumer<WhaleAlert> whaleAlertTransactionCrawler(TransactionLoader transactionLoader,
      AlertDetailRepository alertDetailRepository) {
    return new WhaleAlertCrawler(transactionLoader, new HtmlParser(), alertDetailRepository);
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
