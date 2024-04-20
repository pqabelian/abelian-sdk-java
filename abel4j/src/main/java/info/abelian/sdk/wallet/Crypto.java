package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Fingerprint;
import info.abelian.sdk.proto.Core.DecodeFingerprintFromTxVoutScriptArgs;
import info.abelian.sdk.proto.Core.DecodeFingerprintFromTxVoutScriptResult;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressResult;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;

public class Crypto extends AbelBase {

  public static Bytes generateSeed() throws AbelException {
    try {
      GenerateSafeCryptoSeedArgs args = GenerateSafeCryptoSeedArgs.newBuilder().build();
      GenerateSafeCryptoSeedResult result = getGoProxy().goGenerateSafeCryptoSeed(args);
      return new Bytes(result.getCryptoSeed().toByteArray());
    } catch (Exception e) {
      throw new AbelException(e);
    }
  }

  public static GenerateCryptoKeysAndAddressResult generateKeysAndAddress(Bytes seed) throws AbelException {
    try {
      GenerateCryptoKeysAndAddressArgs args = GenerateCryptoKeysAndAddressArgs.newBuilder()
          .setCryptoSeed(ByteString.copyFrom(seed.getData())).build();
      GenerateCryptoKeysAndAddressResult result = getGoProxy().goGenerateCryptoKeysAndAddress(args);
      return result;
    } catch (Exception e) {
      throw new AbelException(e);
    }
  }

  public static Fingerprint decodeFingerprintFromTxVoutScript(Bytes txVoutScript) throws AbelException {
    try {
      DecodeFingerprintFromTxVoutScriptArgs args = DecodeFingerprintFromTxVoutScriptArgs.newBuilder()
          .setTxVoutScript(ByteString.copyFrom(txVoutScript.getData())).build();
      DecodeFingerprintFromTxVoutScriptResult result = getGoProxy().goDecodeFingerprintFromTxVoutScript(args);
      return new Fingerprint(result.getFingerprint().toByteArray());
    } catch (Exception e) {
      throw new AbelException(e);
    }
  }
}
