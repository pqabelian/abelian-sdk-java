package info.abelian.sdk.go;

import java.util.Arrays;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class GoResponse {
  public String funcName;
  public DataItem retVal;
  public DataItem[] results;

  public static GoResponse create(GoRequest req, Object goRetVal, Object[] goParams) {
    Object retVal = null;
    DataItemType retValType = null;
    DataItem[] results = req.reclaimGoParams(goParams);
    if (req.retValType == DataItemType.V_BYTE_BUFFER) {
      retValType = DataItemType.BYTE_ARRAY;
      Pointer retPtr = (Pointer) goRetVal;
      if (retPtr != null) {
        int pbSize = retPtr.getInt(0);
        retVal = retPtr.getByteArray(4, pbSize);
        Native.free(Pointer.nativeValue(retPtr));
      }
    } else if (req.retValType == DataItemType.STRING) {
      retValType = DataItemType.STRING;
      Pointer retPtr = (Pointer) goRetVal;
      retVal = retPtr.getString(0);
      Native.free(Pointer.nativeValue(retPtr));
    } else if (!req.retValType.isArray()) {
      retValType = req.retValType;
      retVal = goRetVal;
    } else {
      throw new IllegalArgumentException("Unsupported return type: " + req.retValType);
    }
    return new GoResponse(req.funcName, new DataItem("retVal", retValType, retVal), results);
  }

  private GoResponse(String funcName, DataItem retVal, DataItem[] results) {
    this.funcName = funcName;
    this.retVal = retVal;
    this.results = results;
  }

  public String toString() {
    return String.format("GoResponse(%s, %s, %s)", this.funcName, this.retVal, Arrays.toString(this.results));
  }

  public DataItem getRetVal() {
    return retVal;
  }

  public String[] getResultNames() {
    String[] names = new String[results.length];
    for (int i = 0; i < results.length; i++) {
      names[i] = results[i].name;
    }
    return names;
  }

  public DataItem getResult(String name) {
    for (DataItem result : results) {
      if (result.name.equals(name)) {
        return result;
      }
    }
    return null;
  }

  public byte[] getRetValAsByteArray() {
    return retVal.asByteArray();
  }

  public byte[] getResultAsByteArray(String name) {
    return getResult(name).asByteArray();
  }

  public boolean isRetValNull() {
    return retVal.value == null;
  }
}
