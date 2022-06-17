package com.github.comrada.crypto.wtc.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDetail(
    String blockchain,
    String type,
    BigDecimal amount,
    String asset,
    BigDecimal usdAmount,
    Instant timestamp,
    String hash,
    String transactionUrl,
    String fromWallet,
    String fromName,
    String fromWalletUrl,
    String toWallet,
    String toName,
    String toWalletUrl
) {

}
