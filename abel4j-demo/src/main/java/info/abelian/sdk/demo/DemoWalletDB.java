package info.abelian.sdk.demo;

import info.abelian.sdk.demo.persist.WalletDB.ColdWalletDB;
import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;

public class DemoWalletDB {
  
  public static void demoWalletDB(String[] args) throws Exception {
    HotWalletDB hotWalletDB = Demo.getHotWalletDB();
    ColdWalletDB coldWalletDB = Demo.getColdWalletDB();

    System.out.println("\n==> Get table summary of the hot wallet db.");
    System.out.println("Count of accounts: " + hotWalletDB.getCountOfViewerAccounts());
    System.out.println("Count of coins   : " + hotWalletDB.getCountOfCoins());
    System.out.println("Count of txs     : " + hotWalletDB.getCountOfTxs());

    System.out.println("\n==> Get table summary of the cold wallet db.");
    System.out.println("Count of accounts: " + coldWalletDB.getCountOfSignerAccounts());
  }
}
