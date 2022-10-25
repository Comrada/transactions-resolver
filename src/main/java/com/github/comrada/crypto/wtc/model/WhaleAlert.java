package com.github.comrada.crypto.wtc.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "whale_alerts")
public class WhaleAlert {

  @Id
  @Column(nullable = false)
  private Long id;

  @Column
  private String message;

  @Column
  private String link;

  @Column
  private Instant postedAt;

  @Column(length = 16)
  private String asset;

  @Column
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private ProcessingStatus processStatus;

  @Column
  private Instant processedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Instant getPostedAt() {
    return postedAt;
  }

  public void setPostedAt(Instant postedAt) {
    this.postedAt = postedAt;
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(String asset) {
    this.asset = asset;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public ProcessingStatus getProcessStatus() {
    return processStatus;
  }

  public void setProcessStatus(ProcessingStatus processStatus) {
    this.processStatus = processStatus;
  }

  public Instant getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Instant processedAt) {
    this.processedAt = processedAt;
  }

  public boolean isNew() {
    return processStatus == ProcessingStatus.NEW;
  }

  public enum ProcessingStatus {
    NEW, IN_PROGRESS, DONE, FAILED
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WhaleAlert that = (WhaleAlert) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "[id=" + id + ", postedAt=" + postedAt + ", asset=" + asset + ", amount=" + amount + ']';
  }
}