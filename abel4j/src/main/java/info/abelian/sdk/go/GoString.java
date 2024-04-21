package info.abelian.sdk.go;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class GoString extends Structure {
  public static class ByValue extends GoString implements Structure.ByValue {
    public ByValue() {
      super();
    }

    public ByValue(String p, long n) {
      super();
      this.p = p;
      this.n = n;
    }
  }

  public String p;
  public long n;

  protected List<String> getFieldOrder() {
    return Arrays.asList(new String[] { "p", "n" });
  }
}
