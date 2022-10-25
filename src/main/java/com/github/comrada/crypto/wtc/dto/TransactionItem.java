package com.github.comrada.crypto.wtc.dto;

import java.math.BigDecimal;

public record TransactionItem(
    String type,
    BigDecimal amount,
    String asset,
    BigDecimal usdAmount,
    String transactionUrl,
    String fromWallet,
    String fromName,
    String fromWalletUrl,
    String toWallet,
    String toName,
    String toWalletUrl
) {}
