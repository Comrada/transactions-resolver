package com.github.comrada.wa.resolver.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.github.comrada.wa.dto.TransactionDetail;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class HtmlParserTest {

  @Test
  void parse() throws IOException {
    HtmlParser parser = new HtmlParser();
    TransactionDetail expected = new TransactionDetail(
        "Ethereum",
        "Transfer",
        BigDecimal.valueOf(2999.383),
        "PAXG",
        BigDecimal.valueOf(5392568),
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
    try (InputStream inputStream = getClass().getResourceAsStream("transaction-detail.html")) {
      String html = new String(inputStream.readAllBytes());
      TransactionDetail actual = parser.parse(html);
      assertEquals(expected, actual);
    }
  }
}
