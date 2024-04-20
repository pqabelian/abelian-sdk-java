package info.abelian.sdk.demo;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.wallet.Coin;
import info.abelian.sdk.wallet.TxTracker;

public class DemoTxTracker {

  public static void demoTrackSpentCoins(String[] args) throws Exception {
    // Usage: TrackSpentCoins [BLOCK_HEIGHT BLOCK_HEIGHT ...]
    if (args.length < 1) {
      System.out.printf("Usage: TrackSpentCoins BLOCK_HEIGHT [BLOCK_HEIGHT ...]\n");
      return;
    }
    long[] blockHeights = new long[args.length];
    for (int i = 0; i < args.length; i++) {
      blockHeights[i] = Long.parseLong(args[i]);
    }

    HotWalletDB hotWalletDB = Demo.getHotWalletDB();
    TxTracker txTracker = TxTracker.create(Demo.getAbecRPCClient());

    for (long blockHeight : blockHeights) {
      System.out.printf("\n==> Tracking spent coins in block %d.\n", blockHeight);
      // CAUTION: In production env, the block info should be fetched by ChainViewer.getSafeBlockInfo().
      BlockInfo blockInfo = txTracker.getAbecRPCClient().getBlockInfo(blockHeight);
      if (blockInfo == null) {
        System.out.printf("Failed to get block info.\n");
        return;
      }

      for (Bytes txid : blockInfo.txHashes) {
        System.out.printf("\n--> Tracking spent coins in tx %s.\n", txid);
        Bytes[] coinSerialNumbers = txTracker.getCoinSerialNumbers(txid);
        if (coinSerialNumbers == null) {
          System.out.printf("Failed to get coin serial numbers.\n");
          return;
        }
        for (Bytes coinSerialNumber : coinSerialNumbers) {
          Coin spentCoin = hotWalletDB.getCoinBySerialNumber(coinSerialNumber);
          if (spentCoin == null) {
            continue;
          }
          System.out.printf("💰 Found spent coin: id=%s, value=%d.\n", spentCoin.id, spentCoin.value);
          hotWalletDB.markSpentCoin(spentCoin.id);
        }
      }
    }
  }
}
