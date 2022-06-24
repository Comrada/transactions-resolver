package com.github.comrada.crypto.wtc.repository;

import com.github.comrada.crypto.wtc.model.Wallet;
import com.github.comrada.crypto.wtc.model.WalletId;
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
      insert into wallets (blockchain, address, exchange, locked)
      values (:#{#wallet.id.blockchain}, :#{#wallet.id.address}, :#{#wallet.exchange}, false)
      """, nativeQuery = true)
  void addWallet(Wallet wallet);

  default void addWallet(String blockchain, String address) {
    addWallet(blockchain, address, null);
  }

  default void addWallet(String blockchain, String address, Boolean isExchange) {
    if (StringUtils.hasText(blockchain) && StringUtils.hasText(address)) {
      Wallet wallet = new Wallet();
      WalletId id = WalletId.builder()
          .blockchain(blockchain)
          .address(address)
          .build();
      if (findById(id).isEmpty()) {
        wallet.setId(id);
        wallet.setExchange(isExchange);
        addWallet(wallet);
      }
    }
  }
}
