package info.abelian.sdk.demo.persist;

import java.sql.SQLException;
import java.util.Map;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.demo.Demo;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.Coin;
import info.abelian.sdk.wallet.CoinID;
import info.abelian.sdk.wallet.SignedRawTx;
import info.abelian.sdk.wallet.UnsignedRawTx;

public abstract class WalletDB {

  public static HotWalletDB openHotWalletDB() {
    String dbName = String.format("demo-%s-hot-wallet.db", Demo.getDefaultChainName());
    String dbUrl = "jdbc:sqlite:" + AbelBase.getEnvPath(dbName);
    HotWalletDB db = new HotWalletDB(dbUrl);
    return db;
  }

  public static ColdWalletDB openColdWalletDB() {
    String dbName = String.format("demo-%s-cold-wallet.db", Demo.getDefaultChainName());
    String dbUrl = "jdbc:sqlite:" + AbelBase.getEnvPath(dbName);
    ColdWalletDB db = new ColdWalletDB(dbUrl);
    return db;
  }

  protected ConnectionSource connectionSource;

  protected WalletDB(String jdbcUrl) {
    try {
      connectionSource = new JdbcConnectionSource(jdbcUrl);
      initTables();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      connectionSource.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract void initTables() throws SQLException;

  public static class ColdWalletDB extends WalletDB {

    protected ColdWalletDB(String jdbcUrl) {
      super(jdbcUrl);
    }

    SignerAccountTable signerAccountTable;

    @Override
    protected void initTables() throws SQLException {
      initSignerAccountTable();
    }
    
    private void initSignerAccountTable() throws SQLException {
      signerAccountTable = new SignerAccountTable(connectionSource);
      // Save all builtin accounts as signer accounts to the table.
      for (Account account : Demo.getBuiltinAccounts().values()) {
        signerAccountTable.addAccountIfNotExists(account);
      }
    }

    public long getCountOfSignerAccounts() throws SQLException {
      return signerAccountTable.getCount();
    }

    public Account getSignerAccount(ShortAbelAddress shortAddress) throws SQLException {
      return signerAccountTable.getAccount(shortAddress);
    }
  }

  public static class HotWalletDB extends WalletDB {

    ViewerAccountTable viewerAccountTable;

    CoinTable coinTable;

    TxTable txTable;

    protected HotWalletDB(String jdbcUrl) {
      super(jdbcUrl);
    }

    @Override
    protected void initTables() throws SQLException {
      initViewerAccountTable();
      initCoinTable();
      initTxTable();
    }

    private void initViewerAccountTable() throws SQLException {
      viewerAccountTable = new ViewerAccountTable(connectionSource);
      // Save all builtin accounts as viewer accounts to the table.
      for (Account account : Demo.getBuiltinAccounts().values()) {
        viewerAccountTable.addAccountIfNotExists(account.getViewerAccount());
      }
    }

    private void initCoinTable() throws SQLException {
      coinTable = new CoinTable(connectionSource);
    }

    private void initTxTable() throws SQLException {
      txTable = new TxTable(connectionSource);
    }

    public long getCountOfViewerAccounts() throws SQLException {
      return viewerAccountTable.getCount();
    }

    public Account[] getAllViewerAccounts() throws SQLException {
      return viewerAccountTable.getAllViewerAccounts();
    }

    public long getCountOfCoins() throws SQLException {
      return coinTable.getCount();
    }

    public void addCoinIfNotExists(Coin coin) throws SQLException {
      coinTable.addCoinIfNotExists(coin);
    }

    public Coin[] getAllUnspentCoins() throws SQLException {
      return coinTable.getAllUnspentCoins();
    }

    public long getCountOfTxs() throws SQLException {
      return txTable.getCount();
    }

    public String addUnsignedRawTxIfNotExists(UnsignedRawTx unsignedRawTx) throws SQLException {
      return txTable.addUnsignedRawTxIfNotExists(unsignedRawTx);
    }

    public Map<String, UnsignedRawTx> getUnsignedRawTxs(int limit) throws SQLException {
      return txTable.getUnsignedRawTxs(limit);
    }

    public UnsignedRawTx getUnsignedRawTx(String txMd5) throws SQLException {
      return txTable.getUnsignedRawTx(txMd5);
    }

    public void updateSignedRawTx(String txMd5, SignedRawTx signedRawTx) throws SQLException {
      txTable.updateSignedRawTx(txMd5, signedRawTx);
    }

    public SignedRawTx getSignedRawTx(String txMd5) throws SQLException {
      return txTable.getSignedRawTx(txMd5);
    }

    public void setSubmitted(String txMd5) throws SQLException {
      txTable.setSubmitted(txMd5);
    }

    public Coin getCoinBySerialNumber(Bytes serialNumber) throws SQLException {
      return coinTable.getCoinBySnHex(serialNumber.toHex());
    }

    public void markSpentCoin(CoinID coinID) throws SQLException {
      coinTable.setCoinSpentStatus(coinID.toString(), true);
    }
  }
}
