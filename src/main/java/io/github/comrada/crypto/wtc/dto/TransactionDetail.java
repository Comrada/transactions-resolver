package io.github.comrada.crypto.wtc.dto;

import java.time.Instant;

public record TransactionDetail(
    String blockchain,
    Instant timestamp,
    String hash,
    String transactionUrl
) {}
