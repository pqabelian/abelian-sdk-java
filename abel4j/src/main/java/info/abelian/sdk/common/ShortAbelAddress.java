package info.abelian.sdk.common;

public class ShortAbelAddress extends Address {

  public ShortAbelAddress(byte[] data) {
    super(data);
  }

  public ShortAbelAddress(String hex) {
    super(hex);
  }

  @Override
  protected void initialize() {
    super.initialize();
    String prefix = getSubBytes(0, 2).toHex().toLowerCase();
    if (!prefix.startsWith("abe")) {
      System.out.printf(toHex());
      throw new IllegalArgumentException("Short Abel address must start with 0xabe*.");
    }
  }

  @Override
  protected int getExpectedLength() {
    return 66;
  }

  @Override
  public int getChainID() {
    return getByte(1) - 0xe1;
  }

  @Override
  public Fingerprint getFingerprint() {
    return new Fingerprint(getSubData(2, 32));
  }

  public Bytes getAbelAddressChecksum() {
    return getSubBytes(34, 32);
  }
}
