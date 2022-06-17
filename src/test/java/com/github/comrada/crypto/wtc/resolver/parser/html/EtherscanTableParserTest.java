package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.*;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class EtherscanTableParserTest {

  @Test
  void parse() throws IOException {
    EtherscanTableParser parser = new EtherscanTableParser();
    String content = readFile(EtherscanTableParserTest.class, "nft-table-etherscan.html");
    TransactionDetail transactionDetail = parser.parse(content);

    assertEquals("0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664", transactionDetail.fromWallet());
    assertEquals("https://etherscan.io/address/0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664",
        transactionDetail.fromWalletUrl());
    assertEquals("0x7f268357a8c2552623316e2562d90e642bb538e5", transactionDetail.toWallet());
    assertEquals("https://etherscan.io/address/0x7f268357a8c2552623316e2562d90e642bb538e5",
        transactionDetail.toWalletUrl());
    assertNull(transactionDetail.blockchain());
    assertNull(transactionDetail.type());
    assertNull(transactionDetail.amount());
    assertNull(transactionDetail.asset());
    assertNull(transactionDetail.usdAmount());
    assertNull(transactionDetail.timestamp());
    assertNull(transactionDetail.hash());
    assertNull(transactionDetail.transactionUrl());
    assertNull(transactionDetail.fromName());
    assertNull(transactionDetail.toName());
  }

  @Test
  void parseWalletWithName() throws IOException {
    EtherscanTableParser parser = new EtherscanTableParser();
    String content = readFile(EtherscanTableParserTest.class, "nft-table-etherscan-with-named-wallet.html");
    TransactionDetail transactionDetail = parser.parse(content);

    assertEquals("0x0aaef7bbc21c627f14cad904e283e199ca2b72cc", transactionDetail.fromWallet());
    assertEquals("https://etherscan.io/address/0x0aaef7bbc21c627f14cad904e283e199ca2b72cc",
        transactionDetail.fromWalletUrl());
    assertEquals("0x7f268357a8c2552623316e2562d90e642bb538e5", transactionDetail.toWallet());
    assertEquals("https://etherscan.io/address/0x7f268357a8c2552623316e2562d90e642bb538e5",
        transactionDetail.toWalletUrl());
    assertNull(transactionDetail.blockchain());
    assertNull(transactionDetail.type());
    assertNull(transactionDetail.amount());
    assertNull(transactionDetail.asset());
    assertNull(transactionDetail.usdAmount());
    assertNull(transactionDetail.timestamp());
    assertNull(transactionDetail.hash());
    assertNull(transactionDetail.transactionUrl());
    assertNull(transactionDetail.fromName());
    assertNull(transactionDetail.toName());
  }
}