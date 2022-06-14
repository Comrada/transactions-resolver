package com.github.comrada.wa.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;

@Builder
@Embeddable
public class WalletId implements Serializable {

  @Serial
  private static final long serialVersionUID = 1375066493278443982L;
  @Column(nullable = false, length = 8)
  private String asset;

  @Column(nullable = false, length = 64)
  private String address;

  public WalletId() {
  }

  public WalletId(String asset, String address) {
    this.asset = asset;
    this.address = address;
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(String asset) {
    this.asset = asset;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WalletId entity = (WalletId) o;
    return Objects.equals(this.address, entity.address) &&
        Objects.equals(this.asset, entity.asset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, asset);
  }

}