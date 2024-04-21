package info.abelian.sdk.demo;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.rpc.TxInfo;
import info.abelian.sdk.wallet.ChainViewer;
import info.abelian.sdk.wallet.Coin;

public class DemoChainViewer {

  public static void demoGetSafeBlockInfo(String[] args) throws Exception {
    // Usage: GetSafeBlockInfo HEIGHT
    if (args.length < 1) {
      System.out.printf("Usage: GetSafeBlockInfo HEIGHT [REQUIRED_CONFIRMATIONS]\n");
      return;
    }
    long height = Integer.parseInt(args[0]);

    System.out.printf("\n==> Create a ChainViewer with all builtin accounts.\n");
    ChainViewer viewer = Demo.createChainViewer(false);

    System.out.printf("\n==> Get height and safe height (required confirmations: %d).\n", viewer.getRequiredConfirmations());
    System.out.printf("Height    : %d\nSafeHeight: %d\n", viewer.getLatestHeight(), viewer.getLatestSafeHeight());

    viewer.setRequiredConfirmations(3);
    System.out.printf("\n==> Get height and safe height (required confirmations: %d).\n", viewer.getRequiredConfirmations());
    System.out.printf("Height    : %d\nSafeHeight: %d\n", viewer.getLatestHeight(), viewer.getLatestSafeHeight());
    
    viewer.setRequiredConfirmations(6);
    System.out.printf("\n==> Get height and safe height (required confirmations: %d).\n", viewer.getRequiredConfirmations());
    System.out.printf("Height    : %d\nSafeHeight: %d\n", viewer.getLatestHeight(), viewer.getLatestSafeHeight());

    viewer.setRequiredConfirmations(args.length > 1 ? Integer.parseInt(args[1]) : 1);
    System.out.printf("\n==> Get block info for height %d (required confirmations: %d).\n", height, viewer.getRequiredConfirmations());
    BlockInfo blockInfo = viewer.getSafeBlockInfo(height);
    if (blockInfo == null) {
      System.out.printf("Failed to get block info.\n");
      return;
    }
    System.out.printf("Block %d: hash=%s, txs=%d.\n", height, blockInfo.hash, blockInfo.txHashes.length);
  }

  public static void demoGetTxInfo(String[] args) throws Exception {
    // Usage: GetTxInfo TXID
    if (args.length < 1) {
      System.out.printf("Usage: GetTxInfo TXID\n");
      return;
    }
    Bytes txid = new Bytes(args[0]);

    System.out.printf("\n==> Create a ChainViewer with all builtin accounts.\n");
    ChainViewer viewer = Demo.createChainViewer(true);

    System.out.printf("\n==> Get tx info for txid %s.\n", txid);
    TxInfo txInfo = viewer.getAbecRPCClient().getTxInfo(txid);
    if (txInfo == null) {
      System.out.printf("Failed to get tx info.\n");
      return;
    }
    txInfo.vins = null;
    txInfo.vouts = null;
    System.out.printf("TxInfo (without vins and vouts): %s.\n", txInfo);
  }

  public static void demoGetCoins(String[] args) throws Exception {
    // Usage: GetCoins HEIGHT
    if (args.length < 1) {
      System.out.printf("Usage: GetCoins HEIGHT\n");
      return;
    }
    long height = Integer.parseInt(args[0]);

    System.out.printf("\n==> Create a ChainViewer with all builtin accounts.\n");
    ChainViewer viewer = Demo.createChainViewer(true);
    HotWalletDB db = Demo.getHotWalletDB();

    System.out.printf("\n==> Get all txids in block %d.\n", height);
    BlockInfo blockInfo = viewer.getSafeBlockInfo(height);
    if (blockInfo == null) {
      System.out.printf("Failed to get block info.\n");
      return;
    }
    for (Bytes txid : blockInfo.txHashes) {
      System.out.printf("Txid: %s\n", txid);
    }

    for (Bytes txid: blockInfo.txHashes) {
      System.out.printf("\n==> Get all viewable coins in tx %s.\n", txid);
      Coin[] coins = viewer.getCoins(txid);
      if (coins == null) {
        System.out.printf("Failed to get coins for txid %s.\n", txid);
        continue;
      }
      for (Coin coin : coins) {
        System.out.printf("Found: 💰 %s\n", coin);
        db.addCoinIfNotExists(coin);
      }
    }
  }

  public static void demoScanCoins(String[] args) throws Exception {
    // Usage: ScanCoins [HeightStart [heightEnd]]
    int heightStart = args.length > 0 ? Integer.parseInt(args[0]) : 751;
    int heightEnd = args.length > 1 ? Integer.parseInt(args[1]) : heightStart;

    System.out.printf("\n==> Create a ChainViewer with all builtin accounts.\n");
    ChainViewer viewer = Demo.createChainViewer(true);

    // Scan all blocks.
    for (long height = heightStart; height <= heightEnd; height++) {
      ScanBlockCoins(viewer, height);
    }
  }

  private static void ScanBlockCoins(ChainViewer viewer, long height) throws Exception {
    System.out.printf("\n==> Search for all viewable coins in block %d.\n", height);

    // Get block info (which contains all txids).
    BlockInfo blockInfo = viewer.getSafeBlockInfo(height);
    if (blockInfo == null) {
      System.out.println("Failed to get block info.");
      return;
    }
    System.out.printf("Block %d: hash=%s, txs=%d.\n", height, blockInfo.hash, blockInfo.txHashes.length);

    // Get all coins in the block and save them to the hot wallet database.
    HotWalletDB db = Demo.getHotWalletDB();
    for (Bytes txid : blockInfo.txHashes) {
      Coin[] coins = viewer.getCoins(txid);
      if (coins == null) {
        System.out.printf("Failed to get coins for txid %s.\n", txid);
        continue;
      }
      System.out.printf("--> Found %d coin%s in tx %s.\n", coins.length, coins.length > 1 ? "s" : "", txid);
      for (Coin coin : coins) {
        System.out.printf("💰 %s\n", coin);
        db.addCoinIfNotExists(coin);
      }
    }
  }
}
