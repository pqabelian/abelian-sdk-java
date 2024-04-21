package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class CoinID extends Struct {
  public Bytes txid;
  public int index;

  public CoinID(Bytes txid, int index) {
    this.txid = txid;
    this.index = index;
  }

  public String toString() {
    return txid.toHex() + ":" + index;
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof CoinID) {
      CoinID other = (CoinID) o;
      return txid.equals(other.txid) && index == other.index;
    }
    return false;
  }
}
