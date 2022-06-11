package com.github.comrada.wa.resolver.parser.html;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class HtmlUtils {

  public static final String SELECTOR_LINK = "a";

  private HtmlUtils() {
  }

  public static Elements select(Element element, String selector) {
    Elements elements = element.select(selector);
    if (elements.isEmpty()) {
      throw new IllegalArgumentException(
          "Selector: '%s' does not exist anymore.".formatted(selector));
    }
    return elements;
  }

  public static String parseUrl(Element walletRow) {
    return parseUrl(walletRow, SELECTOR_LINK);
  }

  public static String parseUrl(Element walletRow, String selector) {
    try {
      return select(walletRow, selector)
          .stream()
          .findFirst()
          .map(element -> element.attr("href"))
          .orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
