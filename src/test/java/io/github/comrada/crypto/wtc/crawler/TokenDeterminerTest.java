package io.github.comrada.crypto.wtc.crawler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TokenDeterminerTest {

  private final TokenDeterminer tokenDeterminer = new TokenDeterminer();

  @ParameterizedTest
  @CsvSource({"Bitcoin,BTC", "Ethereum,ETH", "Tron,TRX", "EOS,EOS", "Neo,NEO", "NewBlockchain,USDC"})
  void isNotToken(String blockchain, String asset) {
    assertFalse(tokenDeterminer.isToken(blockchain, asset));
  }

  @ParameterizedTest
  @CsvSource({"Bitcoin,USDT", "Ethereum,USDT", "Tron,BTT", "EOS,LEO", "Neo,GAS"})
  void isToken(String blockchain, String asset) {
    assertTrue(tokenDeterminer.isToken(blockchain, asset));
  }
}
