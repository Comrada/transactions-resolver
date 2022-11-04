package io.github.comrada.crypto.wtc.crawler;

import java.util.Map;
import java.util.Optional;

public final class TokenDeterminer {

  private final Map<String, String> nativeAssets = Map.of(
      "Bitcoin", "BTC",
      "Ethereum", "ETH",
      "Tron", "TRX",
      "EOS", "EOS",
      "Neo", "NEO"
  );

  public boolean isToken(String blockchain, String asset) {
    return Optional.ofNullable(nativeAssets.get(blockchain))
        .map(nativeAsset -> !asset.equals(nativeAsset))
        .orElse(false);
  }
}
