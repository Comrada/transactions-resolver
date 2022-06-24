package com.github.comrada.crypto.wtc.crawler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DetailsSaverIntegrationTest {

  @Mock
  private AlertDetailRepository alertRepository;
  @Mock
  private WalletRepository walletRepository;
  private DetailsSaver detailsSaver;

  @BeforeEach
  void configureBeans() {
    detailsSaver = new DetailsSaver(alertRepository, walletRepository);
  }

  @Test
  void save() {
    TransactionDetail dto = mockTransactionDetail();
    AlertDetail alertDetail = mockAlertDetail(dto);
    detailsSaver.save(1L, dto);

    verify(alertRepository, times(1)).save(alertDetail);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.toWallet(), false);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.fromWallet(), true);
  }

  @Test
  void whenWalletHasMultipleAddresses_thenItIsNotSaved() {
    TransactionDetail dto = mockMultiAddressTransactionDetail();
    AlertDetail alertDetail = mockAlertDetail(dto);
    detailsSaver.save(1L, dto);

    verify(alertRepository, times(1)).save(alertDetail);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.toWallet(), false);
    verify(walletRepository, never()).addWallet(dto.blockchain(), dto.fromWallet(), true);
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

  private TransactionDetail mockMultiAddressTransactionDetail() {
    return new TransactionDetail(
        "Ethereum",
        "Transfer",
        BigDecimal.valueOf(2999.38),
        "PAXG",
        BigDecimal.valueOf(5392568, 2),
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "Multiple Addresses",
        "Coinbase (Exchange)",
        "https://etherscan.io/address/21a31ee1afc51d94c2efccaa2092ad1028285549",
        "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
        "Unknown",
        "https://etherscan.io/address/b60c61dbb7456f024f9338c739b02be68e3f545c"
    );
  }

  private AlertDetail mockAlertDetail(TransactionDetail dto) {
    AlertDetail entity = new AlertDetail();
    entity.setId(1L);
    entity.setBlockchain(dto.blockchain());
    entity.setType(dto.type());
    entity.setAmount(dto.amount());
    entity.setAsset(dto.asset() != null ? dto.asset().toUpperCase() : null);
    entity.setUsdAmount(dto.usdAmount());
    entity.setTimestamp(dto.timestamp());
    entity.setHash(dto.hash());
    entity.setTransactionUrl(dto.transactionUrl());
    entity.setFromWallet(dto.fromWallet());
    entity.setFromName(dto.fromName());
    entity.setFromWalletUrl(dto.fromWalletUrl());
    entity.setToWallet(dto.toWallet());
    entity.setToName(dto.toName());
    entity.setToWalletUrl(dto.toWalletUrl());
    return entity;
  }
}