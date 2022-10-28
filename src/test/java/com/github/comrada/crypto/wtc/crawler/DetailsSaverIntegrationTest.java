package com.github.comrada.crypto.wtc.crawler;

import static com.github.comrada.crypto.wtc.TestUtils.amount;
import static com.github.comrada.crypto.wtc.TestUtils.asset;
import static com.github.comrada.crypto.wtc.TestUtils.bigDecimal;
import static com.github.comrada.crypto.wtc.TestUtils.fromName;
import static com.github.comrada.crypto.wtc.TestUtils.fromWallet;
import static com.github.comrada.crypto.wtc.TestUtils.toName;
import static com.github.comrada.crypto.wtc.TestUtils.toWallet;
import static com.github.comrada.crypto.wtc.TestUtils.transactionUrl;
import static com.github.comrada.crypto.wtc.TestUtils.type;
import static com.github.comrada.crypto.wtc.TestUtils.usdAmount;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.dto.TransactionItem;
import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import java.time.Instant;
import java.util.List;
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
    List<TransactionItem> transactionItems = mockTransactionItemsWithNativeAsset();
    AlertDetail alertDetail = mockAlertDetail(dto, transactionItems);
    detailsSaver.save(1L, dto, transactionItems);

    verify(alertRepository, times(1)).saveAll(singletonList(alertDetail));
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), toWallet(transactionItems), asset(transactionItems), true,
        false);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), fromWallet(transactionItems), asset(transactionItems),
        false, false);
  }

  @Test
  void whenWalletHasMultipleAddresses_thenItIsNotSaved() {
    TransactionDetail dto = mockMultiAddressTransactionDetail();
    List<TransactionItem> transactionItems = mockMultiAddressTransactionItems();
    AlertDetail alertDetail = mockAlertDetail(dto, transactionItems);
    detailsSaver.save(1L, dto, transactionItems);

    verify(alertRepository, times(1)).saveAll(singletonList(alertDetail));
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), toWallet(transactionItems), asset(transactionItems),
        false, false);
    verify(walletRepository, never()).addWallet(dto.blockchain(), fromWallet(transactionItems), asset(transactionItems),
        true, false);
  }

  @Test
  void whenNotNativeAssetIsUsed_thenWalletSavedAsToken() {
    TransactionDetail dto = mockTransactionDetailWithNotNativeAsset();
    List<TransactionItem> transactionItems = mockTransactionItemsWithNotNativeAsset();
    AlertDetail alertDetail = mockAlertDetail(dto, transactionItems);
    detailsSaver.save(1L, dto, transactionItems);

    verify(alertRepository, times(1)).saveAll(singletonList(alertDetail));
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), toWallet(transactionItems), asset(transactionItems),
        false, true);
    verify(walletRepository, times(1)).addWallet(dto.blockchain(), fromWallet(transactionItems), asset(transactionItems),
        true, true);
  }

  private TransactionDetail mockTransactionDetailWithNotNativeAsset() {
    return new TransactionDetail(
        "Ethereum",
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9"
    );
  }

  private List<TransactionItem> mockTransactionItemsWithNotNativeAsset() {
    return singletonList(new TransactionItem("transfer", bigDecimal(2999.38), "PAXG", bigDecimal(5392568, 2),
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "0x21a31ee1afc51d94c2efccaa2092ad1028285549",
        "Binance (Exchange)",
        "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
        "Unknown"));
  }

  private TransactionDetail mockTransactionDetailWithNativeAsset() {
    return new TransactionDetail(
        "Ethereum",
        Instant.parse("2022-07-21T22:35:07Z"),
        "0x9b0e644ef0fe67b6db4af88b15922c5964c0a7a0fb3d0a7cb108ee0ff18a8094",
        "https://etherscan.io/tx/0x9b0e644ef0fe67b6db4af88b15922c5964c0a7a0fb3d0a7cb108ee0ff18a8094"
    );
  }

  private List<TransactionItem> mockTransactionItemsWithNativeAsset() {
    return singletonList(new TransactionItem("transfer", bigDecimal(50409.99), "ETH", bigDecimal(5392568, 2),
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "0x366064cc2baa69ff0bb0dd7dd07cb266e5105759",
        "Unknown",
        "0xc098b2a3aa256d2140208c3de6543aaef5cd3a94",
        "Ftx (Exchange)"));
  }

  private TransactionDetail mockMultiAddressTransactionDetail() {
    return new TransactionDetail(
        "Ethereum",
        Instant.parse("2021-12-20T10:03:35Z"),
        "0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9"
    );
  }

  private List<TransactionItem> mockMultiAddressTransactionItems() {
    return singletonList(new TransactionItem("transfer", bigDecimal(2999.38), "ETH", bigDecimal(5392568, 2),
        "https://etherscan.io/tx/0x1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9",
        "Multiple Addresses",
        "Coinbase (Exchange)",
        "0xb60c61dbb7456f024f9338c739b02be68e3f545c",
        "Unknown"));
  }

  private AlertDetail mockAlertDetail(TransactionDetail dto, List<TransactionItem> transactionItems) {
    AlertDetail entity = new AlertDetail();
    entity.setId(1L);
    entity.setBlockchain(dto.blockchain());
    entity.setType(type(transactionItems));
    entity.setAmount(amount(transactionItems));
    entity.setAsset(asset(transactionItems) != null ? asset(transactionItems).toUpperCase() : null);
    entity.setUsdAmount(usdAmount(transactionItems));
    entity.setTimestamp(dto.timestamp());
    entity.setHash(dto.hash());
    entity.setTransactionUrl(transactionUrl(transactionItems));
    entity.setFromWallet(fromWallet(transactionItems));
    entity.setFromName(fromName(transactionItems));
    entity.setToWallet(toWallet(transactionItems));
    entity.setToName(toName(transactionItems));
    return entity;
  }
}