package io.github.comrada.crypto.wtc.resolver.parser.html;

import static io.github.comrada.crypto.wtc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.io.IOException;
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
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9"
    );
    TransactionDetail actual = parser.parse(readFile(HtmlParserTest.class, "correct-transaction-detail.html"));
    assertEquals(expected, actual);
  }

  @Test
  void successfulParseWithNoUlrWallet() throws IOException {
    TransactionDetail expected = new TransactionDetail(
        "Ethereum",
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9"
    );
    TransactionDetail actual = parser.parse(
        readFile(HtmlParserTest.class, "correct-transaction-without-wallet-url.html"));
    assertEquals(expected, actual);
  }

  @Test
  void successfulParseWithMultipleAddress() throws IOException {
    TransactionDetail expected = new TransactionDetail(
        "Bitcoin",
        Instant.parse("2019-04-04T15:19:33Z"),
        "585fc0d5e57cb8e1acd0ee918fc066bf189bb33df441ca15d5212083fd3441f4",
        "https://blockstream.info/tx/585fc0d5e57cb8e1acd0ee918fc066bf189bb33df441ca15d5212083fd3441f4"
    );
    TransactionDetail actual = parser.parse(
        readFile(HtmlParserTest.class, "correct-transaction-with-multi-address.html"));
    assertEquals(expected, actual);
  }

  @Test
  void whenDetailsTableNotFound_thenParserFails() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> parser.parse(readFile(HtmlParserTest.class, "transaction-detail-with-wrong-table-position.html")));
    assertEquals("Selector: 'h1.color-primary ~ table.table' does not exist anymore.", exception.getMessage());
  }

  @Test
  void whenDetailsTableHasWrongRowSet_thenParserFails() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> parser.parse(readFile(HtmlParserTest.class, "transaction-detail-with-wrong-row-set.html")));
    assertEquals("Probably page structure has been changed, 'Blockchain', 'Hash' or 'Timestamp' columns not found",
        exception.getMessage());
  }

  @Test
  void whenDetailsTableRowDoesNotHave2Columns_thenParserFails() {
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(readFile(HtmlParserTest.class, "transaction-detail-with-wrong-column-number.html")));
  }

  @Test
  void whenTimestampRowHasWrongFormat_thenParserFails() {
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(readFile(HtmlParserTest.class, "wrong-timestamp-format.html")));
  }
}
