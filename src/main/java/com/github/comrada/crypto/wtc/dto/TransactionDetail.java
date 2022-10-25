package com.github.comrada.crypto.wtc.dto;

import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TransactionDetail(
    String blockchain,
    Instant timestamp,
    String hash,
    List<TransactionItem> items
) {

  public TransactionDetail(String blockchain, String type, BigDecimal amount, String asset, BigDecimal usdAmount,
      Instant timestamp, String hash, String transactionUrl, String fromWallet, String fromName, String fromWalletUrl,
      String toWallet, String toName, String toWalletUrl) {
    this(blockchain, timestamp, hash, singletonList(
        new TransactionItem(type, amount, asset, usdAmount, transactionUrl, fromWallet, fromName, fromWalletUrl, toWallet,
            toName, toWalletUrl)));
  }
}
