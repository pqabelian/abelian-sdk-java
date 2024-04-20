package info.abelian.sdk.rpc;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class TxVout extends Struct {
  public long n;
  public Bytes script;
}