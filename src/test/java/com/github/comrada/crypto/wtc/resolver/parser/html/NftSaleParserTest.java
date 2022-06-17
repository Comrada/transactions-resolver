package com.github.comrada.crypto.wtc.resolver.parser.html;

import static com.github.comrada.crypto.wtc.TestUtils.readFile;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.resolver.TransactionLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NftSaleParserTest {

  @Test
  void parse() throws IOException {
    TransactionLoader loader = mock(TransactionLoader.class);
    when(
        loader.load("https://etherscan.io/tx/0xf7f238e43636bea0d4a6bc024a038a0ff5245ca4e8a450bc33769b0c91d0ec38")
    )
        .thenReturn(readFile(NftSaleParserTest.class, "nft-table-etherscan.html"));
    HtmlParser parser = new HtmlParser(singletonMap(
        "NFT Sale", new NftSaleParser(loader, new EtherscanTableParser())
    ));
    String tableContent = readFile(NftSaleParserTest.class, "correct-nft-sale-table.html");
    TransactionDetail transactionDetail = parser.parse(tableContent);

    assertEquals("Ethereum", transactionDetail.blockchain());
    assertEquals("NFT Sale", transactionDetail.type());
    assertEquals(BigDecimal.valueOf(420.7), transactionDetail.amount());
    assertEquals("ETH", transactionDetail.asset());
    assertEquals(BigDecimal.valueOf(1_424_410.0), transactionDetail.usdAmount());
    assertEquals(Instant.parse("2022-03-30T04:16:40Z"), transactionDetail.timestamp());
    assertEquals("0xf7f238e43636bea0d4a6bc024a038a0ff5245ca4e8a450bc33769b0c91d0ec38", transactionDetail.hash());
    assertEquals("https://etherscan.io/tx/0xf7f238e43636bea0d4a6bc024a038a0ff5245ca4e8a450bc33769b0c91d0ec38",
        transactionDetail.transactionUrl());
    assertEquals("0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664", transactionDetail.fromWallet());
    assertEquals("Azuki", transactionDetail.fromName());
    assertEquals("https://etherscan.io/address/0x6db81d551cc1d1dca0ebff2c4eb215ba112e8664",
        transactionDetail.fromWalletUrl());
    assertEquals("0x7f268357a8c2552623316e2562d90e642bb538e5", transactionDetail.toWallet());
    assertEquals("jdizzles", transactionDetail.toName());
    assertEquals("https://etherscan.io/address/0x7f268357a8c2552623316e2562d90e642bb538e5",
        transactionDetail.toWalletUrl());
  }

  @Test
  void supported() {
    TransactionLoader loader = mock(TransactionLoader.class);
    ResponseParser deepParser = mock(ResponseParser.class);
    NftSaleParser parser = new NftSaleParser(loader, deepParser);

    assertTrue(parser.supported(Set.of(
        "Blockchain",
        "Type",
        "Sale Price",
        "Timestamp",
        "Tx Hash",
        "NFT",
        "Title",
        "Collection",
        "Buyer"
    )));
  }

  @Test
  void notSupported() {
    TransactionLoader loader = mock(TransactionLoader.class);
    ResponseParser deepParser = mock(ResponseParser.class);
    NftSaleParser parser = new NftSaleParser(loader, deepParser);

    assertFalse(parser.supported(Set.of(
        "Blockchain",
        "Type",
        "Sale Price",
        "Timestamp",
        "Tx Hash",
        "NFT",
        "Title",
        "Buyer"
    )));

    assertFalse(parser.supported(Set.of(
        "Blockchain",
        "Type",
        "Sale Price",
        "Timestamp",
        "Hash",
        "NFT",
        "Title",
        "Collection",
        "Buyer"
    )));

    assertFalse(parser.supported(Set.of(
        "Blockchain",
        "Type",
        "Sale Price",
        "Timestamp",
        "Tx Hash",
        "NFT",
        "Title",
        "Collection",
        "Buyer",
        "Seller"
    )));
  }
}