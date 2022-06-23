package com.github.comrada.crypto.wtc.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "wallets",
    indexes = {
        @Index(name = "IDX_CHECKED_AT", columnList = "checked_at"),
        @Index(name = "IDX_BALANCE", columnList = "balance")
    },
    uniqueConstraints = @UniqueConstraint(columnNames = {"blockchain", "asset", "address"})
)
public class Wallet {

  @EmbeddedId
  private WalletId id;

  @Column(precision = 19, scale = 2)
  private BigDecimal balance;

  @Column(name = "checked_at")
  private Instant checkedAt;

  @Column
  private Boolean exchange;
  @Column
  private Boolean locked = false;

  public Wallet() {
  }

  public WalletId getId() {
    return id;
  }

  public void setId(WalletId id) {
    this.id = id;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Instant getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(Instant checkedAt) {
    this.checkedAt = checkedAt;
  }

  public Boolean isExchange() {
    return exchange;
  }

  public void setExchange(Boolean exchange) {
    this.exchange = exchange;
  }

  public Boolean isLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Wallet wallet = (Wallet) o;
    return id.equals(wallet.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}