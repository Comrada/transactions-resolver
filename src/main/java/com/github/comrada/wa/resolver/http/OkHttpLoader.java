package com.github.comrada.wa.resolver.http;

import com.github.comrada.wa.config.WebClientProperties;
import com.github.comrada.wa.resolver.TransactionLoader;
import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;

import java.io.IOException;
import java.io.UncheckedIOException;

public class OkHttpLoader implements TransactionLoader {

  private final Headers headers;
  private final OkHttpClient client;

  public OkHttpLoader(WebClientProperties webClientProperties) {
    headers = Headers.of(webClientProperties.getRequestHeaders());
    client = new OkHttpClient.Builder()
        .followRedirects(true)
        .retryOnConnectionFailure(true)
        .addInterceptor(BrotliInterceptor.INSTANCE)
        .build();
  }

  @Override
  public String load(String transactionUrl) {
    Request request = buildRequest(transactionUrl);
    try (Response response = client.newCall(request).execute()) {
      ResponseBody body = response.body();
      if (body != null) {
        return body.string();
      }
      throw new IllegalStateException("Response body is null");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Request buildRequest(String url) {
    return new Request.Builder()
        .url(url)
        .headers(headers)
        .get()
        .build();
  }
}
