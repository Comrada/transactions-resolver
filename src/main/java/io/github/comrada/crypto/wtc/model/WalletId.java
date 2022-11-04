package io.github.comrada.crypto.wtc.model;

import static java.util.Objects.requireNonNull;

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
  @Column(nullable = false, length = 32)
  private String blockchain;
  @Column(nullable = false, length = 64)
  private String address;
  @Column(nullable = false, length = 16)
  private String asset;

  public WalletId() {
  }

  public WalletId(String blockchain, String address, String asset) {
    this.blockchain = requireNonNull(blockchain);
    this.address = requireNonNull(address);
    this.asset = requireNonNull(asset);
  }

  public String getBlockchain() {
    return blockchain;
  }

  public void setBlockchain(String blockchain) {
    this.blockchain = blockchain;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = requireNonNull(address);
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(String asset) {
    this.asset = requireNonNull(asset);
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
    return Objects.equals(this.blockchain, entity.blockchain) &&
        Objects.equals(this.address, entity.address) &&
        Objects.equals(this.asset, entity.asset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blockchain, address);
  }
}