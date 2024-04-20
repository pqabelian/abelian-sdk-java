package info.abelian.sdk.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.ChainViewer;
import info.abelian.sdk.wallet.Coin;
import info.abelian.sdk.wallet.TxBuilder;
import info.abelian.sdk.wallet.TxBuilder.TxInDesc;
import info.abelian.sdk.wallet.TxBuilder.TxOutDesc;
import info.abelian.sdk.wallet.UnsignedRawTx;

public class DemoTxBuilder {

  public static void demoGenerateUnsignedRawTx(String[] args) throws Exception {
    // Usage: GenerateUnsignedRawTx [NUM_INPUTS] [NUM_OUTPUTS]
    int numInputs = args.length > 0 ? Integer.parseInt(args[0]) : 2;
    int numOutputs = args.length > 1 ? Integer.parseInt(args[1]) : 2;

    // STEP 0.
    System.out.printf("\n==> Generating an unsigned raw tx with %d inputs and %d outputs.\n", numInputs, numOutputs);

    // STEP 1.
    System.out.printf("\n==> Getting account and coin data from the hot wallet db.\n");
    HotWalletDB db = Demo.getHotWalletDB();
    Account[] accounts = db.getAllViewerAccounts();
    Coin[] coins = db.getAllUnspentCoins();
    System.out.printf("Got %d accounts and %d coins.\n", accounts.length, coins.length);
    if (accounts.length < 2 || coins.length < numInputs) {
      System.out.printf("Not enough accounts or coins.\n");
      return;
    }

    // STEP 2.
    System.out.printf("\n==> Selecting %d input coins with the lowest block height.\n", numInputs);
    Arrays.sort(coins, (a, b) -> (int) (a.blockHeight - b.blockHeight));
    Coin[] inputCoins = Arrays.copyOfRange(coins, 0, numInputs);
    for (int i = 0; i < inputCoins.length; i++) {
      System.out.printf("Input coin %d: %s\n", i, inputCoins[i]);
    }

    // STEP 3.
    System.out.printf("\n==> Selecting %d output accounts randomly.\n", numOutputs);
    List<Account> accountList = Arrays.asList(accounts);
    Collections.shuffle(accountList);
    Account[] outputAccounts = Arrays.copyOfRange(accountList.toArray(new Account[0]), 0, numOutputs);
    for (int i = 0; i < outputAccounts.length; i++) {
      System.out.printf("Output address %d: %s\n", i, outputAccounts[i].getShortAddress());
    }

    // STEP 4.
    System.out.printf("\n==> Calculating tx fee and %d output values.\n", numOutputs);
    long txFee = TxBuilder.DEFAULT_TX_FEE;

    long totalInputValue = 0;
    for (Coin coin : inputCoins) {
      totalInputValue += coin.value;
    }
    long totalOutputValue = totalInputValue - txFee;

    long outputValues[] = new long[numOutputs];
    long avgOutputValue = totalOutputValue / numOutputs;
    for (int i = 0; i < numOutputs; i++) {
      if (i == numOutputs - 1) {
        outputValues[i] = totalOutputValue - avgOutputValue * (numOutputs - 1);
      } else {
        outputValues[i] = avgOutputValue;
      }
    }

    System.out.printf("Total input value : %d\n", totalInputValue);
    System.out.printf("Total output value: %d\n", totalOutputValue);
    System.out.printf("Tx fee            : %d\n", txFee);
    for (int i = 0; i < outputValues.length; i++) {
      System.out.printf("Output value %d    : %d\n", i, outputValues[i]);
    }

    // STEP 5.
    System.out.printf("\n==> Building unsigned raw tx with TxBuilder.\n");

    // Build the TxInDesc array.
    TxInDesc[] inputDescs = new TxInDesc[inputCoins.length];
    for (int i = 0; i < inputCoins.length; i++) {
      inputDescs[i] = new TxInDesc(inputCoins[i]);
    }

    // Build the TxOutDesc array.
    TxOutDesc[] outputDescs = new TxOutDesc[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      outputDescs[i] = new TxOutDesc(outputAccounts[i].getAddress(), outputValues[i]);
    }

    // Build the unsigned raw tx and save it in the hot wallet db.
    ChainViewer viewer = Demo.createChainViewer(false);
    UnsignedRawTx tx = new TxBuilder(viewer)
        .addInputs(inputDescs)
        .addOutputs(outputDescs)
        .setTxFee(txFee)
        .build();
    System.out.printf("Unsigned raw tx: data=%s, signers=%d.\n", tx.data, tx.signers.length);
    String txMd5 = db.addUnsignedRawTxIfNotExists(tx);
    System.out.printf("Unsigned raw tx saved in the hot wallet db with txMd5: %s\n", txMd5);
  }
}
