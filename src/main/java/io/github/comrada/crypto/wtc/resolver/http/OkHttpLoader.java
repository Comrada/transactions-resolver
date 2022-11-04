package io.github.comrada.crypto.wtc.resolver.http;

import io.github.comrada.crypto.wtc.resolver.HttpClient;
import java.io.IOException;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.brotli.BrotliInterceptor;

public class OkHttpLoader implements HttpClient {

  private final Headers headers;
  private final OkHttpClient client;

  public OkHttpLoader() {
    this(buildRequestHeaders());
  }

  public OkHttpLoader(Map<String, String> additionalHeaders) {
    headers = Headers.of(additionalHeaders);
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
      if (response.isSuccessful()) {
        return response.body().string();
      } else if (response.code() == 404) {
        throw new ResourceNotFoundException("Page %s not found".formatted(transactionUrl));
      }
      throw new UncheckedHttpException("Unexpected HTTP status %d for %s".formatted(response.code(), transactionUrl));
    } catch (IOException e) {
      throw new UncheckedHttpException("Requesting of %s failed".formatted(transactionUrl), e);
    }
  }

  private Request buildRequest(String url) {
    return new Request.Builder()
        .url(url)
        .headers(headers)
        .get()
        .build();
  }

  private static Map<String, String> buildRequestHeaders() {
    return Map.of(
        "Cache-Control", "no-cache",
        "Pragma", "no-cache",
        "Sec-Fetch-Dest", "document",
        "Sec-Fetch-Mode", "navigate",
        "Sec-Fetch-Site", "none",
        "Sec-Fetch-User", "?1",
        "Sec-GPC", "1",
        "upgrade-insecure-requests", "1",
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36"
    );
  }
}
