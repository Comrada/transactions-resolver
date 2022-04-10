package com.github.comrada.wa.resolver.parser.html;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class HtmlUtils {

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
}
