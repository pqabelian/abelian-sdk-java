package info.abelian.sdk.go;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessageV3;
import com.sun.jna.Native;

import info.abelian.sdk.proto.Core.DecodeCoinValueFromTxVoutScriptArgs;
import info.abelian.sdk.proto.Core.DecodeCoinValueFromTxVoutScriptResult;
import info.abelian.sdk.proto.Core.DecodeFingerprintFromTxVoutScriptArgs;
import info.abelian.sdk.proto.Core.DecodeFingerprintFromTxVoutScriptResult;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberArgs;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberResult;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressResult;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;
import info.abelian.sdk.proto.Core.GenerateSignedRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateSignedRawTxDataResult;
import info.abelian.sdk.proto.Core.GenerateUnsignedRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateUnsignedRawTxDataResult;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressArgs;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressResult;
import info.abelian.sdk.proto.Core.GetCryptoAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetCryptoAddressFromAbelAddressResult;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressResult;

public class GoProxy {

  // Exceptions
  public static class AbelGoException extends Exception {
    public AbelGoException(Exception e) {
      super(e);
    }
  }

  // Constants
  private static final Map<String, String> GO_LIB_PATHS = new HashMap<String, String>() {
    {
      put("macos-x64", "/native/macos-x64/libabelsdk.1.dylib");
      put("macos-arm64", "/native/macos-arm64/libabelsdk.1.dylib");
      put("linux-x64", "/native/linux-x64/libabelsdk.so.1");
      put("linux-arm64", "/native/linux-arm64/libabelsdk.so.1");
    }
  };

  // Singleton
  private static GoProxy instance;

  public static GoProxy getInstance() {
    if (instance == null) {
      instance = new GoProxy();
    }
    return instance;
  }

  private GoLibrary goLib;

  private GoProxy() {
    String goLibPath = getGoLibPath();
    try {
      File libFile = Native.extractFromResourcePath(goLibPath, Native.class.getClassLoader());
      String fileName = goLibPath.substring(goLibPath.lastIndexOf('/') + 1);
      File tmpLibFile = new File(System.getProperty("java.io.tmpdir"), fileName);
      Files.copy(libFile.toPath(), tmpLibFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      this.goLib = (GoLibrary) Native.load(tmpLibFile.getAbsolutePath(), GoLibrary.class);
      tmpLibFile.delete();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getGoLibPath() {
    String os = System.getProperty("os.name").toLowerCase();
    String arch = System.getProperty("os.arch").toLowerCase();

    if (os.contains("mac") || os.contains("darwin")) {
      os = "macos";
    } else if (os.contains("linux")) {
      os = "linux";
    } else {
      throw new RuntimeException("Unsupported OS: " + os);
    }

    if (arch.contains("x86_64") || arch.contains("amd64")) {
      arch = "x64";
    } else if (arch.contains("aarch64") || arch.contains("arm64")) {
      arch = "arm64";
    } else {
      throw new RuntimeException("Unsupported Arch: " + arch);
    }

    String goLibPath = GO_LIB_PATHS.get(os + "-" + arch);
    if (goLibPath == null) {
      throw new RuntimeException("Unsupported OS-Arch: " + os + "-" + arch);
    }
    return goLibPath;
  }

  public Method getGoFunc(String funcName) {
    Method goFunc = null;
    for (Method m : GoLibrary.class.getDeclaredMethods()) {
      if (m.getName().equals(funcName)) {
        goFunc = m;
        break;
      }
    }
    return goFunc;
  }

  public GoResponse callGoFunc(GoRequest req) throws AbelGoException {
    // Get Go func by name.
    Method goFunc = getGoFunc(req.funcName);

    // Create Go params.
    // NOTE: There might be new Memory objects create in Go params. They will be
    // closed by req.reclaimGoParams().
    Object[] goParams = req.createGoParams();

    // Call Go func with Go params.
    Object goRetVal;
    try {
      goRetVal = goFunc.invoke(goLib, new Object[] { goParams });
    } catch (Exception e) {
      throw new AbelGoException(e);
    }

    // Reclaim Go params and construct response.
    // NOTE: Any previously created Memory objects in goParams will be closed here.
    GoResponse resp = GoResponse.create(req, goRetVal, goParams);

    // Return Response.
    return resp;
  }

  public Object callProtoGoFunc(String goFuncName, GeneratedMessageV3 args, Class<?> resultClass)
      throws AbelGoException {
    byte[] argsData = args.toByteArray();
    GoResponse resp = callGoFunc(new GoRequest(goFuncName, DataItemType.V_BYTE_BUFFER, new DataItem[] {
        new DataItem("argsData", DataItemType.BYTE_ARRAY, argsData),
    }));
    try {
      Method parseFrom = resultClass.getDeclaredMethod("parseFrom", byte[].class);
      return parseFrom.invoke(null, (Object) resp.getRetValAsByteArray());
    } catch (Exception e) {
      throw new AbelGoException(e);
    }
  }

  public GoResponse goLoadResourceAccountCryptoSeed(int seqNo) throws AbelGoException {
    return callGoFunc(new GoRequest("LoadResourceAccountCryptoSeed", DataItemType.V_BYTE_BUFFER, new DataItem[] {
        new DataItem("seqNo", DataItemType.INT, seqNo),
    }));
  }

  public GenerateSafeCryptoSeedResult goGenerateSafeCryptoSeed(GenerateSafeCryptoSeedArgs args) throws AbelGoException {
    return (GenerateSafeCryptoSeedResult) callProtoGoFunc("GenerateSafeCryptoSeed", args,
        GenerateSafeCryptoSeedResult.class);
  }

  public GenerateCryptoKeysAndAddressResult goGenerateCryptoKeysAndAddress(GenerateCryptoKeysAndAddressArgs args)
      throws AbelGoException {
    return (GenerateCryptoKeysAndAddressResult) callProtoGoFunc("GenerateCryptoKeysAndAddress", args,
        GenerateCryptoKeysAndAddressResult.class);
  }

  public GetAbelAddressFromCryptoAddressResult goGetAbelAddressFromCryptoAddress(
      GetAbelAddressFromCryptoAddressArgs args) throws AbelGoException {
    return (GetAbelAddressFromCryptoAddressResult) callProtoGoFunc("GetAbelAddressFromCryptoAddress", args,
        GetAbelAddressFromCryptoAddressResult.class);
  }

  public GetCryptoAddressFromAbelAddressResult goGetCryptoAddressFromAbelAddress(
      GetCryptoAddressFromAbelAddressArgs args) throws AbelGoException {
    return (GetCryptoAddressFromAbelAddressResult) callProtoGoFunc("GetCryptoAddressFromAbelAddress", args,
        GetCryptoAddressFromAbelAddressResult.class);
  }

  public GetShortAbelAddressFromAbelAddressResult goGetShortAbelAddressFromAbelAddress(
      GetShortAbelAddressFromAbelAddressArgs args) throws AbelGoException {
    return (GetShortAbelAddressFromAbelAddressResult) callProtoGoFunc("GetShortAbelAddressFromAbelAddress", args,
        GetShortAbelAddressFromAbelAddressResult.class);
  }

  public DecodeFingerprintFromTxVoutScriptResult goDecodeFingerprintFromTxVoutScript(
      DecodeFingerprintFromTxVoutScriptArgs args) throws AbelGoException {
    return (DecodeFingerprintFromTxVoutScriptResult) callProtoGoFunc("DecodeFingerprintFromTxVoutScript", args,
        DecodeFingerprintFromTxVoutScriptResult.class);
  }

  public DecodeCoinValueFromTxVoutScriptResult goDecodeCoinValueFromTxVoutScript(DecodeCoinValueFromTxVoutScriptArgs args) throws AbelGoException {
    return (DecodeCoinValueFromTxVoutScriptResult) callProtoGoFunc("DecodeCoinValueFromTxVoutScript", args,
        DecodeCoinValueFromTxVoutScriptResult.class);
  }

  public GenerateUnsignedRawTxDataResult goGenerateUnsignedRawTxData(GenerateUnsignedRawTxDataArgs args) throws AbelGoException {
    return (GenerateUnsignedRawTxDataResult) callProtoGoFunc("GenerateUnsignedRawTxData", args,
        GenerateUnsignedRawTxDataResult.class);
  }

  public GenerateSignedRawTxDataResult goGenerateSignedRawTxData(GenerateSignedRawTxDataArgs args) throws AbelGoException {
    return (GenerateSignedRawTxDataResult) callProtoGoFunc("GenerateSignedRawTxData", args,
        GenerateSignedRawTxDataResult.class);
  }

  public GenerateCoinSerialNumberResult goGenerateCoinSerialNumber(GenerateCoinSerialNumberArgs args) throws AbelGoException {
    return (GenerateCoinSerialNumberResult) callProtoGoFunc("GenerateCoinSerialNumber", args,
        GenerateCoinSerialNumberResult.class);
  }
}
