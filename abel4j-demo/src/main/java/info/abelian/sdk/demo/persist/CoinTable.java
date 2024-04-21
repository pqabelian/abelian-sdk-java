package info.abelian.sdk.demo.persist;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.wallet.Coin;
import info.abelian.sdk.wallet.CoinID;

public class CoinTable {

  @DatabaseTable(tableName = "coin")
  public static class CoinRow {
    @DatabaseField(id = true)
    public String coinIDStr;

    @DatabaseField
    public String ownerShortAddressHex;

    @DatabaseField
    public long value;

    @DatabaseField
    public long blockHeight;

    @DatabaseField
    public String snHex;

    @DatabaseField
    public boolean isSpent;

    public CoinRow() {
    }

    public CoinRow(CoinID coinID, String ownerShortAddressHex, long value, long blockHeight, String snHex,
        boolean isSpent) {
      this.coinIDStr = coinID.toString();
      this.ownerShortAddressHex = ownerShortAddressHex;
      this.value = value;
      this.blockHeight = blockHeight;
      this.snHex = snHex;
      this.isSpent = isSpent;
    }

    public CoinRow(Coin coin) {
      this(coin.id, coin.ownerShortAddress.toHex(), coin.value, coin.blockHeight,
          coin.serialNumber == null ? null : coin.serialNumber.toHex(), false);
    }

    public CoinID getCoinID() {
      String[] parts = coinIDStr.split(":");
      return new CoinID(new Bytes(parts[0]), Integer.parseInt(parts[1]));
    }
  }

  private Dao<CoinRow, String> dao;

  public CoinTable(ConnectionSource connectionSource) throws SQLException {
    dao = DaoManager.createDao(connectionSource, CoinRow.class);
    TableUtils.createTableIfNotExists(connectionSource, CoinRow.class);
  }

  public long getCount() throws SQLException {
    return dao.countOf();
  }

  public void addCoinIfNotExists(Coin coin) throws SQLException {
    if (dao.idExists(coin.id.toString())) {
      return;
    }
    dao.createIfNotExists(new CoinRow(coin));
  }

  public Coin[] getAllUnspentCoins() throws SQLException {
    CoinRow[] rows = dao.queryBuilder().where().eq("isSpent", false).query().toArray(new CoinRow[0]);
    Coin[] coins = new Coin[rows.length];
    for (int i = 0; i < rows.length; i++) {
      CoinRow row = rows[i];
      coins[i] = createCoinFromRow(row);
    }
    return coins;
  }

  public Coin getCoinBySnHex(String snHex) throws SQLException {
    CoinRow row = dao.queryBuilder().where().eq("snHex", snHex).queryForFirst();
    return createCoinFromRow(row);
  }

  public void setCoinSpentStatus(String coinIDStr, boolean isSpent) throws SQLException {
    CoinRow row = dao.queryForId(coinIDStr);
    if (row == null) {
      return;
    }
    row.isSpent = isSpent;
    dao.update(row);
  }

  private Coin createCoinFromRow(CoinRow row) {
    if (row == null) {
      return null;
    }
    return new Coin(row.getCoinID(), new ShortAbelAddress(row.ownerShortAddressHex), null, row.value, null, null,
        row.blockHeight, row.snHex == null ? null : new Bytes(row.snHex));
  }
}
