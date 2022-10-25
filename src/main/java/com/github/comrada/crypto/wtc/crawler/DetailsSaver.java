package com.github.comrada.crypto.wtc.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.dto.TransactionDetail;
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

  public void save(Long alertId, TransactionDetail dto) {
    List<AlertDetail> alertDetail = createAlertDetail(alertId, dto);
    alertDetailRepository.saveAll(alertDetail);
    saveWallets(alertDetail);
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

  private List<AlertDetail> createAlertDetail(Long id, TransactionDetail dto) {
    return dto.items().stream().map(item -> {
      AlertDetail entity = new AlertDetail();
      entity.setId(id);
      entity.setBlockchain(dto.blockchain());
      entity.setTimestamp(dto.timestamp());
      entity.setHash(dto.hash());
      entity.setType(item.type());
      entity.setAmount(item.amount());
      entity.setAsset(item.asset() != null ? item.asset().toUpperCase() : null);
      entity.setUsdAmount(item.usdAmount());
      entity.setTransactionUrl(item.transactionUrl());
      entity.setFromWallet(item.fromWallet());
      entity.setFromName(item.fromName());
      entity.setFromWalletUrl(item.fromWalletUrl());
      entity.setToWallet(item.toWallet());
      entity.setToName(item.toName());
      entity.setToWalletUrl(item.toWalletUrl());
      return entity;
    }).toList();
  }
}
