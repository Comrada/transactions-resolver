package com.github.comrada.wa.resolver.parser.html;

import static com.github.comrada.wa.resolver.parser.html.HtmlUtils.select;
import static java.lang.Double.parseDouble;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

public abstract class BaseTableParser {

  private static final Pattern PATTERN_AMOUNT = Pattern.compile("^([\\d,]*\\.?[\\d,]*)$");
  private static final Pattern PATTERN_USD_AMOUNT = Pattern.compile(
      "\\$([\\d,]*\\.?[\\d,]*)\\sUSD");
  private static final Pattern PATTERN_TIMESTAMP =
      Pattern.compile("\\(([a-zA-Z]{3},\\s\\d{1,2}\\s[a-zA-Z]{3}\\s[\\d\\s:]+(UTC|GMT))\\)$");
  private static final String SELECTOR_ADDRESS_BLOCK = "div>i>span.d-lg-block";
  private static final String SELECTOR_MULTI_ADDRESS_BLOCK = "div>i";
  private static final String SELECTOR_BOLD_TEXT = "b";
  private static final String SELECTOR_ITALIC_TEXT = "i";

  protected String parseSingleStringValue(Element row) {
    return row.text().trim();
  }

  protected String parseWalletName(Element walletRow) {
    return walletRow.ownText();
  }

  protected String parseAddress(Element walletRow) {
    try {
      return select(walletRow, SELECTOR_ADDRESS_BLOCK).text();
    } catch (IllegalArgumentException e) {
      return select(walletRow, SELECTOR_MULTI_ADDRESS_BLOCK).text();
    }
  }

  protected Instant parseTimestamp(Element timestampRow) {
    String rowText = timestampRow.text();
    Matcher matcher = PATTERN_TIMESTAMP.matcher(rowText);
    if (matcher.find() && matcher.groupCount() >= 1) {
      return RFC_1123_DATE_TIME.parse(matcher.group(1).replace("UTC", "GMT"), Instant::from);
    }
    throw new IllegalArgumentException("Timestamp doesn't have right format: " + rowText);
  }

  protected String parseAsset(Element amountRow) {
    return select(amountRow, SELECTOR_BOLD_TEXT).text();
  }

  protected BigDecimal parseUsdAmount(Element amountRow) {
    return select(amountRow, SELECTOR_ITALIC_TEXT)
        .stream()
        .findFirst()
        .map(element -> parseAmount(element, PATTERN_USD_AMOUNT))
        .orElseThrow(() -> new IllegalArgumentException("USD Amount not found"));
  }

  protected BigDecimal parseAmount(Element amountRow) {
    return parseAmount(amountRow, PATTERN_AMOUNT);
  }

  protected BigDecimal parseAmount(Element amountRow, Pattern pattern) {
    String rowText = amountRow.ownText();
    Matcher matcher = pattern.matcher(rowText);
    if (matcher.find() && matcher.groupCount() == 1) {
      return BigDecimal.valueOf(parseDouble(matcher.group(1).replace(",", "").trim()));
    }
    throw new IllegalArgumentException("Amount doesn't have right format: " + rowText);
  }
}
