package info.abelian.sdk.rpc;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class BlockInfo extends Struct {
  public long height;
  public Bytes hash;
  public Bytes prevHash;
  public long time;
  public Bytes[] txHashes;
}
