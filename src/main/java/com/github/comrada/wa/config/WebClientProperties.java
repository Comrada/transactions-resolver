package com.github.comrada.wa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@ConfigurationProperties(prefix = "app.web-client")
public class WebClientProperties {

  private Map<String, String> requestHeaders = new HashMap<>();

  public Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }

  public void setRequestHeaders(Set<HttpHeader> requestHeaders) {
    this.requestHeaders = requestHeaders.stream()
        .collect(toMap(HttpHeader::getName, HttpHeader::getValue));
  }

  public static final class HttpHeader {

    private String name;
    private String value;

    public HttpHeader() {
    }

    public HttpHeader(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public static HttpHeader of(String name, String value) {
      return new HttpHeader(name, value);
    }
  }
}
