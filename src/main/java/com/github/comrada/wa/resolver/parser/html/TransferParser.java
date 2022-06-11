package com.github.comrada.wa.resolver.parser.html;

import static com.github.comrada.wa.resolver.parser.html.HtmlUtils.parseUrl;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.resolver.parser.TransactionTableParser;
import java.util.Map;
import java.util.Set;
import org.jsoup.nodes.Element;

public class TransferParser extends BaseTableParser implements TransactionTableParser {

  private static final Set<String> REQUIRED_COLUMNS = Set.of(
      "Blockchain",
      "Type",
      "Amount",
      "Timestamp",
      "Hash",
      "From",
      "To"
  );

  @Override
  public TransactionDetail parse(Map<String, Element> rows) {
    return new TransactionDetail(
        parseSingleStringValue(rows.get("Blockchain")),
        parseSingleStringValue(rows.get("Type")),
        parseAmount(rows.get("Amount")),
        parseAsset(rows.get("Amount")),
        parseUsdAmount(rows.get("Amount")),
        parseTimestamp(rows.get("Timestamp")),
        parseAddress(rows.get("Hash")),
        parseUrl(rows.get("Hash")),
        parseAddress(rows.get("From")),
        parseWalletName(rows.get("From")),
        parseUrl(rows.get("From")),
        parseAddress(rows.get("To")),
        parseWalletName(rows.get("To")),
        parseUrl(rows.get("To"))
    );
  }

  @Override
  public boolean supported(Set<String> columns) {
    return columns.size() == REQUIRED_COLUMNS.size() && REQUIRED_COLUMNS.containsAll(columns);
  }
}
