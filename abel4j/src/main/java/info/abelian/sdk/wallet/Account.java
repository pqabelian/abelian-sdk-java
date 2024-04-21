package info.abelian.sdk.wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Fingerprint;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.go.GoProxy.AbelGoException;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressResult;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressArgs;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressResult;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressResult;
import info.abelian.sdk.wallet.CryptoKey.PrivateKey;
import info.abelian.sdk.wallet.CryptoKey.SerialNoKey;
import info.abelian.sdk.wallet.CryptoKey.SpendKey;
import info.abelian.sdk.wallet.CryptoKey.ViewKey;

public class Account extends AbelBase {

  private static final Map<String, Map<String, Account>> allChainsBuiltinAccounts = new HashMap<>();

  public static Map<String, Account> getBuiltinAccounts(String chainName) {
    if (!allChainsBuiltinAccounts.containsKey(chainName)) {
      loadBuiltinAccounts(chainName);
    }
    return allChainsBuiltinAccounts.get(chainName);
  }

  private static void loadBuiltinAccounts(String chainName) {
    Map<String, Account> accounts = new HashMap<>();
    Properties accountConf = getConf(String.format("%s.account.", chainName));
    for (String key : accountConf.stringPropertyNames()) {
      if (key.endsWith(".privateKey")) {
        String accountName = key.substring(0, key.length() - ".privateKey".length());
        PrivateKey privateKey = new PrivateKey(Bytes.fromHex(accountConf.getProperty(key)));
        try {
          accounts.put(accountName, Account.importSignerAccount(getChainID(chainName), privateKey));
        } catch (AbelException e) {
          throw new RuntimeException(e);
        }
      }
    }
    allChainsBuiltinAccounts.put(chainName, accounts);
  }

  protected int chainID;

  protected PrivateKey cryptoSeed;

  protected SpendKey spendKey;

  protected SerialNoKey serialNoKey;

  protected ViewKey viewKey;

  protected AbelAddress address;

  protected ShortAbelAddress shortAddress;

  protected Fingerprint fingerprint;

  protected Account(int chainID, PrivateKey cryptoSeed, SpendKey spendKey, SerialNoKey serialNoKey, ViewKey viewKey,
      AbelAddress address, ShortAbelAddress shortAddress, Fingerprint fingerprint) {
    this.chainID = chainID;
    this.cryptoSeed = cryptoSeed;
    this.spendKey = spendKey;
    this.serialNoKey = serialNoKey;
    this.viewKey = viewKey;
    this.address = address;
    this.shortAddress = shortAddress;
    this.fingerprint = fingerprint;
  }

  public int getChainID() {
    return chainID;
  }

  public PrivateKey getPrivateKey() {
    return cryptoSeed;
  }

  public SpendKey getSpendKey() {
    return spendKey;
  }

  public SerialNoKey getSerialNoKey() {
    return serialNoKey;
  }

  public ViewKey getViewKey() {
    return viewKey;
  }

  public AbelAddress getAddress() {
    return address;
  }

  public ShortAbelAddress getShortAddress() {
    return shortAddress;
  }

  public Fingerprint getFingerprint() {
    return fingerprint;
  }

  public boolean isSignerAccount() {
    return cryptoSeed != null || spendKey != null;
  }

  public Account getViewerAccount() {
    if (!isSignerAccount()) {
      return this;
    }
    return new Account(chainID, null, null, serialNoKey, viewKey, address, shortAddress, fingerprint);
  }

  protected static Account createAccount(int chainID, PrivateKey cryptoSeed, SpendKey spendKey, SerialNoKey serialNoKey,
      ViewKey viewKey, AbelAddress address) throws AbelException {
    try {
      GetShortAbelAddressFromAbelAddressArgs args = GetShortAbelAddressFromAbelAddressArgs.newBuilder()
          .setAbelAddress(ByteString.copyFrom(address.getData())).build();
      GetShortAbelAddressFromAbelAddressResult result = getGoProxy().goGetShortAbelAddressFromAbelAddress(args);
      ShortAbelAddress shortAddress = new ShortAbelAddress(result.getShortAbelAddress().toByteArray());
      Fingerprint fingerprint = shortAddress.getFingerprint();
      return new Account(chainID, cryptoSeed, spendKey, serialNoKey, viewKey, address, shortAddress, fingerprint);
    } catch (AbelGoException e) {
      throw new AbelException(e);
    }
  }

  public static Account generateSignerAccount(int chainID) throws AbelException {
    return importSignerAccount(chainID, new PrivateKey(Crypto.generateSeed()));
  }

  public static Account importSignerAccount(int chainID, PrivateKey privateKey) throws AbelException {
    GenerateCryptoKeysAndAddressResult ckaa = Crypto.generateKeysAndAddress(privateKey);
    SpendKey spendKey = new SpendKey(ckaa.getSpendSecretKey().toByteArray());
    SerialNoKey serialNoKey = new SerialNoKey(ckaa.getSerialNoSecretKey().toByteArray());
    ViewKey viewKey = new ViewKey(ckaa.getViewSecretKey().toByteArray());

    byte[] cryptoAddress = ckaa.getCryptoAddress().toByteArray();
    try {
      GetAbelAddressFromCryptoAddressArgs args = GetAbelAddressFromCryptoAddressArgs.newBuilder()
          .setCryptoAddress(ByteString.copyFrom(cryptoAddress)).setChainID(chainID).build();
      GetAbelAddressFromCryptoAddressResult result = getGoProxy().goGetAbelAddressFromCryptoAddress(args);
      AbelAddress address = new AbelAddress(result.getAbelAddress().toByteArray());
      return createAccount(chainID, privateKey, spendKey, serialNoKey, viewKey, address);
    } catch (AbelGoException e) {
      throw new AbelException(e);
    }
  }

  public static Account importViewerAccount(int chainID, SerialNoKey serialNoKey, ViewKey viewKey, AbelAddress address)
      throws AbelException {
    return createAccount(chainID, null, null, serialNoKey, viewKey, address);
  }
}