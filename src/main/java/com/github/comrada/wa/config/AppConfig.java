package com.github.comrada.wa.config;

import com.github.comrada.wa.resolver.ResponseParser;
import com.github.comrada.wa.resolver.TransactionLoader;
import com.github.comrada.wa.resolver.http.OkHttpLoader;
import com.github.comrada.wa.resolver.parser.HtmlParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebClientProperties.class)
public class AppConfig {

  @Bean
  TransactionLoader transactionLoader(WebClientProperties webClientProperties) {
    return new OkHttpLoader(webClientProperties);
  }

  @Bean
  ResponseParser responseParser() {
    return new HtmlParser();
  }
}
