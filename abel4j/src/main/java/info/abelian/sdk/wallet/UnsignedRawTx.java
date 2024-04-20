package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.common.Struct;

public class UnsignedRawTx extends Struct {
  
  public Bytes data;
  
  public ShortAbelAddress[] signers;

  public UnsignedRawTx(Bytes data, ShortAbelAddress[] signers) {
    this.data = data;
    this.signers = signers;
  }
}
