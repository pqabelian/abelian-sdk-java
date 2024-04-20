package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;

public abstract class CryptoKey extends Bytes {

  public CryptoKey(byte[] data) {
    super(data);
    if (!isValid()) {
      throw new IllegalArgumentException("Invalid crypto key.");
    }
  }

  public CryptoKey(Bytes key) {
    this(key.getData());
  }
  
  protected abstract boolean isValid();

  protected abstract boolean isSecret();

  public String toString() {
    if (isSecret()) {
      return String.format("[%d bytes|********...********]", getLength());
    }
    return super.toString();
  }

  public static class PrivateKey extends CryptoKey {

    public static final int PRIVATE_KEY_LENGTH = 132;

    public static final PrivateKey ZERO_PRIVATE_KEY = new PrivateKey(new byte[PRIVATE_KEY_LENGTH]);

    public PrivateKey(byte[] data) {
      super(data);
    }

    public PrivateKey(Bytes key) {
      super(key);
    }

    @Override
    protected boolean isValid() {
      return getLength() == PRIVATE_KEY_LENGTH;
    }

    @Override
    protected boolean isSecret() {
      return true;
    }
  }

  public static class SpendKey extends CryptoKey {

    public static final int SPEND_KEY_LENGTH = 1540;

    public SpendKey(byte[] data) {
      super(data);
    }

    public SpendKey(Bytes key) {
      super(key);
    }

    @Override
    protected boolean isValid() {
      return getLength() == SPEND_KEY_LENGTH;
    }

    @Override
    protected boolean isSecret() {
      return true;
    }
  }

  public static class SerialNoKey extends CryptoKey {

    public static final int SERIAL_NO_KEY_LENGTH = 1060;

    public SerialNoKey(byte[] data) {
      super(data);
    }

    public SerialNoKey(Bytes key) {
      super(key);
    }

    @Override
    protected boolean isValid() {
      return getLength() == SERIAL_NO_KEY_LENGTH;
    }

    @Override
    protected boolean isSecret() {
      return false;
    }
  }

  public static class ViewKey extends CryptoKey {

    public static final int VIEW_KEY_LENGTH = 2408;

    public ViewKey(byte[] data) {
      super(data);
    }

    public ViewKey(Bytes key) {
      super(key);
    }

    @Override
    protected boolean isValid() {
      return getLength() == VIEW_KEY_LENGTH;
    }

    @Override
    protected boolean isSecret() {
      return false;
    }
  }
}
