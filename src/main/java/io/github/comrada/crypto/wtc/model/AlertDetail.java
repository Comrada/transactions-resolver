package io.github.comrada.crypto.wtc.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "alert_details")
public class AlertDetail {

  @Id
  @Column(name = "alert_id", nullable = false)
  private Long id;

  @Column(length = 32)
  private String blockchain;

  @Column(length = 32)
  private String type;

  @Column(precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(length = 16)
  private String asset;

  @Column(precision = 19, scale = 2)
  private BigDecimal usdAmount;

  @Column
  private Instant timestamp;

  @Column(length = 128)
  private String hash;

  @Column
  private String transactionUrl;

  @Column(length = 128)
  private String fromWallet;

  @Column(length = 128)
  private String fromName;

  @Column
  private String fromWalletUrl;

  @Column(length = 128)
  private String toWallet;

  @Column(length = 128)
  private String toName;

  @Column
  private String toWalletUrl;

  public AlertDetail() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBlockchain() {
    return blockchain;
  }

  public void setBlockchain(String blockchain) {
    this.blockchain = blockchain;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(String asset) {
    this.asset = asset;
  }

  public BigDecimal getUsdAmount() {
    return usdAmount;
  }

  public void setUsdAmount(BigDecimal usdAmount) {
    this.usdAmount = usdAmount;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getTransactionUrl() {
    return transactionUrl;
  }

  public void setTransactionUrl(String transactionUrl) {
    this.transactionUrl = transactionUrl;
  }

  public String getFromWallet() {
    return fromWallet;
  }

  public void setFromWallet(String fromWallet) {
    this.fromWallet = fromWallet;
  }

  public String getFromName() {
    return fromName;
  }

  public void setFromName(String fromName) {
    this.fromName = fromName;
  }

  public String getFromWalletUrl() {
    return fromWalletUrl;
  }

  public void setFromWalletUrl(String fromWalletUrl) {
    this.fromWalletUrl = fromWalletUrl;
  }

  public String getToWallet() {
    return toWallet;
  }

  public void setToWallet(String toWallet) {
    this.toWallet = toWallet;
  }

  public String getToName() {
    return toName;
  }

  public void setToName(String toName) {
    this.toName = toName;
  }

  public String getToWalletUrl() {
    return toWalletUrl;
  }

  public void setToWalletUrl(String toWalletUrl) {
    this.toWalletUrl = toWalletUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlertDetail that = (AlertDetail) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "[id=" + id + ", blockchain=" + blockchain + ", type=" + type + ']';
  }
}