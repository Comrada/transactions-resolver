package com.github.comrada.wa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.wa.model.Wallet;
import com.github.comrada.wa.model.WalletId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class WalletRepositoryIntegrationTest {

  @Autowired
  WalletRepository repository;

  @Test
  @Sql("wallets.sql")
  void findAllTest() {
    List<Wallet> addresses = repository.findAll();
    assertEquals(5, addresses.size());
  }

  @Test
  @Sql("wallets.sql")
  void addWalletDoesNotChangeExistingRecord() {
    WalletId id = WalletId.builder()
        .asset("XRP")
        .address("rfTjtcvf4mBLP5hpD38RjtdAFTZdr31uiY")
        .build();

    Optional<Wallet> item1 = repository.findById(id);
    assertTrue(item1.isPresent());
    Wallet wallet1 = item1.get();
    repository.addWallet("XRP", "rfTjtcvf4mBLP5hpD38RjtdAFTZdr31uiY");
    Optional<Wallet> item12 = repository.findById(id);
    assertTrue(item12.isPresent());
    Wallet wallet12 = item12.get();
    assertEquals(wallet1, wallet12);
    assertEquals(wallet1.getBalance(), wallet12.getBalance());
    assertEquals(wallet1.getCheckedAt(), wallet12.getCheckedAt());
  }

  @Test
  void addWallet() {
    WalletId id = WalletId.builder()
        .asset("XRP")
        .address("rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg")
        .build();
    Wallet expected = new Wallet();
    expected.setId(id);
    repository.addWallet("XRP", "rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg");

    Optional<Wallet> item = repository.findById(id);
    assertTrue(item.isPresent());
    Wallet actual = item.get();
    assertEquals(expected, actual);
  }

  @Test
  void emptyAssetIsNotStored() {
    repository.addWallet("", "rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg");

    List<Wallet> wallets = repository.findAll();
    assertTrue(wallets.isEmpty());
  }

  @Test
  void emptyAddressIsNotStored() {
    repository.addWallet("XRP", "");

    List<Wallet> wallets = repository.findAll();
    assertTrue(wallets.isEmpty());
  }
}