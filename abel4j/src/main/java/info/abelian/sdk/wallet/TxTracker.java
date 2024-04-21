package info.abelian.sdk.wallet;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.rpc.TxInfo;

public class TxTracker extends AbelBase {
  
  public static TxTracker create(int chainId) throws AbelException {
    return new TxTracker(AbecRPCClient.getInstance(chainId));
  }

  public static TxTracker create(String chainName) throws AbelException {
    return new TxTracker(AbecRPCClient.getInstance(chainName));
  }

  public static TxTracker create(AbecRPCClient rpc) {
    return new TxTracker(rpc);
  }

  protected AbecRPCClient rpc;
  
  protected TxTracker(AbecRPCClient rpc) {
    this.rpc = rpc;
  }

  public AbecRPCClient getAbecRPCClient() {
    return rpc;
  }

  public Bytes[] getCoinSerialNumbers(Bytes txid) {
    TxInfo txInfo = rpc.getTxInfo(txid);
    if (txInfo == null) {
      return null;
    }
    return txInfo.getCoinSerialNumbers();
  }
}
