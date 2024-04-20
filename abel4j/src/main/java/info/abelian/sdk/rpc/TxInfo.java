package info.abelian.sdk.rpc;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class TxInfo extends Struct {
  public Bytes txid;
  public long time = -1;
  public Bytes blockHash;
  public long blockTime = -1;
  public TxVin[] vins;
  public TxVout[] vouts;
  public long confirmations = -1;

  public Bytes[] getCoinSerialNumbers() {
    if (vins == null) {
      return null;
    }
    Bytes[] coinSerialNumbers = new Bytes[vins.length];
    for (int i = 0; i < vins.length; i++) {
      coinSerialNumbers[i] = vins[i].serialNumber;
    }
    return coinSerialNumbers;
  }
}