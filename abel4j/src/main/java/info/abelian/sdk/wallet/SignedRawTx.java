package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class SignedRawTx extends Struct {
  
  public Bytes data;

  public Bytes txid;

  public SignedRawTx(Bytes data, Bytes txid) {
    this.data = data;
    this.txid = txid;
  }
}
