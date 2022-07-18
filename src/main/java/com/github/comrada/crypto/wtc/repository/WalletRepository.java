package com.github.comrada.crypto.wtc.repository;

import com.github.comrada.crypto.wtc.model.Wallet;
import com.github.comrada.crypto.wtc.model.WalletId;
import com.github.comrada.crypto.wtc.model.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {

  @Transactional
  @Modifying(flushAutomatically = true)
  @Query(value = """
      insert into wallets (blockchain, address, asset, exchange, status, locked)
      values (:#{#wallet.id.blockchain}, :#{#wallet.id.address}, :#{#wallet.id.asset}, :#{#wallet.exchange}, 'OK', false)
      """, nativeQuery = true)
  void addWallet(Wallet wallet);

  default void addWallet(String blockchain, String address, String asset) {
    addWallet(blockchain, address, asset, false);
  }

  default void addWallet(String blockchain, String address, String asset, boolean isExchange) {
    if (StringUtils.hasText(blockchain) && StringUtils.hasText(address)) {
      Wallet wallet = new Wallet();
      WalletId id = WalletId.builder()
          .blockchain(blockchain)
          .address(address)
          .asset(asset)
          .build();
      if (findById(id).isEmpty()) {
        wallet.setId(id);
        wallet.setExchange(isExchange);
        addWallet(wallet);
      }
    }
  }
}
