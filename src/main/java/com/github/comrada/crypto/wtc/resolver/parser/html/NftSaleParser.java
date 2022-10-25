package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.resolver.parser.html.HtmlUtils.parseUrl;
import static com.github.comrada.crypto.wtc.resolver.parser.html.HtmlUtils.select;
import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.dto.TransactionItem;
import com.github.comrada.crypto.wtc.resolver.TransactionLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.resolver.parser.TransactionTableParser;
import java.util.Map;
import java.util.Set;
import org.jsoup.nodes.Element;

public class NftSaleParser extends BaseTableParser implements TransactionTableParser {

  private static final Set<String> REQUIRED_COLUMNS = Set.of(
      "Blockchain",
      "Type",
      "Sale Price",
      "Timestamp",
      "Tx Hash",
      "NFT",
      "Title",
      "Collection",
      "Buyer"
  );
  private final TransactionLoader deepTransactionLoader;
  private final ResponseParser deepTransactionParser;

  public NftSaleParser(TransactionLoader deepTransactionLoader, ResponseParser deepTransactionParser) {
    this.deepTransactionLoader = requireNonNull(deepTransactionLoader);
    this.deepTransactionParser = requireNonNull(deepTransactionParser);
  }

  @Override
  public TransactionDetail parse(Map<String, Element> rows) {
    TransactionDetail transactionDetail = loadTransactionDetails(parseUrl(rows.get("Tx Hash")));
    return new TransactionDetail(
        parseSingleStringValue(rows.get("Blockchain")),
        parseSingleStringValue(rows.get("Type")),
        parseAmount(rows.get("Sale Price")),
        parseAsset(rows.get("Sale Price")),
        parseUsdAmount(rows.get("Sale Price")),
        parseTimestamp(rows.get("Timestamp")),
        parseAddress(rows.get("Tx Hash")),
        parseUrl(rows.get("Tx Hash")),
        transactionDetail.items().stream().findFirst().map(TransactionItem::fromWallet).orElse(null),
        parseDivText(rows.get("Collection")),
        transactionDetail.items().stream().findFirst().map(TransactionItem::fromWalletUrl).orElse(null),
        transactionDetail.items().stream().findFirst().map(TransactionItem::toWallet).orElse(null),
        parseBuyerName(rows.get("Buyer")),
        transactionDetail.items().stream().findFirst().map(TransactionItem::toWalletUrl).orElse(null)
    );
  }

  private TransactionDetail loadTransactionDetails(String deepLink) {
    String transactionPage = deepTransactionLoader.load(deepLink);
    return deepTransactionParser.parse(transactionPage);
  }

  private String parseDivText(Element row) {
    return select(row, "div").stream()
        .findFirst()
        .map(Element::ownText)
        .orElse(null);
  }

  private String parseBuyerName(Element row) {
    String buyerDivText = parseDivText(row);
    return buyerDivText != null ? buyerDivText.replaceAll("-$", "").trim() : null;
  }

  @Override
  public boolean supported(Set<String> columns) {
    return columns.size() == REQUIRED_COLUMNS.size() && REQUIRED_COLUMNS.containsAll(columns);
  }
}
