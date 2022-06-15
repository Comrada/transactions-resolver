package com.github.comrada.wa.repository;

import com.github.comrada.wa.model.Wallet;
import com.github.comrada.wa.model.WalletId;
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
      insert into wallets (asset, address, exchange)
      values (:#{#wallet.id.asset}, :#{#wallet.id.address}, :#{#wallet.exchange})
      """, nativeQuery = true)
  void addWallet(Wallet wallet);

  default void addWallet(String asset, String address) {
    addWallet(asset, address, null);
  }

  default void addWallet(String asset, String address, Boolean isExchange) {
    if (StringUtils.hasText(asset) && StringUtils.hasText(address)) {
      Wallet wallet = new Wallet();
      WalletId id = WalletId.builder()
          .asset(asset)
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
