package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlUtilsTest {


  private Element element;

  @BeforeEach
  void initDoc() throws IOException {
    element = Jsoup.parse(readFile(HtmlUtilsTest.class, "correct-transaction-detail.html"));
  }

  @Test
  void selectExistingElement() {
    Elements selected = HtmlUtils.select(element, "table.table>tbody>tr");
    assertEquals(7, selected.size());
  }

  @Test
  void selectNotExistingElement() {
    assertThrows(IllegalArgumentException.class, () -> HtmlUtils.select(element, "table.table>tr"));
  }

  @Test
  void parseUrlWithDefaultSelector() {
    Elements rows = HtmlUtils.select(element, "table.table>tbody>tr");
    Element hashRow = rows.get(4);
    String url = HtmlUtils.parseUrl(hashRow);
    assertEquals("https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9", url);
  }

  @Test
  void parseUrlWithCustomSelector() {
    String url = HtmlUtils.parseUrl(element, "a[href*='0x1da']");
    assertEquals("https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9", url);
  }
}