package com.github.comrada.crypto.wtc.crawler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.model.Wallet;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import com.github.comrada.crypto.wtc.resolver.TransactionLoader;
import com.github.comrada.crypto.wtc.resolver.parser.ResponseParser;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.model.WalletId;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.model.WhaleAlert.ProcessingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class WhaleAlertCrawlerIntegrationTest {

  private final TransactionDetail transactionDetailMock = mockTransactionDetail();
  private final WhaleAlert whaleAlertMock = mockAlert();
  @MockBean
  private TransactionLoader transactionLoader;
  @MockBean
  private ResponseParser responseParser;
  @Autowired
  private AlertDetailRepository alertDetailRepository;
  @Autowired
  private WalletRepository walletRepository;
  @SpyBean
  private DetailsSaver detailsSaver;
  @Autowired
  private Consumer<WhaleAlert> testCrawler;

  @BeforeEach
  void configureBeans() {
    when(transactionLoader.load(whaleAlertMock.getLink())).thenReturn("fake content");
    when(responseParser.parse("fake content")).thenReturn(transactionDetailMock);
  }

  @Test
  void happyPath() {
    testCrawler.accept(whaleAlertMock);

    verify(detailsSaver, times(1)).save(1L, transactionDetailMock);
    verify(transactionLoader, times(1)).load(whaleAlertMock.getLink());

    Optional<AlertDetail> detailRecord = alertDetailRepository.findById(1L);
    assertTrue(detailRecord.isPresent());
    AlertDetail alertDetail = detailRecord.get();

    assertEquals(whaleAlertMock.getId(), alertDetail.getId());
    assertEquals(transactionDetailMock.blockchain(), alertDetail.getBlockchain());
    assertEquals(transactionDetailMock.type(), alertDetail.getType());
    assertEquals(transactionDetailMock.amount(), alertDetail.getAmount());
    assertEquals(transactionDetailMock.asset(), alertDetail.getAsset());
    assertEquals(transactionDetailMock.usdAmount(), alertDetail.getUsdAmount());
    assertEquals(transactionDetailMock.timestamp(), alertDetail.getTimestamp());
    assertEquals(transactionDetailMock.hash(), alertDetail.getHash());
    assertEquals(transactionDetailMock.transactionUrl(), alertDetail.getTransactionUrl());
    assertEquals(transactionDetailMock.fromWallet(), alertDetail.getFromWallet());
    assertEquals(transactionDetailMock.fromName(), alertDetail.getFromName());
    assertEquals(transactionDetailMock.fromWalletUrl(), alertDetail.getFromWalletUrl());
    assertEquals(transactionDetailMock.toWallet(), alertDetail.getToWallet());
    assertEquals(transactionDetailMock.toName(), alertDetail.getToName());
    assertEquals(transactionDetailMock.toWalletUrl(), alertDetail.getToWalletUrl());

    WalletId fromWalletId = WalletId.builder()
        .blockchain(transactionDetailMock.blockchain())
        .address(transactionDetailMock.fromWallet())
        .asset(transactionDetailMock.asset())
        .build();
    Optional<Wallet> fromWalletRecord = walletRepository.findById(fromWalletId);
    assertTrue(fromWalletRecord.isPresent());
    WalletId toWalletId = WalletId.builder()
        .blockchain(transactionDetailMock.blockchain())
        .address(transactionDetailMock.toWallet())
        .asset(transactionDetailMock.asset())
        .build();
    Optional<Wallet> toWalletRecord = walletRepository.findById(toWalletId);
    assertTrue(toWalletRecord.isPresent());

    Wallet fromWallet = fromWalletRecord.get();
    Wallet toWallet = toWalletRecord.get();

    assertEquals(transactionDetailMock.fromWallet(), fromWallet.getId().getAddress());
    assertTrue(fromWallet.isExchange());

    assertEquals(transactionDetailMock.toWallet(), toWallet.getId().getAddress());
    assertFalse(toWallet.isExchange());
  }

  private WhaleAlert mockAlert() {
    WhaleAlert alert = new WhaleAlert();
    alert.setId(1L);
    alert.setAmount(transactionDetailMock.amount());
    alert.setMessage("fake message");
    alert.setLink("https://fakeurl.com");
    alert.setAsset(transactionDetailMock.asset());
    alert.setProcessStatus(ProcessingStatus.NEW);
    alert.setPostedAt(transactionDetailMock.timestamp());
    return alert;
  }

  private TransactionDetail mockTransactionDetail() {
    return new TransactionDetail(
        "Ethereum",
        "Transfer",
        BigDecimal.valueOf(2999.38),
        "PAXG",
        BigDecimal.valueOf(5392568, 2),
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "0x21a31ee1afc51d94c2efccaa2092ad1028285549",
        "Binance (Exchange)",
        "https://etherscan.io/address/21a31ee1afc51d94c2efccaa2092ad1028285549",
        "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
        "Unknown",
        "https://etherscan.io/address/b60c61dbb7456f024f9338c739b02be68e3f545c"
    );
  }
}