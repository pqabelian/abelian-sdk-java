package info.abelian.sdk.go;

public class GoRequest {
  public String funcName;
  public DataItemType retValType;
  public DataItem[] params;

  public GoRequest() {
  }

  public GoRequest(String funcName, DataItemType retValType, DataItem[] params) {
    this.funcName = funcName;
    this.retValType = retValType;
    this.params = params;
  }

  public Object[] createGoParams() {
    Object[] goParams = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      goParams[i] = params[i].createGoParam();
    }
    return goParams;
  }

  public DataItem[] reclaimGoParams(Object[] goParams) {
    DataItem[] results = new DataItem[goParams.length];
    for (int i = 0; i < goParams.length; i++) {
      results[i] = params[i].reclaimGoParam(goParams[i]);
    }
    return results;
  }
}
