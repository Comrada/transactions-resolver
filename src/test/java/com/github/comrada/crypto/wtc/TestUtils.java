package com.github.comrada.crypto.wtc;

import com.github.comrada.crypto.wtc.dto.TransactionItem;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public final class TestUtils {

  private TestUtils() {
  }

  public static String readFile(Class<?> loader, String fileName) throws IOException {
    try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
      return new String(inputStream.readAllBytes());
    }
  }

  public static <T> T getFromFirst(Function<TransactionItem, T> itemGetter, List<TransactionItem> items) {
    return items.stream().findFirst().map(itemGetter).orElse(null);
  }

  public static String fromWallet(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::fromWallet, items);
  }

  public static String fromName(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::fromName, items);
  }

  public static String toWallet(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::toWallet, items);
  }

  public static String toName(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::toName, items);
  }

  public static String asset(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::asset, items);
  }

  public static String type(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::type, items);
  }

  public static BigDecimal amount(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::amount, items);
  }

  public static BigDecimal usdAmount(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::usdAmount, items);
  }

  public static String transactionUrl(List<TransactionItem> items) {
    return getFromFirst(TransactionItem::transactionUrl, items);
  }

  public static BigDecimal bigDecimal(double val) {
    return BigDecimal.valueOf(val);
  }

  public static BigDecimal bigDecimal(long unscaledVal, int scale) {
    return BigDecimal.valueOf(unscaledVal, scale);
  }
}
