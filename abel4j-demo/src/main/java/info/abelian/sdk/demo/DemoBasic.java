package info.abelian.sdk.demo;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressResult;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.rpc.AbecRPCClient.JsonResponse;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.rpc.ChainInfo;
import info.abelian.sdk.rpc.TxInfo;
import info.abelian.sdk.rpc.TxVin;
import info.abelian.sdk.rpc.TxVout;
import info.abelian.sdk.wallet.Crypto;

public class DemoBasic {

  // Demo Crypto.
  public static void demoCrypto(String[] args) throws Exception {
    class DemoTasks {
      void generateKeysAndAddress(Bytes seed) throws AbelException {
        System.out.println("    Seed = " + Utils.summary(seed));
        GenerateCryptoKeysAndAddressResult ckaa = Crypto.generateKeysAndAddress(seed);
        System.out.println("    SpendSecretKey = " + Utils.summary(ckaa.getSpendSecretKey()));
        System.out.println("    SerialNoSecretKey = " + Utils.summary(ckaa.getSerialNoSecretKey()));
        System.out.println("    ViewSecretKey = " + Utils.summary(ckaa.getViewSecretKey()));
        System.out.println("    CryptoAddress = " + Utils.summary(ckaa.getCryptoAddress()));
      }
    }

    DemoTasks tasks = new DemoTasks();

    System.out.println("\n==> Generate crypto seeds.");
    Bytes[] seeds = new Bytes[3];
    for (int i = 0; i < seeds.length; i++) {
      seeds[i] = Crypto.generateSeed();
      System.out.println("--> Seed[" + i + "] = " + Utils.summary(seeds[i]));
    }

    System.out.println("\n==> Generate crypto keys and addresses.");
    for (int i = 0; i < seeds.length; i++) {
      System.out.printf("--> Seed[%d]\n", i);
      tasks.generateKeysAndAddress(seeds[i]);
    }

    System.out.println("\n==> Generate crypto keys and addresses again (from the same seeds).");
    for (int i = 0; i < seeds.length; i++) {
      System.out.printf("--> Seed[%d]\n", i);
      tasks.generateKeysAndAddress(seeds[i]);
    }
  }

  // Demo AbecRPCClient.
  public static void demoAbecRPCClient(String[] args) throws Exception {
    AbecRPCClient client = Demo.getAbecRPCClient();

    class DemoTasks {
      void doAnyRPCCall(String method, Object... params) throws Exception {
        JsonResponse resp = client.call(method, params);
        System.out.printf("Request: method: %s, params: %s\n", method, Utils.summary(params));
        System.out.printf("Response: %s\n", resp);
        if (resp.hasError()) {
          System.out.println("    ❌ This is a failed call.");
        } else {
          System.out.println("    ✅ This is a successful call.");
        }
        System.out.printf("    Error: %s\n", resp.getError());
        System.out.printf("    Result: %s\n", resp.getResultAsString());
      }
    }

    DemoTasks tasks = new DemoTasks();

    System.out.println("\n==> Call any RPC method using client.call(method, ...params).");

    System.out.println("\n--> Call without parameters.");
    tasks.doAnyRPCCall("getinfo");

    System.out.println("\n--> Call with parameters.");
    tasks.doAnyRPCCall("getblockhash", 0);

    System.out.println("\n--> Call with wrong method name.");
    tasks.doAnyRPCCall("getnothing");

    System.out.println("\n--> Call with wrong parameters.");
    tasks.doAnyRPCCall("getinfo", 0, "0x0000000");
    
    System.out.printf("\n==> Call builtin member methods of %s.\n", AbecRPCClient.class.getSimpleName());
    System.out.println("\n--> Get chain info by client.getChainInfo().");
    ChainInfo ci = client.getChainInfo();
    if (ci != null) {
      System.out.println("ChainInfo: " + ci);
    } else {
      System.out.println("❌ Failed to get chain info.");
    }

    long blockHeight = 835;
    System.out.printf("\n==> Get block hash by client.getBlockHash(height=%d).\n", blockHeight);
    Bytes blockHash = client.getBlockHash(blockHeight);
    if (blockHash != null) {
      System.out.println("BlockHash: " + blockHash);
    } else {
      System.out.println("❌ Failed to get block hash.");
    }

    System.out.printf("\n==> Get block info by client.getBlockInfo(hash=%s).\n", blockHash);
    BlockInfo bi = client.getBlockInfo(blockHash);
    if (bi != null) {
      System.out.println("BlockInfo: " + bi);
    } else {
      System.out.println("❌ Failed to get block info.");
    }

    Bytes txid = bi.txHashes[bi.txHashes.length - 1];
    System.out.printf("\n==> Get tx info by client.getTxInfo(txid=%s).\n", blockHash);
    TxInfo ti = client.getTxInfo(txid);
    if (ti != null) {
      TxInfo tiSummary = new TxInfo() {{
        this.txid = ti.txid;
        this.time = ti.time;
        this.blockHash = ti.blockHash;
        this.blockTime = ti.blockTime;
        this.vins = new TxVin[0];
        this.vouts = new TxVout[0];
        this.confirmations = ti.confirmations;
      }};
      System.out.println("TxInfo (without inputs and outputs): " + tiSummary);
      System.out.println("Inputs:");
      for (int i = 0; i < ti.vins.length; i++) {
        System.out.printf("  vins[%d]:\n", i);
        System.out.printf("    serialnumber: %s\n", ti.vins[i].serialNumber);
      }
      System.out.println("Outputs");
      for (int i = 0; i < ti.vouts.length; i++) {
        System.out.printf("  vouts[%d]:\n", i);
        System.out.printf("    script: %s\n", ti.vouts[i].script);
        Bytes fingerprint = Crypto.decodeFingerprintFromTxVoutScript(ti.vouts[i].script);
        System.out.printf("    fingerprint: %s\n", fingerprint);
      }
    } else {
      System.out.println("❌ Failed to get tx info.");
    }
  }
}
