package info.abelian.sdk.common;

public class AbelException extends Exception {
  public AbelException(Exception e) {
    super(e);
  }

  public AbelException(String msg) {
    super(msg);
  }
}