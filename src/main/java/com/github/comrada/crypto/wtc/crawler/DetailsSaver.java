package com.github.comrada.crypto.wtc.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import com.github.comrada.crypto.wtc.dto.TransactionItem;
import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import java.util.List;
import java.util.Set;

public class DetailsSaver {

  private final Set<String> addressExclusions = Set.of(
      "Multiple Addresses"
  );
  private final AlertDetailRepository alertDetailRepository;
  private final WalletRepository walletRepository;
  private final TokenDeterminer tokenDeterminer;

  public DetailsSaver(AlertDetailRepository alertDetailRepository, WalletRepository walletRepository) {
    this.alertDetailRepository = requireNonNull(alertDetailRepository);
    this.walletRepository = requireNonNull(walletRepository);
    this.tokenDeterminer = new TokenDeterminer();
  }

  public void save(Long alertId, TransactionDetail dto, List<TransactionItem> transactionItems) {
    if (!transactionItems.isEmpty()) {
      List<AlertDetail> alertDetail = createAlertDetailEntity(alertId, dto, transactionItems);
      alertDetailRepository.saveAll(alertDetail);
      saveWallets(alertDetail);
    }
  }

  private void saveWallets(List<AlertDetail> alertDetails) {
    alertDetails.forEach(alert -> {
      saveWallet(alert.getBlockchain(), alert.getFromWallet(), alert.getAsset(), alert.getFromName());
      saveWallet(alert.getBlockchain(), alert.getToWallet(), alert.getAsset(), alert.getToName());
    });
  }

  private void saveWallet(String blockchain, String address, String asset, String walletName) {
    if (address != null && !addressExclusions.contains(address)) {
      walletRepository.addWallet(blockchain, address, asset, isExchange(walletName),
          tokenDeterminer.isToken(blockchain, asset));
    }
  }

  private boolean isExchange(String walletName) {
    return walletName != null && walletName.toLowerCase().contains("exchange");
  }

  private List<AlertDetail> createAlertDetailEntity(Long id, TransactionDetail dto, List<TransactionItem> transactionItems) {
    return transactionItems.stream().map(item -> AlertDetail.builder()
            .id(id)
            .blockchain(dto.blockchain())
            .timestamp(dto.timestamp())
            .hash(dto.hash())
            .type(item.type())
            .amount(item.amount())
            .asset(item.asset() != null ? item.asset().toUpperCase() : null)
            .usdAmount(item.usdAmount())
            .transactionUrl(item.transactionUrl())
            .fromWallet(item.fromWallet())
            .fromName(item.fromName())
            .toWallet(item.toWallet())
            .toName(item.toName())
            .build())
        .toList();
  }
}
