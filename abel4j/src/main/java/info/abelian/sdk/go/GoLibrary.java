package info.abelian.sdk.go;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

// Functions exported by the Go library
public interface GoLibrary extends Library {

  public Pointer LoadResourceAccountCryptoSeed(Object... params);

  public Pointer GenerateSafeCryptoSeed(Object... params);

  public Pointer GenerateCryptoKeysAndAddress(Object... params);

  public Pointer GetAbelAddressFromCryptoAddress(Object... params);

  public Pointer GetCryptoAddressFromAbelAddress(Object... params);

  public Pointer GetShortAbelAddressFromAbelAddress(Object... params);

  public Pointer DecodeFingerprintFromTxVoutScript(Object... params);

  public Pointer DecodeCoinValueFromTxVoutScript(Object... params);

  public Pointer GenerateUnsignedRawTxData(Object... params);

  public Pointer GenerateSignedRawTxData(Object... params);

  public Pointer GenerateCoinSerialNumber(Object... params);
}
