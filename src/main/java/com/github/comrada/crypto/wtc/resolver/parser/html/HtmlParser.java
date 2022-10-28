package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.resolver.parser.html.HtmlUtils.parseUrl;
import static com.github.comrada.crypto.wtc.resolver.parser.html.HtmlUtils.select;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlParser implements ResponseParser {

  private static final String SELECTOR_DETAILS_TABLE_ROWS = "h1.color-primary ~ table.table";
  private static final String ADDRESS_BLOCK_V1 = "div>i>span.d-lg-block";
  private static final String ADDRESS_BLOCK_V2 = "a>span.d-lg-block";
  private static final Pattern PATTERN_TIMESTAMP =
      Pattern.compile("\\(([a-zA-Z]{3},\\s\\d{1,2}\\s[a-zA-Z]{3}\\s[\\d\\s:]+(UTC|GMT))\\)$");

  @Override
  public TransactionDetail parse(String content) {
    Document doc = Jsoup.parse(content);
    List<Element> detailRows = findTableRows(doc);
    Map<String, Element> values = mapRows(detailRows);
    assertTableContent(values);
    return buildFrom(values);
  }

  private List<Element> findTableRows(Document doc) {
    Element table = select(doc, SELECTOR_DETAILS_TABLE_ROWS).stream().findFirst().orElseThrow();
    return select(table, "tbody>tr").stream().toList();
  }

  private void assertTableContent(Map<String, Element> tableRows) {
    Element blockchain = tableRows.get("Blockchain");
    Element hash = tableRows.get("Hash");
    Element timestamp = tableRows.get("Timestamp");
    if (isNull(blockchain) || isNull(hash) || isNull(timestamp)) {
      throw new IllegalArgumentException(
          "Probably page structure has been changed, 'Blockchain', 'Hash' or 'Timestamp' columns not found");
    }
  }

  private Map<String, Element> mapRows(List<Element> detailRows) {
    return detailRows
        .stream()
        .map(this::rowToArray)
        .collect(toMap(tds -> cleanTitleColumn(tds[0]), tds -> tds[1]));
  }

  private Element[] rowToArray(Element tr) {
    Element[] columns = tr.select("td").toArray(Element[]::new);
    assertColumnsNumber(columns);
    return columns;
  }

  private String cleanTitleColumn(Element td) {
    return td.text().replace("(<b>|</b>)", "").trim();
  }

  private void assertColumnsNumber(Element[] columns) {
    if (columns.length != 2) {
      throw new IllegalArgumentException(
          "Probably page structure has been changed, number of table row columns not equals 2");
    }
  }

  private TransactionDetail buildFrom(Map<String, Element> values) {
    String blockchain = parseSingleStringValue(values.get("Blockchain"));
    String hash = parseAddress(values.get("Hash"));
    String transactionUrl = parseUrl(values.get("Hash"));
    Instant timestamp = parseTimestamp(values.get("Timestamp"));
    return new TransactionDetail(blockchain, timestamp, hash, transactionUrl);
  }

  private String parseSingleStringValue(Element row) {
    return row.text().trim();
  }

  private String parseAddress(Element walletRow) {
    try {
      return select(walletRow, ADDRESS_BLOCK_V1).text();
    } catch (IllegalArgumentException e) {
      return select(walletRow, ADDRESS_BLOCK_V2).stream().findFirst().orElseThrow().text();
    }
  }

  private Instant parseTimestamp(Element timestampRow) {
    String rowText = timestampRow.text();
    Matcher matcher = PATTERN_TIMESTAMP.matcher(rowText);
    if (matcher.find() && matcher.groupCount() >= 1) {
      return RFC_1123_DATE_TIME.parse(matcher.group(1).replace("UTC", "GMT"), Instant::from);
    }
    throw new IllegalArgumentException("Timestamp doesn't have right format: " + rowText);
  }
}
