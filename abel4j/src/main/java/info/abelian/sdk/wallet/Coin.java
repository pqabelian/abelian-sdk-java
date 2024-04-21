package info.abelian.sdk.wallet;

import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.common.Struct;

public class Coin extends Struct {
  public CoinID id;
  public ShortAbelAddress ownerShortAddress;
  public AbelAddress ownerAddress;
  public long value;
  public Bytes script;
  public Bytes serialNumber;
  public Bytes blockHash;
  public long blockHeight;

  public Coin(CoinID id, ShortAbelAddress ownerShortAddress, AbelAddress ownerAddress, long value, Bytes script,
      Bytes blockHash, long blockHeight, Bytes serialNumber) {
    this.id = id;
    this.ownerShortAddress = ownerShortAddress;
    this.ownerAddress = ownerAddress;
    this.value = value;
    this.script = script;
    this.blockHash = blockHash;
    this.blockHeight = blockHeight;
    this.serialNumber = serialNumber;
  }

  public String toString() {
    return String.format("COIN(id=%s, height=%d, owner=0x%s, value=%d, script=%s)", id, blockHeight,
        ownerShortAddress.toHex(), value, script);
  }

  public boolean isIncompleteData() {
    return id == null
        || ownerShortAddress == null
        || ownerAddress == null
        || value < 0
        || script == null
        || blockHash == null
        || blockHeight < 0;
  }
}