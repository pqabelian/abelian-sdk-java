package info.abelian.sdk.demo;

import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.wallet.SignedRawTx;
import info.abelian.sdk.wallet.TxSubmitter;

public class DemoTxSubmitter {
  
  public static void demoSubmitSignedRawTx(String[] args) throws Exception {
    // Usage: SubmitSignedRawTx [TX_MD5]
    if (args.length < 1) {
      System.out.printf("Usage: SubmitSignedRawTx TX_MD5\n");
      return;
    }
    String txMd5 = args[0];

    HotWalletDB hotWalletDB = Demo.getHotWalletDB();

    System.out.printf("\n==> Getting the signed raw tx from the hot wallet db with txMd5: %s\n", txMd5);
    SignedRawTx signedRawTx = hotWalletDB.getSignedRawTx(txMd5);
    if (signedRawTx == null) {
      System.out.printf("Signed raw tx not found.\n");
      return;
    }
    System.out.printf("txid: %s\n", signedRawTx.txid.toHex());
    System.out.printf("data: %s\n", signedRawTx.data);

    System.out.printf("\n==> Submitting the signed raw tx.\n");
    TxSubmitter txSubmitter = TxSubmitter.create(Demo.getAbecRPCClient());
    boolean success = txSubmitter.submit(signedRawTx);
    System.out.printf("Submitted the signed raw tx: %s.\n", success ? "SUCCESS" : "FAILED");
    hotWalletDB.setSubmitted(txMd5);
  }
}
