package io.github.comrada.crypto.wtc.resolver.parser.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionResponse(
    @JsonProperty
    String result,
    @JsonProperty
    int count,
    @JsonProperty
    List<Transaction> transactions
) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Transaction(
      @JsonProperty
      String blockchain,
      @JsonProperty
      String symbol,
      @JsonProperty
      long id,
      @JsonProperty("transaction_type")
      String transactionType,
      @JsonProperty
      String hash,
      @JsonProperty
      Address from,
      @JsonProperty
      Address to,
      @JsonProperty
      Long timestamp,
      @JsonProperty
      BigDecimal amount,
      @JsonProperty("amount_usd")
      BigDecimal amountUsd,
      @JsonProperty("transaction_count")
      Integer transactionCount
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Address(
      @JsonProperty
      String address,
      @JsonProperty
      String owner,
      @JsonProperty("owner_type")
      String ownerType
  ) {}
}
