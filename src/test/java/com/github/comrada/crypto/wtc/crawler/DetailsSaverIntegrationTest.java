package com.github.comrada.crypto.wtc.crawler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
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
    TransactionDetail dto = mockTransactionDetailWithNativeAsset();
    AlertDetail alertDetail = mockAlertDetail(dto);
    detailsSaver.save(1L, dto);

    verify(alertRepository, times(1)).save(alertDetail);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.toWallet(), dto.asset(), true, false);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.fromWallet(), dto.asset(), false, false);
  }

  @Test
  void whenWalletHasMultipleAddresses_thenItIsNotSaved() {
    TransactionDetail dto = mockMultiAddressTransactionDetail();
    AlertDetail alertDetail = mockAlertDetail(dto);
    detailsSaver.save(1L, dto);

    verify(alertRepository, times(1)).save(alertDetail);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.toWallet(), dto.asset(), false, false);
    verify(walletRepository, never()).addWallet(dto.blockchain(), dto.fromWallet(), dto.asset(), true, false);
  }

  @Test
  void whenNotNativeAssetIsUsed_thenWalletSavedAsToken() {
    TransactionDetail dto = mockTransactionDetailWithNotNativeAsset();
    AlertDetail alertDetail = mockAlertDetail(dto);
    detailsSaver.save(1L, dto);

    verify(alertRepository, times(1)).save(alertDetail);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.toWallet(), dto.asset(), false, true);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), dto.fromWallet(), dto.asset(), true, true);
  }

  private TransactionDetail mockTransactionDetailWithNotNativeAsset() {
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

  private TransactionDetail mockTransactionDetailWithNativeAsset() {
    return new TransactionDetail(
        "Ethereum",
        "Transfer",
        BigDecimal.valueOf(50409.99),
        "ETH",
        BigDecimal.valueOf(79159430, 2),
        Instant.parse("2022-07-21T22:35:07Z"),
        "0x9b0e644ef0fe67b6db4af88b15922c5964c0a7a0fb3d0a7cb108ee0ff18a8094",
        "https://etherscan.io/tx/0x9b0e644ef0fe67b6db4af88b15922c5964c0a7a0fb3d0a7cb108ee0ff18a8094",
        "0x366064cc2baa69ff0bb0dd7dd07cb266e5105759",
        "Unknown",
        "https://etherscan.io/address/366064cc2baa69ff0bb0dd7dd07cb266e5105759",
        "0xc098b2a3aa256d2140208c3de6543aaef5cd3a94",
        "Ftx (Exchange)",
        "https://etherscan.io/address/c098b2a3aa256d2140208c3de6543aaef5cd3a94"
    );
  }

  private TransactionDetail mockMultiAddressTransactionDetail() {
    return new TransactionDetail(
        "Ethereum",
        "Transfer",
        BigDecimal.valueOf(2999.38),
        "ETH",
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