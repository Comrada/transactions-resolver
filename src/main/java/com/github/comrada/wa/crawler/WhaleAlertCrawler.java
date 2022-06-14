package com.github.comrada.wa.crawler;

import static java.util.Objects.requireNonNull;

import com.github.comrada.wa.dto.TransactionDetail;
import com.github.comrada.wa.model.AlertDetail;
import com.github.comrada.wa.model.WhaleAlert;
import com.github.comrada.wa.repository.AlertDetailRepository;
import com.github.comrada.wa.repository.WalletRepository;
import com.github.comrada.wa.resolver.TransactionLoader;
import com.github.comrada.wa.resolver.parser.ResponseParser;
import java.util.function.Consumer;

public class WhaleAlertCrawler implements Consumer<WhaleAlert> {

  private final TransactionLoader transactionLoader;
  private final ResponseParser responseParser;
  private final AlertDetailRepository alertDetailRepository;
  private final WalletRepository walletRepository;

  public WhaleAlertCrawler(TransactionLoader transactionLoader, ResponseParser responseParser,
      AlertDetailRepository alertDetailRepository, WalletRepository walletRepository) {
    this.transactionLoader = requireNonNull(transactionLoader);
    this.responseParser = requireNonNull(responseParser);
    this.alertDetailRepository = requireNonNull(alertDetailRepository);
    this.walletRepository = requireNonNull(walletRepository);
  }

  @Override
  public void accept(WhaleAlert alert) {
    String pageContent = transactionLoader.load(alert.getLink());
    TransactionDetail transactionDetail = responseParser.parse(pageContent);
    saveDetails(alert.getId(), transactionDetail);
  }

  private void saveDetails(Long alertId, TransactionDetail transactionDetail) {
    AlertDetail alertDetail = createAlertDetail(alertId, transactionDetail);
    alertDetailRepository.save(alertDetail);
    walletRepository.addWallet(transactionDetail.asset(), transactionDetail.fromWallet());
    walletRepository.addWallet(transactionDetail.asset(), transactionDetail.toWallet());
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
