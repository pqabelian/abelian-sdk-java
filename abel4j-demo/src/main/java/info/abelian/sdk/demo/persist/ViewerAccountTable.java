package info.abelian.sdk.demo.persist;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.CryptoKey.SerialNoKey;
import info.abelian.sdk.wallet.CryptoKey.ViewKey;

public class ViewerAccountTable {

  @DatabaseTable(tableName = "viewer_account")
  public static class ViewerAccountRow {
    @DatabaseField(id = true)
    public String shortAddressHex;

    @DatabaseField
    public String serialNoKeyHex;

    @DatabaseField
    public String viewKeyHex;

    @DatabaseField
    public String addressHex;

    public ViewerAccountRow() {
    }

    public ViewerAccountRow(String shortAddressHex, String serialNoKeyHex, String viewKeyHex, String addressHex) {
      this.shortAddressHex = shortAddressHex;
      this.serialNoKeyHex = serialNoKeyHex;
      this.viewKeyHex = viewKeyHex;
      this.addressHex = addressHex;
    }

    public ViewerAccountRow(Account account) {
      this(account.getShortAddress().toHex(), account.getSerialNoKey().toHex(), account.getViewKey().toHex(),
          account.getAddress().toHex());
    }
  }

  private Dao<ViewerAccountRow, String> dao;

  public ViewerAccountTable(ConnectionSource connectionSource) throws SQLException {
    dao = DaoManager.createDao(connectionSource, ViewerAccountRow.class);
    TableUtils.createTableIfNotExists(connectionSource, ViewerAccountRow.class);
  }

  public long getCount() throws SQLException {
    return dao.countOf();
  }

  public void addAccountIfNotExists(Account account) throws SQLException {
    if (account.isSignerAccount()) {
      throw new IllegalArgumentException("Signer account is not allowed to save to viewer account table.");
    }
    dao.createIfNotExists(new ViewerAccountRow(account));
  }

  public Account[] getAllViewerAccounts() throws SQLException {
    ViewerAccountRow[] rows = dao.queryForAll().toArray(new ViewerAccountRow[0]);
    Account[] accounts = new Account[rows.length];
    for (int i = 0; i < rows.length; i++) {
      ViewerAccountRow row = rows[i];
      ShortAbelAddress shortAddress = new ShortAbelAddress(row.shortAddressHex);
      try {
        accounts[i] = Account.importViewerAccount(shortAddress.getChainID(), new SerialNoKey(Bytes.fromHex(row.serialNoKeyHex)),
            new ViewKey(Bytes.fromHex(row.viewKeyHex)), new AbelAddress(Bytes.fromHex(row.addressHex)));
      } catch (AbelException e) {
        throw new RuntimeException(e);
      }
    }
    return accounts;
  }
}
