package com.github.comrada.wa.resolver.parser.html;

import static com.github.comrada.wa.resolver.parser.html.HtmlUtils.select;
import static java.util.stream.Collectors.toMap;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.resolver.parser.ResponseParser;
import com.github.comrada.wa.resolver.parser.TransactionTableParser;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlParser implements ResponseParser {

  private static final String SELECTOR_DETAILS_TABLE_ROWS = "h1.color-primary ~ table.table>tbody>tr";
  private final Map<String, TransactionTableParser> transactionTableParsers = Map.of(
      "Transfer", new TransferParser(),
      "Mint", new MintBurnParser(),
      "Burn", new MintBurnParser()
  );

  @Override
  public TransactionDetail parse(String content) {
    Document doc = Jsoup.parse(content);
    List<Element> detailRows = findTableRows(doc);
    Map<String, Element> values = mapRows(detailRows);
    assertTableContent(values);
    return buildFrom(values);
  }

  private List<Element> findTableRows(Document doc) {
    return select(doc, SELECTOR_DETAILS_TABLE_ROWS).stream().toList();
  }

  private void assertTableContent(Map<String, Element> tableRows) {
    String type = getTransactionType(tableRows);
    TransactionTableParser tableParser = getTableParserForType(type);
    if (!tableParser.supported(tableRows.keySet())) {
      throw new IllegalArgumentException(
          "Probably page structure has been changed, number of table rows not equals 6 or 7");
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
    String transactionType = getTransactionType(values);
    TransactionTableParser tableParser = getTableParserForType(transactionType);
    return tableParser.parse(values);
  }

  @NotNull
  private String getTransactionType(Map<String, Element> tableRows) {
    Element typeRow = tableRows.get("Type");
    if (typeRow == null) {
      throw new IllegalArgumentException("No 'Type' column, '%s' presented".formatted(
          String.join(",", tableRows.keySet())));
    }
    return typeRow.text().trim();
  }

  @NotNull
  private TransactionTableParser getTableParserForType(String transactionType) {
    TransactionTableParser tableParser = transactionTableParsers.get(transactionType);
    if (tableParser == null) {
      throw new IllegalArgumentException("Unknown transaction type: " + transactionType);
    }
    return tableParser;
  }
}
