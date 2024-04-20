package info.abelian.sdk.wallet;

import java.util.HashMap;
import java.util.Map;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.Fingerprint;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.rpc.AbecRPCClient;

public abstract class Wallet extends AbelBase {

  protected AbecRPCClient client;

  protected Map<Fingerprint, Account> accounts = new HashMap<>();

  public Wallet(AbecRPCClient client, Account[] accounts) {
    this.client = client;
    for (Account account : accounts) {
      addAccount(account);
    }
  }

  public AbecRPCClient getAbecRPCClient() {
    return client;
  }

  protected abstract boolean acceptAccount(Account account);

  public Account addAccount(Account account) {
    if (!acceptAccount(account)) {
      LOG.warn("Account not accepted by this wallet.");
      return null;
    }
    return accounts.put(account.getFingerprint(), account);
  }

  public Account getAccount(Fingerprint fingerprint) {

    return accounts.get(fingerprint);
  }

  public Account getAccount(ShortAbelAddress shortAddress) {
    return getAccount(shortAddress.getFingerprint());
  }

  public boolean hasAccount(Fingerprint fingerprint) {
    return accounts.containsKey(fingerprint);
  }

  public boolean hasAccount(ShortAbelAddress shortAddress) {
    return hasAccount(shortAddress.getFingerprint());
  }

  public Account[] getAccounts() {
    return accounts.values().toArray(new Account[0]);
  }

  public Fingerprint[] getFingerprints() {
    return accounts.keySet().toArray(new Fingerprint[0]);
  }

  public ShortAbelAddress[] getShortAddresses() {
    Account[] accounts = getAccounts();
    ShortAbelAddress[] shortAddresses = new ShortAbelAddress[accounts.length];
    for (int i = 0; i < accounts.length; i++) {
      shortAddresses[i] = accounts[i].getShortAddress();
    }
    return shortAddresses;
  }
}
