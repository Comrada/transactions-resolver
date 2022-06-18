package com.github.comrada.crypto.wtc.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.model.AlertDetail;
import com.github.comrada.crypto.wtc.repository.AlertDetailRepository;
import com.github.comrada.crypto.wtc.repository.WalletRepository;
import com.github.comrada.crypto.wtc.dto.TransactionDetail;
import java.util.Set;

public class DetailsSaver {

  private final Set<String> addressExclusions = Set.of(
      "Multiple Addresses"
  );
  private final AlertDetailRepository alertDetailRepository;
  private final WalletRepository walletRepository;

  public DetailsSaver(AlertDetailRepository alertDetailRepository, WalletRepository walletRepository) {
    this.alertDetailRepository = requireNonNull(alertDetailRepository);
    this.walletRepository = requireNonNull(walletRepository);
  }

  public void save(Long alertId, TransactionDetail dto) {
    AlertDetail alertDetail = createAlertDetail(alertId, dto);
    alertDetailRepository.save(alertDetail);
    saveWallet(dto.asset(), dto.fromWallet(), dto.fromName());
    saveWallet(dto.asset(), dto.toWallet(), dto.toName());
  }

  private void saveWallet(String asset, String address, String walletName) {
    if (address != null && !addressExclusions.contains(address)) {
      walletRepository.addWallet(asset, address, isExchange(walletName));
    }
  }

  private boolean isExchange(String walletName) {
    return walletName != null && walletName.toLowerCase().contains("exchange");
  }

  private AlertDetail createAlertDetail(Long id, TransactionDetail dto) {
    AlertDetail entity = new AlertDetail();
    entity.setId(id);
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
