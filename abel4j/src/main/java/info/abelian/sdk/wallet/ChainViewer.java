package info.abelian.sdk.wallet;

import java.util.ArrayList;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Fingerprint;
import info.abelian.sdk.go.GoProxy.AbelGoException;
import info.abelian.sdk.proto.Core.BlockDescMessage;
import info.abelian.sdk.proto.Core.DecodeCoinValueFromTxVoutScriptArgs;
import info.abelian.sdk.proto.Core.DecodeCoinValueFromTxVoutScriptResult;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberArgs;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberResult;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.rpc.ChainInfo;
import info.abelian.sdk.rpc.TxInfo;
import info.abelian.sdk.rpc.TxVout;

public class ChainViewer extends Wallet {

  private static final int RING_SIZE = 3;

  private long cachedLatestHeight = -1;

  private long lastUpdateTimestamp = 0;

  private int requiredConfirmations = 1;

  public ChainViewer(AbecRPCClient client) {
    this(client, new Account[0]);
  }

  public ChainViewer(AbecRPCClient client, Account[] accounts) {
    super(client, accounts);
  }

  @Override
  protected boolean acceptAccount(Account account) {
    return !account.isSignerAccount();
  }

  public int getRequiredConfirmations() {
    return requiredConfirmations;
  }

  public void setRequiredConfirmations(int requiredConfirmations) {
    this.requiredConfirmations = requiredConfirmations;
  }

  public boolean updateLatestHeight() {
    LOG.debug("Updating latest height.");
    ChainInfo chainInfo = client.getChainInfo();
    if (chainInfo == null) {
      LOG.debug("Failed to update chain height.");
      return false;
    }
    if (chainInfo.height < cachedLatestHeight) {
      LOG.warn("Updated chain height is lower than cached height.");
    }
    cachedLatestHeight = chainInfo.height;
    lastUpdateTimestamp = System.currentTimeMillis() / 1000;
    return true;
  }

  public long getLatestHeight() {
    long currentTimestamp = System.currentTimeMillis() / 1000;
    if (cachedLatestHeight < 0 || currentTimestamp - lastUpdateTimestamp > 10) {
      updateLatestHeight();
    } else {
      LOG.debug("Using cached latest height: {}.", cachedLatestHeight);
    }
    return cachedLatestHeight;
  }

  public long getLatestSafeHeight() {
    long latestConfirmedHeight = getLatestHeight() - requiredConfirmations;
    return latestConfirmedHeight - (latestConfirmedHeight + 1) % RING_SIZE;
  }

  public BlockInfo getSafeBlockInfo(long height) {
    if (height > getLatestSafeHeight()) {
      updateLatestHeight();
    }

    if (height > cachedLatestHeight) {
      LOG.warn("Block height ({}) is beyond the latest height ({}).", height, cachedLatestHeight);
      return null;
    } else if (height > getLatestSafeHeight()) {
      LOG.warn("Block height ({}) is not safe yet.", height);
      return null;
    } else {
      return client.getBlockInfo(height);
    }
  }

  public Account getOwnerAccount(Bytes txVoutScript) {
    try {
      Fingerprint fingerprint = Crypto.decodeFingerprintFromTxVoutScript(txVoutScript);
      return getAccount(fingerprint);
    } catch (Exception e) {
      LOG.error("Failed to decode fingerprint from tx vout script: {}.", e.getMessage());
      return null;
    }
  }

  public Coin[] getCoins(Bytes txid) {
    return createCoins(txid, -1);
  }

  public Coin getCoin(CoinID coinID) {
    Coin[] coins = createCoins(coinID.txid, coinID.index);
    if (coins.length == 0) {
      LOG.error("Failed to find coin for coin id: {}.", coinID);
      return null;
    } else if (coins.length == 1) {
      return coins[0];
    } else {
      LOG.error("Found more than one coin for coin id: {}.", coinID);
      return null;
    }
  }

  public void getCoinSerialNumbers(CoinID[] coinIDs) {
    return;
  }

  private Coin[] createCoins(Bytes txid, int voutIndex) {
    TxInfo txInfo = getAbecRPCClient().getTxInfo(txid);
    if (txInfo == null) {
      LOG.error("Failed to get tx info for txid: {}.", txid.toHex());
      return null;
    }

    BlockInfo blockInfo = getAbecRPCClient().getBlockInfo(txInfo.blockHash);
    if (blockInfo == null) {
      LOG.error("Failed to get block info for block hash: {}.", txInfo.blockHash.toHex());
      return null;
    }

    ArrayList<Coin> coins = new ArrayList<Coin>();
    for (int i = 0; i < txInfo.vouts.length; i++) {
      if (voutIndex < 0 || voutIndex == i) {
        Coin coin = createCoin(txid, i, txInfo.vouts[i], txInfo.blockHash, blockInfo.height);
        if (coin != null) {
          coins.add(coin);
        }
      }
    }

    return coins.toArray(new Coin[0]);
  }

  private Coin createCoin(Bytes txid, int voutIndex, TxVout vout, Bytes blockHash, long blockHeight) {
    Account ownerAccount = getOwnerAccount(vout.script);
    if (ownerAccount == null) {
      return null;
    }

    // Decode coin value.
    long value = -1;
    try {
      DecodeCoinValueFromTxVoutScriptArgs args = DecodeCoinValueFromTxVoutScriptArgs.newBuilder()
          .setTxVoutScript(ByteString.copyFrom(vout.script.getData()))
          .setViewSecretKey(ByteString.copyFrom(ownerAccount.getViewKey().getData())).build();
      DecodeCoinValueFromTxVoutScriptResult result = getGoProxy().goDecodeCoinValueFromTxVoutScript(args);
      value = result.getCoinValue();
    } catch (AbelGoException e) {
      LOG.error("Failed to decode coin value: {}.", e.getMessage());
      return null;
    }

    // Generate coin serial number.
    GenerateCoinSerialNumberArgs.Builder argsBuilder = GenerateCoinSerialNumberArgs.newBuilder();
    argsBuilder.setTxid(ByteString.copyFrom(txid.getData()));
    argsBuilder.setIndex(voutIndex);
    argsBuilder.setSerialNoSecretKey(ByteString.copyFrom(ownerAccount.getSerialNoKey().getData()));

    long[] ringBlockHeights = getRingBlockHeights(blockHeight);
    for (long ringBlockHeight : ringBlockHeights) {
      Bytes blockBinData = getAbecRPCClient().getBlockBytes(ringBlockHeight);
      if (blockBinData == null) {
        LOG.error("Failed to get ring block data at height: {}.", ringBlockHeight);
        return null;
      }
      BlockDescMessage.Builder blockDescMessageBuilder = BlockDescMessage.newBuilder();
      blockDescMessageBuilder.setBinData(ByteString.copyFrom(blockBinData.getData()));
      blockDescMessageBuilder.setHeight(ringBlockHeight);
      argsBuilder.addRingBlockDescs(blockDescMessageBuilder.build());
    }

    GenerateCoinSerialNumberArgs args = argsBuilder.build();

    Bytes serialNumber = null;
    try {
      GenerateCoinSerialNumberResult result = getGoProxy().goGenerateCoinSerialNumber(args);
      serialNumber = new Bytes(result.getSerialNumber().toByteArray());
    } catch (AbelGoException e) {
      LOG.error("Failed to calculate coin serial number: {}.", e.getMessage());
      return null;
    }

    // Create coin.
    return new Coin(new CoinID(txid, voutIndex), ownerAccount.getShortAddress(), ownerAccount.getAddress(), value,
        vout.script, blockHash, blockHeight, serialNumber);
  }
}