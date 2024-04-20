package info.abelian.sdk.go;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class GoSlice extends Structure {
  public static class ByValue extends GoSlice implements Structure.ByValue {
    public ByValue() {
      super();
    }

    public ByValue(Pointer data, long len) {
      super();
      this.data = data;
      this.len = len;
      this.cap = len;
    }

    public ByValue(Pointer data, long len, long cap) {
      super();
      this.data = data;
      this.len = len;
      this.cap = cap;
    }
  }

  public Pointer data;
  public long len;
  public long cap;

  protected List<String> getFieldOrder() {
    return Arrays.asList(new String[] { "data", "len", "cap" });
  }
}
