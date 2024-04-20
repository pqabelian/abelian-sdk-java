package info.abelian.sdk.common;

public class Struct {
  public String toString() {
    return AbelBase.toJSONString(this, false);
  }
}
