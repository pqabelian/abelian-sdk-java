package info.abelian.sdk.wallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.common.Struct;
import info.abelian.sdk.proto.Core.BlockDescMessage;
import info.abelian.sdk.proto.Core.GenerateUnsignedRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateUnsignedRawTxDataResult;
import info.abelian.sdk.proto.Core.TxInDescMessage;
import info.abelian.sdk.proto.Core.TxOutDescMessage;
import info.abelian.sdk.rpc.BlockInfo;

public class TxBuilder extends AbelBase {

  public static final long DEFAULT_TX_FEE = 1000000;

  public static class TxInDesc extends Struct {
    public Coin coin;

    public TxInDesc(Coin coin) {
      this.coin = coin;
    }

    public TxInDescMessage toProto() {
      TxInDescMessage.Builder builder = TxInDescMessage.newBuilder();
      builder.setScript(ByteString.copyFrom(coin.script.getData()));
      builder.setValue(coin.value);
      builder.setShortAbelAddress(ByteString.copyFrom(coin.ownerShortAddress.getData()));
      builder.setHeight(coin.blockHeight);
      builder.setTxid(ByteString.copyFrom(coin.id.txid.getData()));
      builder.setIndex(coin.id.index);
      return builder.build();
    }
  }

  public static class TxOutDesc extends Struct {
    public AbelAddress address;
    public long value;

    public TxOutDesc(AbelAddress address, long value) {
      this.address = address;
      this.value = value;
    }

    public TxOutDescMessage toProto() {
      TxOutDescMessage.Builder builder = TxOutDescMessage.newBuilder();
      builder.setAbelAddress(ByteString.copyFrom(address.getData()));
      builder.setValue(value);
      return builder.build();
    }
  }

  public static class TxBlockDesc extends Struct {
    public Bytes binData;
    public long height;

    public TxBlockDesc(Bytes binData, long height) {
      this.binData = binData;
      this.height = height;
    }

    public BlockDescMessage toProto() {
      BlockDescMessage.Builder builder = BlockDescMessage.newBuilder();
      builder.setBinData(ByteString.copyFrom(binData.getData()));
      builder.setHeight(height);
      return builder.build();
    }
  }

  protected ChainViewer viewer;

  protected ArrayList<TxInDesc> txInDescs = new ArrayList<TxInDesc>();

  protected ArrayList<TxOutDesc> txOutDescs = new ArrayList<TxOutDesc>();

  protected ArrayList<TxBlockDesc> txRingBlockDescs = new ArrayList<TxBlockDesc>();

  protected long txFee = DEFAULT_TX_FEE;

  public TxBuilder(ChainViewer viewer) {
    super();
    this.viewer = viewer;
  }

  public TxInDesc[] getTxInDescs() {
    return txInDescs.toArray(new TxInDesc[0]);
  }

  public TxOutDesc[] getTxOutDescs() {
    return txOutDescs.toArray(new TxOutDesc[0]);
  }

  public TxBlockDesc[] getTxRingBlockDescs() {
    return txRingBlockDescs.toArray(new TxBlockDesc[0]);
  }

  public long getTxFee() {
    return txFee;
  }

  public TxBuilder addInput(TxInDesc txInDesc) {
    txInDescs.add(txInDesc);
    return this;
  }

  public TxBuilder addInputs(TxInDesc[] txInDescs) {
    for (TxInDesc txInDesc : txInDescs) {
      addInput(txInDesc);
    }
    return this;
  }

  public TxBuilder addOutput(TxOutDesc txOutDesc) {
    txOutDescs.add(txOutDesc);
    return this;
  }

  public TxBuilder addOutputs(TxOutDesc[] txOutDescs) {
    for (TxOutDesc txOutDesc : txOutDescs) {
      addOutput(txOutDesc);
    }
    return this;
  }

  public TxBuilder setTxFee(long txFee) {
    this.txFee = txFee;
    return this;
  }

  public UnsignedRawTx build() throws AbelException {
    if (!verifyAndCompleteTxInDescs()) {
      return null;
    }

    if (!fillRingBlockDescs()) {
      return null;
    }

    GenerateUnsignedRawTxDataArgs args = buildGoFuncArgs();
    GenerateUnsignedRawTxDataResult result;
    try {
      result = getGoProxy().goGenerateUnsignedRawTxData(args);
    } catch (Exception e) {
      throw new AbelException(e);
    }

    ArrayList<ShortAbelAddress> signers = new ArrayList<ShortAbelAddress>();
    for (ByteString signerShortAddressData : result.getSignerShortAddressesList()) {
      signers.add(new ShortAbelAddress(signerShortAddressData.toByteArray()));
    }

    return new UnsignedRawTx(new Bytes(result.getData().toByteArray()), signers.toArray(new ShortAbelAddress[0]));
  }

  protected boolean verifyAndCompleteTxInDescs() {
    // Fetch coin data from chain viewer and use it to verify the current coin data.
    for (int i = 0; i < txInDescs.size(); i++) {
      Coin coinGiven = txInDescs.get(i).coin;
      Coin coinOnChain = viewer.getCoin(coinGiven.id);

      if (coinOnChain == null) {
        LOG.error("Coin data verification failed. Cannot find coin on chain: {}.", coinGiven.id);
        return false;
      }

      if (!coinOnChain.ownerShortAddress.equals(coinGiven.ownerShortAddress)) {
        LOG.error("Coin data verification failed. Owner address mismatch: {} != {}.", coinGiven.ownerShortAddress,
            coinOnChain.ownerShortAddress);
        return false;
      }

      if (coinOnChain.value != coinGiven.value) {
        LOG.error("Coin data verification failed. Value mismatch: {} != {}.", coinGiven.value, coinOnChain.value);
        return false;
      }

      // Replace the given coin data with the one fetched from chain viewer.
      txInDescs.set(i, new TxInDesc(coinOnChain));
    }

    return true;
  }

  protected boolean fillRingBlockDescs() {
    // Calculate the ring block heights.
    HashSet<Long> coinBlockHeights = new HashSet<Long>();
    for (TxInDesc txInDesc : txInDescs) {
      coinBlockHeights.add(txInDesc.coin.blockHeight);
    }

    HashSet<Long> ringBlockHeights = new HashSet<Long>();
    for (long height : coinBlockHeights) {
      long[] coinRingBlockHeights = getRingBlockHeights(height);
      for (long coinRingBlockHeight : coinRingBlockHeights) {
        ringBlockHeights.add(coinRingBlockHeight);
      }
    }

    Long[] sortedRingBlockHeights = ringBlockHeights.toArray(new Long[0]);
    Arrays.sort(sortedRingBlockHeights);
    LOG.debug("Ring block heights: {}.", (Object) sortedRingBlockHeights);

    // Fetch ring block data from chain viewer.
    for (long height : sortedRingBlockHeights) {
      BlockInfo blockInfo = viewer.getSafeBlockInfo(height);
      if (blockInfo == null) {
        LOG.error("Failed to fetch block info at height {}.", height);
        return false;
      }
      Bytes binData = viewer.getAbecRPCClient().getBlockBytes(blockInfo.hash);
      if (binData == null) {
        LOG.error("Failed to fetch block bytes at height {}.", height);
        return false;
      }
      txRingBlockDescs.add(new TxBlockDesc(binData, height));
    }

    return true;
  }

  protected GenerateUnsignedRawTxDataArgs buildGoFuncArgs() {
    GenerateUnsignedRawTxDataArgs.Builder builder = GenerateUnsignedRawTxDataArgs.newBuilder();
    for (TxInDesc txInDesc : txInDescs) {
      builder.addTxInDescs(txInDesc.toProto());
    }
    for (TxOutDesc txOutDesc : txOutDescs) {
      builder.addTxOutDescs(txOutDesc.toProto());
    }
    for (TxBlockDesc txBlockDesc : txRingBlockDescs) {
      builder.addTxRingBlockDescs(txBlockDesc.toProto());
    }
    builder.setTxFee(txFee);
    return builder.build();
  }
}
