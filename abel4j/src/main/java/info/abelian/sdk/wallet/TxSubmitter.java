package info.abelian.sdk.wallet;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.rpc.AbecRPCClient;

public class TxSubmitter extends AbelBase {

  public static TxSubmitter create(int chainId) throws AbelException {
    return new TxSubmitter(AbecRPCClient.getInstance(chainId));
  }

  public static TxSubmitter create(String chainName) throws AbelException {
    return new TxSubmitter(AbecRPCClient.getInstance(chainName));
  }

  public static TxSubmitter create(AbecRPCClient rpc) {
    return new TxSubmitter(rpc);
  }

  protected AbecRPCClient rpc;
  
  protected TxSubmitter(AbecRPCClient rpc) {
    this.rpc = rpc;
  }

  public AbecRPCClient getAbecRPCClient() {
    return rpc;
  }

  public boolean submit(SignedRawTx tx) {
    return rpc.sendRawTx(tx.data);
  }
}
