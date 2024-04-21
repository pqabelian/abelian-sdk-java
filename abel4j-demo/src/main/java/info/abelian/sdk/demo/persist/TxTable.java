package info.abelian.sdk.demo.persist;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.wallet.SignedRawTx;
import info.abelian.sdk.wallet.UnsignedRawTx;

public class TxTable {

  @DatabaseTable(tableName = "tx")
  public static class TxRow {
    @DatabaseField(id = true)
    public String txMd5;

    @DatabaseField
    public String unsignedRawTxDataHex;

    @DatabaseField
    public String signerShortAddresses;

    @DatabaseField
    public String signedRawTxDataHex;

    @DatabaseField
    public String txid;

    @DatabaseField
    public boolean isSubmitted;

    public TxRow() {
    }

    public TxRow(String txMd5, String unsignedRawTxDataHex, String signerShortAddresses, String signedRawTxDataHex,
        boolean isSubmitted) {
      this.txMd5 = txMd5;
      this.unsignedRawTxDataHex = unsignedRawTxDataHex;
      this.signerShortAddresses = signerShortAddresses;
      this.signedRawTxDataHex = signedRawTxDataHex;
      this.isSubmitted = isSubmitted;
    }
  }

  private Dao<TxRow, String> dao;

  public TxTable(ConnectionSource connectionSource) throws SQLException {
    dao = DaoManager.createDao(connectionSource, TxRow.class);
    TableUtils.createTableIfNotExists(connectionSource, TxRow.class);
  }

  public long getCount() throws SQLException {
    return dao.countOf();
  }

  public String addUnsignedRawTxIfNotExists(UnsignedRawTx unsignedRawTx) throws SQLException {
    String txMd5 = unsignedRawTx.data.md5().toHex();
    if (dao.idExists(txMd5)) {
      return txMd5;
    }

    String[] signerShortAddresses = new String[unsignedRawTx.signers.length];
    for (int i = 0; i < unsignedRawTx.signers.length; i++) {
      signerShortAddresses[i] = unsignedRawTx.signers[i].toHex();
    }

    TxRow txRow = new TxRow(txMd5, unsignedRawTx.data.toHex(), String.join(",", signerShortAddresses), null, false);
    dao.create(txRow);
    return txMd5;
  }

  public Map<String, UnsignedRawTx> getUnsignedRawTxs(int limit) throws SQLException {
    Map<String, UnsignedRawTx> unsignedRawTxs = new HashMap<>();

    TxRow[] txRows = dao.queryBuilder().limit((long) limit).where().isNull("signedRawTxDataHex").query().toArray(new TxRow[0]);
    if (txRows.length == 0) {
      return unsignedRawTxs;
    }
    
    for (TxRow txRow : txRows) {
      unsignedRawTxs.put(txRow.txMd5, createUnsignedRawTxFromTxRow(txRow));
    }
    
    return unsignedRawTxs;
  }

  public UnsignedRawTx getUnsignedRawTx(String txMd5) throws SQLException {
    TxRow txRow = dao.queryForId(txMd5);
    if (txRow == null || txRow.signedRawTxDataHex != null) {
      return null;
    }
    return createUnsignedRawTxFromTxRow(txRow);
  }

  public void updateSignedRawTx(String txMd5, SignedRawTx signedRawTx) throws SQLException {
    TxRow txRow = dao.queryForId(txMd5);
    if (txRow == null) {
      return;
    }

    txRow.signedRawTxDataHex = signedRawTx.data.toHex();
    txRow.txid = signedRawTx.txid.toHex();
    dao.update(txRow);
  }

  protected UnsignedRawTx createUnsignedRawTxFromTxRow(TxRow txRow) {
    if (txRow == null) {
      return null;
    }

    String[] signerShortAddresses = txRow.signerShortAddresses.split(",");
    ShortAbelAddress[] signers = new ShortAbelAddress[signerShortAddresses.length];
    for (int j = 0; j < signerShortAddresses.length; j++) {
      signers[j] = new ShortAbelAddress(signerShortAddresses[j]);
    }
    return new UnsignedRawTx(new Bytes(txRow.unsignedRawTxDataHex), signers);
  }

  public SignedRawTx getSignedRawTx(String txMd5) throws SQLException {
    TxRow txRow = dao.queryForId(txMd5);
    if (txRow == null || txRow.signedRawTxDataHex == null) {
      return null;
    }
    return new SignedRawTx(new Bytes(txRow.signedRawTxDataHex), new Bytes(txRow.txid));
  }

  public void setSubmitted(String txMd5) throws SQLException {
    TxRow txRow = dao.queryForId(txMd5);
    if (txRow == null) {
      return;
    }

    txRow.isSubmitted = true;
    dao.update(txRow);
  }
}
