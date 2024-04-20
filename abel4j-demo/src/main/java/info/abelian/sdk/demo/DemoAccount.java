package info.abelian.sdk.demo;

import java.util.Map;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.wallet.Account;

public class DemoAccount {

  // Demo creating new accounts and loading existing accounts.
  public static void demoAccount(String[] args) throws Exception {
    int chainID = Demo.getDefaultChainID();

    System.out.println("\n==> Create accounts.");
    Account[] accounts = new Account[3];
    for (int i = 0; i < accounts.length; i++) {
      System.out.printf("\n--> Account[%d]\n", i);
      accounts[i] = Account.generateSignerAccount(chainID);
      printAccountInfo(accounts[i]);
    }

    System.out.println("\n==> Load signer accounts from private key.");
    Account[] signerAccounts = new Account[accounts.length];
    for (int i = 0; i < accounts.length; i++) {
      System.out.printf("\n--> Account[%d]\n", i);
      signerAccounts[i] = Account.importSignerAccount(0, accounts[i].getPrivateKey());
      printAccountInfo(signerAccounts[i]);
    }

    System.out.println("\n==> Load viewer accounts from serial number key, view key and address.");
    Account[] viewerAccounts = new Account[accounts.length];
    for (int i = 0; i < accounts.length; i++) {
      System.out.printf("\n--> Account[%d]\n", i);
      viewerAccounts[i] = Account.importViewerAccount(chainID, accounts[i].getSerialNoKey(), accounts[i].getViewKey(),
          accounts[i].getAddress());
      printAccountInfo(viewerAccounts[i]);
    }

    System.out.println("\n==> Show builtin accounts.");
    Map<String, Account> builtinAccounts = Demo.getBuiltinAccounts();
    for (Map.Entry<String, Account> entry : builtinAccounts.entrySet()) {
      System.out.printf("\n--> Account[%s]\n", entry.getKey());
      printAccountInfo(entry.getValue());
    }

    System.out.println("\n==> Export the addresses of all builtin accounts.");
    String outputDir = AbelBase.getEnvPath("accounts");
    java.io.File dir = new java.io.File(outputDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    for (Map.Entry<String, Account> entry : builtinAccounts.entrySet()) {
      String accountName = entry.getKey();
      Account account = entry.getValue();
      String filePath = String.format("%s/chain-%d-account-%s.abeladdress", outputDir, chainID, accountName);
      try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
        writer.println(account.getAddress().toHex());
        writer.println(account.getShortAddress().toHex());
      }
    }
    System.out.printf("Successfully exported all addresses to %s.\n", outputDir);
  }

  private static void printAccountInfo(Account account) throws AbelException {
    System.out.println("    PrivateKey = " + account.getPrivateKey());
    System.out.println("    SpendKey = " + account.getSpendKey());
    System.out.println("    SerialNoKey = " + account.getSerialNoKey());
    System.out.println("    ViewKey = " + account.getViewKey());
    System.out.println("    Address = " + account.getAddress());
    System.out.println("    ShortAddress = " + account.getShortAddress());
    System.out.println("    Fingerprint = " + account.getFingerprint());
  }
}
