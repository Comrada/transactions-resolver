package com.github.comrada.crypto.wtc;

import java.io.IOException;
import java.io.InputStream;

public final class TestUtils {

  private TestUtils() {
  }

  public static String readFile(Class<?> loader, String fileName) throws IOException {
    try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
      return new String(inputStream.readAllBytes());
    }
  }
}
