package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.TestUtils.amount;
import static com.github.comrada.crypto.wtc.TestUtils.asset;
import static com.github.comrada.crypto.wtc.TestUtils.fromName;
import static com.github.comrada.crypto.wtc.TestUtils.fromWallet;
import static com.github.comrada.crypto.wtc.TestUtils.fromWalletUrl;
import static com.github.comrada.crypto.wtc.TestUtils.readFile;
import static com.github.comrada.crypto.wtc.TestUtils.toName;
import static com.github.comrada.crypto.wtc.TestUtils.toWallet;
import static com.github.comrada.crypto.wtc.TestUtils.toWalletUrl;
import static com.github.comrada.crypto.wtc.TestUtils.transactionUrl;
import static com.github.comrada.crypto.wtc.TestUtils.type;
import static com.github.comrada.crypto.wtc.TestUtils.usdAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class EtherscanTableParserTest {

  @Test
  void parse() throws IOException {
    EtherscanTableParser parser = new EtherscanTableParser();
    String content = readFile(EtherscanTableParserTest.class, "nft-table-etherscan.html");
    TransactionDetail transactionDetail = parser.parse(content);

    assertEquals("0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664", fromWallet(transactionDetail));
    assertEquals("https://etherscan.io/address/0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664",
        fromWalletUrl(transactionDetail));
    assertEquals("0x7f268357a8c2552623316e2562d90e642bb538e5", toWallet(transactionDetail));
    assertEquals("https://etherscan.io/address/0x7f268357a8c2552623316e2562d90e642bb538e5",
        toWalletUrl(transactionDetail));
    assertNull(transactionDetail.blockchain());
    assertNull(type(transactionDetail));
    assertNull(amount(transactionDetail));
    assertNull(asset(transactionDetail));
    assertNull(usdAmount(transactionDetail));
    assertNull(transactionDetail.timestamp());
    assertNull(transactionDetail.hash());
    assertNull(transactionUrl(transactionDetail));
    assertNull(fromName(transactionDetail));
    assertNull(toName(transactionDetail));
  }

  @Test
  void parseWalletWithName() throws IOException {
    EtherscanTableParser parser = new EtherscanTableParser();
    String content = readFile(EtherscanTableParserTest.class, "nft-table-etherscan-with-named-wallet.html");
    TransactionDetail transactionDetail = parser.parse(content);

    assertEquals("0x0aaef7bbc21c627f14cad904e283e199ca2b72cc", fromWallet(transactionDetail));
    assertEquals("https://etherscan.io/address/0x0aaef7bbc21c627f14cad904e283e199ca2b72cc",
        fromWalletUrl(transactionDetail));
    assertEquals("0x7f268357a8c2552623316e2562d90e642bb538e5", toWallet(transactionDetail));
    assertEquals("https://etherscan.io/address/0x7f268357a8c2552623316e2562d90e642bb538e5",
        toWalletUrl(transactionDetail));
    assertNull(transactionDetail.blockchain());
    assertNull(type(transactionDetail));
    assertNull(amount(transactionDetail));
    assertNull(asset(transactionDetail));
    assertNull(usdAmount(transactionDetail));
    assertNull(transactionDetail.timestamp());
    assertNull(transactionDetail.hash());
    assertNull(transactionUrl(transactionDetail));
    assertNull(fromName(transactionDetail));
    assertNull(toName(transactionDetail));
  }
}