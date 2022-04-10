package com.github.comrada.wa.resolver.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.resolver.parser.html.HtmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlParserTest {

    private HtmlParser parser;

    @BeforeEach
    void createParser() {
        this.parser = new HtmlParser();
    }

    @Test
    void successfulParse() throws IOException {
        TransactionDetail expected = new TransactionDetail(
                "Ethereum",
                "Transfer",
                BigDecimal.valueOf(2999.383),
                "PAXG",
                BigDecimal.valueOf(5392568.0),
                Instant.parse("2021-12-20T10:03:35Z"),
                "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
                "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
                "0x21a31ee1afc51d94c2efccaa2092ad1028285549",
                "Binance (Exchange)",
                "https://etherscan.io/address/21a31ee1afc51d94c2efccaa2092ad1028285549",
                "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
                "Unknown",
                "https://etherscan.io/address/b60c61dbb7456f024f9338c739b02be68e3f545c"
        );
        TransactionDetail actual = parser.parse(readFile("correct-transaction-detail.html"));
        assertEquals(expected, actual);
    }

    @Test
    void successfulParseWithNoUlrWallet() throws IOException {
        TransactionDetail expected = new TransactionDetail(
            "Ethereum",
            "Transfer",
            BigDecimal.valueOf(2999.383),
            "PAXG",
            BigDecimal.valueOf(5392568.0),
            Instant.parse("2021-12-20T10:03:35Z"),
            "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
            "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
            "0x21a31ee1afc51d94c2efccaa2092ad1028285549",
            "Binance (Exchange)",
            null,
            "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
            "Unknown",
            "https://etherscan.io/address/b60c61dbb7456f024f9338c739b02be68e3f545c"
        );
        TransactionDetail actual = parser.parse(readFile("correct-transaction-without-wallet-url.html"));
        assertEquals(expected, actual);
    }

    @Test
    void successfulParseWithMultipleAddress() throws IOException {
        TransactionDetail expected = new TransactionDetail(
            "Bitcoin",
            "Transfer",
            BigDecimal.valueOf(1317.9052),
            "BTC",
            BigDecimal.valueOf(6607372.0),
            Instant.parse("2019-04-04T15:19:33Z"),
            "585fc0d5e57cb8e1acd0ee918fc066bf189bb33df441ca15d5212083fd3441f4",
            "https://blockstream.info/tx/585fc0d5e57cb8e1acd0ee918fc066bf189bb33df441ca15d5212083fd3441f4",
            "Multiple Addresses",
            "Bitstamp (Exchange)",
            null,
            "3FR2r2F62Hj9Kpi78EpDs8EpvrFt4wVeUU",
            "Unknown",
            "https://blockstream.info/address/3FR2r2F62Hj9Kpi78EpDs8EpvrFt4wVeUU"
        );
        TransactionDetail actual = parser.parse(readFile("correct-transaction-with-multi-address.html"));
        assertEquals(expected, actual);
    }

    @Test
    void whenDetailsTableNotFound_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-wrong-table-position.html")));
    }

    @Test
    void whenDetailsTableHasNoTypeColumn_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-without-type.html")));
    }

    @Test
    void whenDetailsTableHasWrongRowSet_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-wrong-row-set.html")));
    }

    @Test
    void whenDetailsTableRowDoesNotHave2Columns_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-wrong-column-number.html")));
    }

    @Test
    void whenAmountRowHasWrongFormat_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-amount-format.html")));
    }

    @Test
    void whenUsdAmountRowHasWrongFormat_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-usd-amount-format.html")));
    }

    @Test
    void whenTimestampRowHasWrongFormat_thenParserFails() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-timestamp-format.html")));
    }

    private String readFile(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            return new String(inputStream.readAllBytes());
        }
    }
}
