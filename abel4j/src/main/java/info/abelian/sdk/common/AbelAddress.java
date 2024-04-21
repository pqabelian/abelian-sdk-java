package info.abelian.sdk.common;

import com.google.protobuf.ByteString;

import info.abelian.sdk.go.GoProxy;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressResult;

public class AbelAddress extends Address {

  private ShortAbelAddress shortAddress;

  public AbelAddress(byte[] data) {
    super(data);
  }

  public AbelAddress(String hex) {
    super(hex);
  }

  @Override
  protected void initialize() {
    super.initialize();
    try {
      GetShortAbelAddressFromAbelAddressArgs args = GetShortAbelAddressFromAbelAddressArgs.newBuilder()
          .setAbelAddress(ByteString.copyFrom(getData())).build();
      GetShortAbelAddressFromAbelAddressResult result = GoProxy.getInstance()
          .goGetShortAbelAddressFromAbelAddress(args);
      shortAddress = new ShortAbelAddress(result.getShortAbelAddress().toByteArray());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Abel address.", e);
    }
  }

  @Override
  public int getChainID() {
    return getByte(0);
  }

  @Override
  public Fingerprint getFingerprint() {
    return shortAddress.getFingerprint();
  }

  public ShortAbelAddress getShortAddress() {
    return shortAddress;
  }
}
