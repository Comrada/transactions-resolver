package io.github.comrada.crypto.wtc.resolver.parser.rest;

import static io.github.comrada.crypto.wtc.TestUtils.bigDecimal;
import static io.github.comrada.crypto.wtc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.comrada.crypto.wtc.dto.TransactionDetail;
import io.github.comrada.crypto.wtc.dto.TransactionItem;
import io.github.comrada.crypto.wtc.resolver.HttpClient;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionItemsLoaderIntegrationTest {

  private HttpClient httpClient;
  private TransactionItemsLoader transactionItemsLoader;
  private final TransactionDetail detail = new TransactionDetail("bitcoin", Instant.now(),
      "03b4746f370705800cc561ca74f677f0cb017318bd1f1aad21fac4485cf01267",
      "https://whale-alert.io/transaction/bitcoin/03b4746f370705800cc561ca74f677f0cb017318bd1f1aad21fac4485cf01267");

  @BeforeEach
  void initLoader() throws IOException {
    httpClient = mock(HttpClient.class);
    String status = readFile(getClass(), "status.json");
    when(httpClient.load("https://api.whale-alert.io/v1/status")).thenReturn(status);
    transactionItemsLoader = new TransactionItemsLoader(new ObjectMapper(), httpClient);
  }

  @Test
  void load() throws IOException {
    String transaction = readFile(getClass(), "transaction.json");
    when(httpClient.load("https://api.whale-alert.io/v1/transaction/%s/%s".formatted(detail.blockchain(), detail.hash())))
        .thenReturn(transaction);
    List<TransactionItem> items = transactionItemsLoader.load(detail);
    TransactionItem expected = new TransactionItem("transfer", bigDecimal(3148.3154), "btc", bigDecimal(66922852),
        "https://whale-alert.io/transaction/bitcoin/03b4746f370705800cc561ca74f677f0cb017318bd1f1aad21fac4485cf01267",
        "Multiple Addresses", "unknown", "3Gw4km16kPzQCAvJTqiP9tZTtFzuUVLrgx", "unknown");

    assertEquals(1, items.size());
    assertEquals(expected, items.get(0));
  }

  @Test
  void whenEthereumAddressDoesNotStartWith0x_thenAdded() throws IOException {
    TransactionDetail detail = new TransactionDetail("ethereum", Instant.now(),
        "0x32dff5620f147d94d4c7e3db197d9d18e8ce067d137809886d6c9c3445868118",
        "https://whale-alert.io/transaction/ethereum/0x32dff5620f147d94d4c7e3db197d9d18e8ce067d137809886d6c9c3445868118");
    String transaction = readFile(getClass(), "ethereum-transaction.json");
    when(httpClient.load("https://api.whale-alert.io/v1/transaction/%s/%s".formatted(detail.blockchain(), detail.hash())))
        .thenReturn(transaction);
    List<TransactionItem> items = transactionItemsLoader.load(detail);
    TransactionItem expected = new TransactionItem("transfer", bigDecimal(147161000, 0), "usdt", bigDecimal(146768320, 0),
        "https://whale-alert.io/transaction/ethereum/0x32dff5620f147d94d4c7e3db197d9d18e8ce067d137809886d6c9c3445868118",
        "0x5754284f345afc66a98fbb0a0afe71e0f007b949", "unknown", "0x53d2e40b2d24ac36bc4be85d8b677248c1c4c9bb", "Binance (Exchange)");

    assertEquals(1, items.size());
    assertEquals(expected, items.get(0));
  }

  @Test
  void supports() {
    assertFalse(transactionItemsLoader.supports("Litecoin"));
    assertFalse(transactionItemsLoader.supports("Dogecoin"));
    assertTrue(transactionItemsLoader.supports("Ripple"));
    assertTrue(transactionItemsLoader.supports("Bitcoin"));

  }

  @Test
  void updateStatus() {
    verify(httpClient, times(1)).load("https://api.whale-alert.io/v1/status");
    transactionItemsLoader.updateStatus();
    verify(httpClient, times(2)).load("https://api.whale-alert.io/v1/status");
  }
}