package info.abelian.sdk.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.ContentType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;

public class AbecRPCClient extends AbelBase {

  private static final Map<String, AbecRPCClient> instances = new HashMap<>();

  public static AbecRPCClient getInstance(String chainName, String serverName) {
    chainName = chainName == null ? getDefaultChainName() : chainName;
    serverName = serverName == null ? "default" : serverName;
    
    String instanceName = chainName + "." + serverName;
    if (!instances.containsKey(instanceName)) {
      Properties abecConf = getConf(chainName + ".abec.rpc." + serverName + ".");
      String endpoint = abecConf.getProperty("endpoint");
      String username = abecConf.getProperty("username");
      String password = abecConf.getProperty("password");
      AbecRPCClient instance = new AbecRPCClient(endpoint, username, password);
      instances.put(instanceName, instance);
    }

    return instances.get(instanceName);
  }

  public static AbecRPCClient getInstance(String chainName) throws AbelException {
    return getInstance(chainName, null);
  }

  public static AbecRPCClient getInstance() throws AbelException {
    return getInstance(null, null);
  }

  public static AbecRPCClient getInstance(int chainID, String serverName) throws AbelException {
    return getInstance(getChainName(chainID), serverName);
  }

  public static AbecRPCClient getInstance(int chainID) throws AbelException {
    return getInstance(getChainName(chainID), null);
  }

  private String endpoint;

  private String encodedAuth;

  public AbecRPCClient(String endpoint, String username, String password) {
    this.endpoint = endpoint;
    this.encodedAuth = "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
  }

  public String getEndpoint() {
    return endpoint;
  }

  private Request buildRequest(String uuid, String method, Object... params) throws AbelException {
    // Define JSON-RPC request body.
    Map<String, Object> req = new HashMap<String, Object>() {
      {
        put("id", uuid);
        put("jsonrpc", "1.0");
        put("method", method);
        put("params", params);
      }
    };

    // Build HTTP request.
    String requestBody = toJSONString(req, null, false);
    if (requestBody == null) {
      throw new AbelException("Failed to serialize JSON-RPC request body.");
    }
    
    return Request.post(endpoint).addHeader("Authorization", encodedAuth)
        .bodyString(requestBody, ContentType.APPLICATION_JSON);
  }

  public Response execute(String method, Object... params) throws AbelException {
    String uuid = UUID.randomUUID().toString();
    Request req = buildRequest(uuid, method, params);
    try {
      return req.execute();
    } catch (IOException e) {
      throw new AbelException(e);
    }
  }

  public static class JsonResponse extends AbelBase {

    public static class Error {
      public int code;
      public String message;

      public Error(int code, String message) {
        this.code = code;
        this.message = message;
      }
      
      public Error(String message) {
        this(-1, message);
      }

      public String toString() {
        return String.format("(%d, \"%s\")", code, message);
      }
    }

    private String id;

    private Error error;

    private JsonNode result;

    public static JsonResponse createFromAbecRPCResponse(String id, String jsonBody) {
      try {
        JsonNode resp = new ObjectMapper().readTree(jsonBody);
        JsonNode result = resp.get("result");

        Error error = null;
        JsonNode errorNode = resp.get("error");
        if (!errorNode.isNull()) {
          JsonNode codeNode = errorNode.get("code");
          int code = codeNode == null ? -1 : codeNode.asInt();

          JsonNode messageNode = errorNode.get("message");
          String message = messageNode == null ? null : messageNode.asText();

          error = new Error(code, message);
        }

        return new JsonResponse(id, result, error);
      } catch (IOException e) {
        return new JsonResponse(id, null, e.getMessage());
      }
    }

    public JsonResponse(String id, JsonNode result, Error error) {
      this.id = id;
      this.result = result;
      this.error = error;
    }

    public JsonResponse(String id, JsonNode result, String errorMessage) {
      this.id = id;
      this.result = result;
      this.error = errorMessage == null || errorMessage.length() == 0 ? null : new Error(-1, errorMessage);
    }

    public String getID() {
      return id;
    }

    public Error getError() {
      return error;
    }

    public int getErrorCode() {
      return error == null ? 0 : error.code;
    }

    public String getErrorMessage() {
      return error == null ? null : error.message;
    }

    public JsonNode getResult() {
      return result;
    }

    public String getResultAsString() {
      return result == null ? null : result.toString();
    }

    public boolean hasError() {
      return error != null;
    }

    public boolean hasResult() {
      return result != null;
    }

    public boolean checkError() {
      if (hasError()) {
        LOG.error("Abec RPC error: " + error);
        return true;
      }
      return false;
    }

    public String toString() {
      return String.format("{id: %s, result: %s, error: %s}", id, result, error);
    }
  }

  public JsonResponse call(String method, Object... params) {
    String uuid = UUID.randomUUID().toString();
    try {
      Request req = buildRequest(uuid, method, params);
      LOG.debug("Calling Abec RPC: " + endpoint + "/" + method);
      String jsonBody = req.execute().returnContent().asString();
      return JsonResponse.createFromAbecRPCResponse(uuid, jsonBody);
    } catch (Exception e) {
      LOG.error("Failed to call Abec RPC method: " + method, e);
      return new JsonResponse(uuid, null, e.getMessage());
    }
  }

  public ChainInfo getChainInfo() {
    JsonResponse resp = call("getinfo");
    if (resp.checkError()) {
      return null;
    }

    ChainInfo ci = new ChainInfo();
    ci.rpc = endpoint;
    ci.height = resp.getResult().get("blocks").asLong();
    ci.difficulty = resp.getResult().get("difficulty").asLong();
    return ci;
  }

  public Bytes getBlockHash(long height) {
    JsonResponse resp = call("getblockhash", height);
    if (resp.checkError()) {
      return null;
    }

    return new Bytes(resp.getResult().asText());
  }

  public BlockInfo getBlockInfo(Bytes blockHash) {
    JsonResponse resp = call("getblockabe", blockHash.toHex(), 1);
    if (resp.checkError()) {
      return null;
    }

    BlockInfo bi = new BlockInfo();
    bi.height = resp.getResult().get("height").asLong();
    bi.hash = new Bytes(resp.getResult().get("hash").asText());
    bi.prevHash = new Bytes(resp.getResult().get("previousblockhash").asText());
    bi.time = resp.getResult().get("time").asLong();

    JsonNode txs = resp.getResult().get("tx");
    bi.txHashes = new Bytes[txs.size()];
    for (int i = 0; i < txs.size(); i++) {
      bi.txHashes[i] = new Bytes(txs.get(i).asText());
    }

    return bi;
  }

  public BlockInfo getBlockInfo(long height) {
    Bytes blockHash = getBlockHash(height);
    if (blockHash == null) {
      return null;
    }
    return getBlockInfo(blockHash);
  }

  public Bytes getBlockBytes(Bytes blockHash) {
    JsonResponse resp = call("getblockabe", blockHash.toHex(), 0);
    if (resp.checkError()) {
      return null;
    }
    return new Bytes(resp.getResult().asText());
  }

  public Bytes getBlockBytes(long height) {
    Bytes blockHash = getBlockHash(height);
    if (blockHash == null) {
      return null;
    }
    return getBlockBytes(blockHash);
  }

  public TxInfo getTxInfo(Bytes txid) {
    JsonResponse resp = call("getrawtransaction", txid.toHex(), true);
    if (resp.checkError()) {
      return null;
    }

    TxInfo ti = new TxInfo();
    ti.txid = new Bytes(resp.getResult().get("txid").asText());

    JsonNode timeNode = resp.getResult().get("time");
    ti.time = timeNode == null ? -1 : timeNode.asLong();

    JsonNode blockHashNode = resp.getResult().get("blockhash");
    ti.blockHash = blockHashNode == null ? null : new Bytes(blockHashNode.asText());

    JsonNode blockTimeNode = resp.getResult().get("blocktime");
    ti.blockTime = blockTimeNode == null ? -1 : blockTimeNode.asLong();

    JsonNode confirmationsNode = resp.getResult().get("confirmations");
    ti.confirmations = confirmationsNode == null ? -1 : confirmationsNode.asLong();
    
    JsonNode vins = resp.getResult().get("vin");
    ti.vins = new TxVin[vins.size()];
    for (int i = 0; i < vins.size(); i++) {
      JsonNode vin = vins.get(i);
      ti.vins[i] = new TxVin();
      ti.vins[i].serialNumber = new Bytes(vin.get("serialnumber").asText());
    }

    JsonNode vouts = resp.getResult().get("vout");
    ti.vouts = new TxVout[vouts.size()];
    for (int i = 0; i < vouts.size(); i++) {
      JsonNode vout = vouts.get(i);
      ti.vouts[i] = new TxVout();
      ti.vouts[i].n = vout.get("n").asLong();
      ti.vouts[i].script = new Bytes(vout.get("script").asText());
    }

    return ti;
  }

  public boolean sendRawTx(Bytes rawTx) {
    JsonResponse resp = call("sendrawtransactionabe", rawTx.toHex());
    if (resp.checkError()) {
      return false;
    }
    return true;
  }
}
