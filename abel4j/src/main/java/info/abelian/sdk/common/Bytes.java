package info.abelian.sdk.common;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Bytes {

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String toHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static byte[] fromHex(String hex) {
    if (hex == null) {
      return null;
    }
    if (hex.length() % 2 != 0) {
      throw new IllegalArgumentException("Hex string must have even length");
    }
    byte[] data = new byte[hex.length() / 2];
    for (int i = 0; i < hex.length(); i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }
  
  @JsonSerialize(using = BytesToStringSerializer.class)
  private byte[] data;

  public Bytes(byte[] data) {
    this.data = data.clone();
  }

  public Bytes(String hex) {
    this(fromHex(hex));
  }

  public Bytes(Bytes template) {
    this(template.getData());
  }

  public byte[] getData() {
    return data;
  };

  public int getLength() {
    return data.length;
  }

  public String toHex() {
    return toHex(data);
  }

  public byte getByte(int offset) {
    return data[offset];
  }

  public byte[] getSubData(int offset, int length) {
    return Arrays.copyOfRange(data, offset, offset + length);
  }

  public Bytes getSubBytes(int offset, int length) {
    return new Bytes(getSubData(offset, length));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof byte[]) {
      return Arrays.equals(data, (byte[]) obj);
    } else if (obj instanceof Bytes) {
      return Arrays.equals(data, ((Bytes) obj).data);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }

  @Override
  public String toString() {
    if (data == null) {
      return "null";
    }
    if (data.length == 0) {
      return "[]";
    }

    String hex;
    if (data.length <= 32) {
      hex = toHex(data);
    } else {
      hex = String.format("%s...%s", toHex(Arrays.copyOfRange(data, 0, 16)),
          toHex(Arrays.copyOfRange(data, data.length - 16, data.length)));
    }
    return String.format("[%d bytes|0x%s]", data.length, hex);
  }

  public Bytes md5() {
    try {
      return new Bytes(MessageDigest.getInstance("MD5").digest(data));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static class BytesToStringSerializer extends StdSerializer<byte[]> {

    public BytesToStringSerializer() {
        super(byte[].class);
    }

    protected BytesToStringSerializer(Class<byte[]> t) {
        super(t);
    }

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(Bytes.toHex(value));
    }
  }
}
