package com.github.comrada.wa.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

  @Column(length = 128)
  private String hash;

  @Column
  private String transactionUrl;

  @Column(length = 128)
  private String fromWallet;

  @Column(length = 128)
  private String toWallet;

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

  public String getToWallet() {
    return toWallet;
  }

  public void setToWallet(String toWallet) {
    this.toWallet = toWallet;
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