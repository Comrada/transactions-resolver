package com.github.comrada.crypto.wtc;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.dto.TransactionItem;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.function.Function;

public final class TestUtils {

  private TestUtils() {
  }

  public static String readFile(Class<?> loader, String fileName) throws IOException {
    try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
      return new String(inputStream.readAllBytes());
    }
  }

  public static <T> T getFromFirst(Function<TransactionItem, T> itemGetter, TransactionDetail transaction) {
    return transaction.items().stream().findFirst().map(itemGetter).orElse(null);
  }

  public static String fromWallet(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::fromWallet, transaction);
  }

  public static String fromName(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::fromName, transaction);
  }

  public static String fromWalletUrl(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::fromWalletUrl, transaction);
  }

  public static String toWallet(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::toWallet, transaction);
  }

  public static String toName(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::toName, transaction);
  }

  public static String toWalletUrl(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::toWalletUrl, transaction);
  }

  public static String asset(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::asset, transaction);
  }

  public static String type(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::type, transaction);
  }

  public static BigDecimal amount(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::amount, transaction);
  }

  public static BigDecimal usdAmount(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::usdAmount, transaction);
  }

  public static String transactionUrl(TransactionDetail transaction) {
    return getFromFirst(TransactionItem::transactionUrl, transaction);
  }
}
