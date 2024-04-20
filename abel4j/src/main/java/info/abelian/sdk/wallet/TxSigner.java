package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.ShortAbelAddress;
import info.abelian.sdk.proto.Core.GenerateSignedRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateSignedRawTxDataResult;

public class TxSigner extends Wallet {

  private int accountsChainID = -1;

  public TxSigner(Account[] accounts) {
    super(null, accounts);
  }

  @Override
  protected boolean acceptAccount(Account account) {
    if (accounts.size() == 0) {
      accountsChainID = account.getChainID();
    } else if (account.getChainID() != accountsChainID) {
      LOG.warn("Account chain ID does not match other accounts in this wallet.");
      return false;
    }
    return account.isSignerAccount();
  }

  public int getAccountsChainID() {
    if (accounts.size() == 0) {
      LOG.warn("Cannot determine accounts chain ID as there is no account in this wallet.");
    }
    return accountsChainID;
  }

  public SignedRawTx sign(UnsignedRawTx unsignedRawTx) throws AbelException {
    try {
      GenerateSignedRawTxDataArgs args = buildGoFuncArgs(unsignedRawTx);
      GenerateSignedRawTxDataResult result = getGoProxy().goGenerateSignedRawTxData(args);
      return new SignedRawTx(new Bytes(result.getData().toByteArray()),
          new Bytes(result.getTxid().toByteArray()));
    } catch (Exception e) {
      throw new AbelException(e);
    }
  }

  protected GenerateSignedRawTxDataArgs buildGoFuncArgs(UnsignedRawTx unsignedRawTx) {
    GenerateSignedRawTxDataArgs.Builder builder = GenerateSignedRawTxDataArgs.newBuilder();

    builder.setUnsignedRawTxData(ByteString.copyFrom(unsignedRawTx.data.getData()));

    for (ShortAbelAddress signerShortAddress : unsignedRawTx.signers) {
      builder.addSignerShortAddresses(ByteString.copyFrom(signerShortAddress.getData()));

      Account signerAccount = getAccount(signerShortAddress);
      if (signerAccount == null) {
        LOG.error("Signer account not found: %s.", signerShortAddress);
        return null;
      }
      builder.addSignerCryptoSeeds(ByteString.copyFrom(signerAccount.getPrivateKey().getData()));
    }

    return builder.build();
  }
}
