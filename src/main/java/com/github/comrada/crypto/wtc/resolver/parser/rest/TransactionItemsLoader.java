package com.github.comrada.crypto.wtc.resolver.parser.rest;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.dto.TransactionItem;
import com.github.comrada.crypto.wtc.resolver.HttpClient;
import com.github.comrada.crypto.wtc.resolver.parser.rest.StatusResponse.Blockchain;
import com.github.comrada.crypto.wtc.resolver.parser.rest.TransactionResponse.Address;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionItemsLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionItemsLoader.class);

  private static final String TRANSACTION_API_URI = "https://api.whale-alert.io/v1/transaction/%s/%s";
  private static final String STATUS_API_URI = "https://api.whale-alert.io/v1/status";
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;
  private final Map<String, Set<String>> supportedBlockchains = new ConcurrentHashMap<>();

  public TransactionItemsLoader(ObjectMapper objectMapper, HttpClient httpClient) {
    this.objectMapper = requireNonNull(objectMapper);
    this.httpClient = requireNonNull(httpClient);
    updateStatus();
  }

  public List<TransactionItem> load(TransactionDetail detail) {
    try {
      String content = httpClient.load(TRANSACTION_API_URI.formatted(detail.blockchain(), detail.hash()));
      TransactionResponse transactionResponse = objectMapper.readValue(content, TransactionResponse.class);
      return Optional.ofNullable(transactionResponse.transactions())
          .map(transactions -> transactions.stream()
              .map(transaction -> new TransactionItem(
                  transaction.transactionType(),
                  transaction.amount(),
                  transaction.symbol(),
                  transaction.amountUsd(),
                  detail.transactionUrl(),
                  transaction.from().address(),
                  mapName(transaction.from()),
                  transaction.to().address(),
                  mapName(transaction.to())
              ))
              .toList())
          .orElse(emptyList());
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public boolean supports(String blockchain) {
    if (supportedBlockchains.isEmpty()) {
      return true;
    }
    return supportedBlockchains.get(blockchain.toLowerCase()) != null;
  }

  private String mapName(Address address) {
    if (Objects.equals("unknown", address.ownerType())) {
      return "unknown";
    }
    return Objects.equals("exchange", address.ownerType()) ? address.owner() + " (Exchange)" : address.owner();
  }

  public void updateStatus() {
    supportedBlockchains.clear();
    try {
      String content = httpClient.load(STATUS_API_URI);
      StatusResponse response = objectMapper.readValue(content, StatusResponse.class);
      supportedBlockchains.putAll(response.blockchains().stream()
          .filter(blockchain -> blockchain.status().equals("connected"))
          .collect(toMap(Blockchain::name, Blockchain::symbols)));
    } catch (Exception e) {
      LOGGER.error("Status update has failed. A transaction request will be made for all alerts.");
    }
  }
}
