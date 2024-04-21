package info.abelian.sdk.common;

public abstract class Address extends Bytes {

  public Address(byte[] data) {
    super(data);
    initialize();
  }

  public Address(String hex) {
    this(fromHex(hex));
  }

  protected void initialize() {
    int expectedLength = getExpectedLength();
    if (expectedLength != -1 && getData().length != expectedLength) {
      throw new IllegalArgumentException(this.getClass().getName() + " data must be " + expectedLength + " bytes long.");
    }
  }

  protected int getExpectedLength() {
    return -1;
  }

  public abstract int getChainID();

  public abstract Fingerprint getFingerprint();
}