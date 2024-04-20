package info.abelian.sdk.common;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import info.abelian.sdk.go.GoProxy;

public abstract class AbelBase {

  public static final int MAINNET_CHAIN_ID = 0;
  public static final int TESTNET_CHAIN_ID = 2;
  public static final int SIMNET_CHAIN_ID = 3;

  public static String getChainName(int chainID) {
    switch (chainID) {
    case AbelBase.MAINNET_CHAIN_ID:
      return "mainnet";
    case AbelBase.TESTNET_CHAIN_ID:
      return "testnet";
    case AbelBase.SIMNET_CHAIN_ID:
      return "simnet";
    default:
      throw new IllegalArgumentException("Invalid chain ID: " + chainID);
    }
  }

  public static int getChainID(String chainName) {
    switch (chainName) {
    case "mainnet":
      return AbelBase.MAINNET_CHAIN_ID;
    case "testnet":
      return AbelBase.TESTNET_CHAIN_ID;
    case "simnet":
      return AbelBase.SIMNET_CHAIN_ID;
    default:
      throw new IllegalArgumentException("Invalid chain name: " + chainName);
    }
  }

  public static final int RING_SIZE = 3;

  public static long[] getRingBlockHeights(long height) {
    long firstRingBlockHeight = height - height % RING_SIZE;
    long[] ringBlockHeights = new long[RING_SIZE];
    for (int i = 0; i < RING_SIZE; i++) {
      ringBlockHeights[i] = firstRingBlockHeight + i;
    }
    return ringBlockHeights;
  }

  public static Logger LOG = LoggerFactory.getLogger("abel4j");
  
  public static final String DEFAULT_ABEL4J_ENV = System.getProperty("user.home") + "/.abel4j";

  private static final String ABEL4J_ENV = System.getenv("ABEL4J_ENV") != null
      ? System.getenv("ABEL4J_ENV") : DEFAULT_ABEL4J_ENV;

  static {
    File envDir = new File(ABEL4J_ENV);
    if (!envDir.exists()) {
      // Create abel4j env directory only when it is the default directory.
      if (ABEL4J_ENV.equals(DEFAULT_ABEL4J_ENV)) {
        envDir.mkdirs();
      } else {
        throw new RuntimeException("Specified abel4j env directory does not exist: " + ABEL4J_ENV);
      }
    }
  }

  private static final String DEFAULT_CONF_FILE = "abel4j-default.conf";

  private static final String USER_CONF_FILE = "abel4j.conf";

  private static Properties conf = new Properties();

  static {
    try {
      // Load default config.
      conf.load(AbelBase.class.getClassLoader().getResourceAsStream(DEFAULT_CONF_FILE));

      // Load user config.
      File userConfFile = new File(getEnvPath(USER_CONF_FILE));
      if (userConfFile.exists()) {
        conf.load(new FileInputStream(userConfFile));
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static String getEnvPath(String relPath) {
    return Paths.get(ABEL4J_ENV, relPath).toString();
  }

  public static Properties getConf() {
    return getConf("");
  }

  public static Properties getConf(String keyPrefix) {
    if (keyPrefix == null) {
      keyPrefix = "";
    }

    Properties subConf = new Properties();
    for (String key : conf.stringPropertyNames()) {
      if (key.startsWith(keyPrefix)) {
        subConf.setProperty(key.substring(keyPrefix.length()), conf.getProperty(key));
      }
    }
    return subConf;
  }

  public static String getDefaultChainName() {
    return conf.getProperty("default.chain");
  }

  public static int getDefaultChainID() {
    return getChainID(getDefaultChainName());
  }

  public static GoProxy getGoProxy() {
    return GoProxy.getInstance();
  }

  private static ObjectWriter jsonWriter = new ObjectMapper().writer();

  public static String toJSONString(Object obj, boolean prettyPrint) {
    return toJSONString(obj, "{ \"error\": \"Error converting to JSON.\"}", prettyPrint);
  }

  public static String toJSONString(Object obj, String valueOnError, boolean prettyPrint) {
    try {
      if (prettyPrint) {
        return jsonWriter.withDefaultPrettyPrinter().writeValueAsString(obj);
      } else {
        return jsonWriter.writeValueAsString(obj);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return valueOnError;
    }
  }

  public String toJSONString(boolean prettyPrint) {
    return toJSONString(this, prettyPrint);
  }

  public String toJSONString() {
    return toJSONString(this, false);
  }
}
