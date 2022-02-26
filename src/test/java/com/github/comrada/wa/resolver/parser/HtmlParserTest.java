package com.github.comrada.wa.resolver.parser;

import com.github.comrada.wa.dto.TransactionDetail;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HtmlParserTest {

    @Test
    void successfulParse() throws IOException {
        HtmlParser parser = new HtmlParser();
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
    void whenDetailsTableNotFound_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-wrong-table-position.html")));
    }

    @Test
    void whenDetailsTableHasNotEnoughRows_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-less-rows.html")));
    }

    @Test
    void whenDetailsTableRowDoesNotHave2Columns_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("transaction-detail-with-wrong-column-number.html")));
    }

    @Test
    void whenAmountRowHasWrongFormat_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-amount-format.html")));
    }

    @Test
    void whenUsdAmountRowHasWrongFormat_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-usd-amount-format.html")));
    }

    @Test
    void whenTimestampRowHasWrongFormat_thenParserFails() {
        HtmlParser parser = new HtmlParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(readFile("wrong-timestamp-format.html")));
    }

    private String readFile(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            return new String(inputStream.readAllBytes());
        }
    }
}
