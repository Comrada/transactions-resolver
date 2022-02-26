package com.github.comrada.wa.resolver.parser;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.resolver.ResponseParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.stream.Collectors.toMap;

public class HtmlParser implements ResponseParser {

    private static final Pattern PATTERN_AMOUNT = Pattern.compile("^([\\d,]*\\.?[\\d,]*)$");
    private static final Pattern PATTERN_USD_AMOUNT = Pattern.compile("\\$([\\d,]*\\.?[\\d,]*)\\sUSD");
    private static final Pattern PATTERN_TIMESTAMP =
            Pattern.compile("\\(([a-zA-Z]{3},\\s\\d{1,2}\\s[a-zA-Z]{3}\\s[\\d\\s:]+(UTC|GMT))\\)$");
    private static final String SELECTOR_DETAILS_TABLE_ROWS = "h1.color-primary ~ table.table>tbody>tr";
    private static final String SELECTOR_LINK = "a";
    private static final String SELECTOR_ADDRESS_BLOCK = "div>i>span.d-lg-block";
    private static final String SELECTOR_BOLD_TEXT = "b";
    private static final String SELECTOR_ITALIC_TEXT = "i";

    @Override
    public TransactionDetail parse(String content) {
        Document doc = Jsoup.parse(content);
        List<Element> detailRows = findTableRows(doc);
        assertTableSize(detailRows);
        Map<String, Element> values = mapRows(detailRows);
        return buildFrom(values);
    }

    private List<Element> findTableRows(Document doc) {
        return select(doc, SELECTOR_DETAILS_TABLE_ROWS).stream().toList();
    }

    private Elements select(Element element, String selector) {
        Elements elements = element.select(selector);
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Selector: '%s' does not exist anymore.".formatted(selector));
        }
        return elements;
    }

    private void assertTableSize(List<Element> detailRows) {
        if (detailRows.size() != 7) {
            throw new IllegalArgumentException("Probably page structure has been changed, number of table rows not equals 7");
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
        return new TransactionDetail(
                parseSingleStringValue(values.get("Blockchain")),
                parseSingleStringValue(values.get("Type")),
                parseAmount(values.get("Amount")),
                parseAsset(values.get("Amount")),
                parseUsdAmount(values.get("Amount")),
                parseTimestamp(values.get("Timestamp")),
                parseAddress(values.get("Hash")),
                parseBlockWithUrl(values.get("Hash")),
                parseAddress(values.get("From")),
                parseWalletName(values.get("From")),
                parseBlockWithUrl(values.get("From")),
                parseAddress(values.get("To")),
                parseWalletName(values.get("To")),
                parseBlockWithUrl(values.get("To"))
        );
    }

    private String parseSingleStringValue(Element row) {
        return row.text().trim();
    }

    private String parseBlockWithUrl(Element walletRow) {
        return select(walletRow, SELECTOR_LINK)
                .stream()
                .findFirst()
                .map(element -> element.attr("href"))
                .orElse("");
    }

    private String parseWalletName(Element walletRow) {
        return walletRow.ownText();
    }

    private String parseAddress(Element walletRow) {
        return select(walletRow, SELECTOR_ADDRESS_BLOCK).text();
    }

    private Instant parseTimestamp(Element timestampRow) {
        String rowText = timestampRow.text();
        Matcher matcher = PATTERN_TIMESTAMP.matcher(rowText);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return RFC_1123_DATE_TIME.parse(matcher.group(1).replace("UTC", "GMT"), Instant::from);
        }
        throw new IllegalArgumentException("Timestamp doesn't have right format: " + rowText);
    }

    private String parseAsset(Element amountRow) {
        return select(amountRow, SELECTOR_BOLD_TEXT).text();
    }

    private BigDecimal parseUsdAmount(Element amountRow) {
        return select(amountRow, SELECTOR_ITALIC_TEXT)
                .stream()
                .findFirst()
                .map(element -> parseAmount(element, PATTERN_USD_AMOUNT))
                .orElseThrow(() -> new IllegalArgumentException("USD Amount not found"));
    }

    private BigDecimal parseAmount(Element amountRow) {
        return parseAmount(amountRow, PATTERN_AMOUNT);
    }

    private BigDecimal parseAmount(Element amountRow, Pattern pattern) {
        String rowText = amountRow.ownText();
        Matcher matcher = pattern.matcher(rowText);
        if (matcher.find() && matcher.groupCount() == 1) {
            return BigDecimal.valueOf(parseDouble(matcher.group(1).replace(",", "").trim()));
        }
        throw new IllegalArgumentException("Amount doesn't have right format: " + rowText);
    }
}
