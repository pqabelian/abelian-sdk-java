package info.abelian.sdk.demo;

import java.util.Arrays;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.Bytes;

public class Utils {

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static String summary(Bytes bytes) {
    if (bytes == null) {
      return "null";
    }
    return summary(bytes.getData());
  }

  public static String summary(byte[] bytes) {
    if (bytes == null) {
      return "null";
    }
    if (bytes.length == 0) {
      return "[]";
    }

    String hex;
    if (bytes.length <= 32) {
      hex = bytesToHex(bytes);
    } else {
      hex = String.format("%s...%s", bytesToHex(Arrays.copyOfRange(bytes, 0, 16)),
          bytesToHex(Arrays.copyOfRange(bytes, bytes.length - 16, bytes.length)));
    }
    return String.format("[%d bytes|0x%s]", bytes.length, hex);
  }

  public static String summary(String str) {
    if (str == null) {
      return "null";
    }
    if (str.length() == 0) {
      return "\"\"";
    }

    String summary;
    if (str.length() <= 32) {
      summary = str;
    } else {
      summary = String.format("%s...%s", str.substring(0, 16), str.substring(str.length() - 16));
    }
    return String.format("[%d chars|%s]", str.length(), summary);
  }

  public static String summary(ByteString bs) {
    return summary(bs.toByteArray());
  }

  public static String summary(Object[] objs) {
    if (objs == null) {
      return "null";
    }
    if (objs.length == 0) {
      return "[]";
    }

    String[] summaries = new String[objs.length];
    for (int i = 0; i < objs.length; i++) {
      Object obj = objs[i];
      if (obj instanceof byte[]) {
        summaries[i] = summary((byte[]) obj);
      } else if (obj instanceof ByteString) {
        summaries[i] = summary((ByteString) obj);
      } else if (obj instanceof String) {
        summaries[i] = summary((String) obj);
      } else {
        summaries[i] = obj.toString();
      }
    }
    return String.format("[%d items|%s]", objs.length, String.join(", ", summaries));
  }
}
