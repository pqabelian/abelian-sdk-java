package info.abelian.sdk.demo.persist;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.CryptoKey.PrivateKey;

public class SignerAccountTable {
  
  @DatabaseTable(tableName = "signer_account")
  public static class SignerAccountRow {
    @DatabaseField(id = true)
    public String shortAddressHex;

    @DatabaseField
    public String privateKeyHex;

    public SignerAccountRow() {
    }

    public SignerAccountRow(String shortAddressHex, String privateKeyHex) {
      this.shortAddressHex = shortAddressHex;
      this.privateKeyHex = privateKeyHex;
    }

    public SignerAccountRow(Account account) {
      this(account.getShortAddress().toHex(), account.getPrivateKey().toHex());
    }
  }

  private Dao<SignerAccountRow, String> dao;

  public SignerAccountTable(ConnectionSource connectionSource) throws SQLException {
    dao = DaoManager.createDao(connectionSource, SignerAccountRow.class);
    TableUtils.createTableIfNotExists(connectionSource, SignerAccountRow.class);
  }

  public long getCount() throws SQLException {
    return dao.countOf();
  }

  public void addAccountIfNotExists(Account account) throws SQLException {
    if (!account.isSignerAccount()) {
      throw new IllegalArgumentException("Only signer account is allowed to save to signer account table.");
    }
    dao.createIfNotExists(new SignerAccountRow(account));
  }

  public Account getAccount(ShortAbelAddress shortAddress) throws SQLException {
    SignerAccountRow row = dao.queryForId(shortAddress.toHex());
    if (row == null) {
      return null;
    }
    try {
      return Account.importSignerAccount(0, new PrivateKey(new Bytes(row.privateKeyHex)));
    } catch (AbelException e) {
      throw new RuntimeException(e);
    }
  }
}
