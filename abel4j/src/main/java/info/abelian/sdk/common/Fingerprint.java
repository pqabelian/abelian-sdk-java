package info.abelian.sdk.common;

public class Fingerprint extends Bytes {

  public Fingerprint(byte[] data) {
    super(data);
    if (getData().length != 32) {
      throw new IllegalArgumentException("Fingerprint data must be 32 bytes long.");
    }
  }

  public Fingerprint(String hex) {
    this(fromHex(hex));
  }
}
