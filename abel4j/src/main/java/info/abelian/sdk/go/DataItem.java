package info.abelian.sdk.go;

import java.lang.reflect.Array;

import com.sun.jna.Memory;
import com.sun.jna.Native;

public class DataItem {
  // Fields
  public String name;
  public DataItemType type;
  public Object value;

  // Constructors
  public DataItem(String name, DataItemType type, Object value) {
    if (type.isMutable() && !(value instanceof Integer)) {
      throw new IllegalArgumentException("The value of mutable types must be the count of elements.");
    }

    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String toString() {
    String valueStr = null;
    if (this.type == DataItemType.VOID) {
      valueStr = "void";
    } else if (this.type == DataItemType.STRING) {
      valueStr = (String) this.value;
    } else if (this.type.isArray()) {
      int count = this.getLength();
      valueStr = "[";
      for (int i = 0; i < count; i++) {
        if (i > 0) {
          valueStr += ", ";
        }
        valueStr += Array.get(this.value, i);
      }
      valueStr += "]";
    } else {
      valueStr = this.value.toString();
    }
    return String.format("DataItem(%s, %s, %s)", this.name, this.type, valueStr);
  }

  public byte[] asByteArray() {
    return (byte[]) this.value;
  }

  public Object createGoParam() {
    if (this.type.equals(DataItemType.STRING)) {
      return new GoString.ByValue((String) this.value, this.getLength());
    } else if (this.type.isArray()) {
      if (this.getSize() == 0) {
        return new GoSlice.ByValue(null, 0);
      }
      Memory mem = new Memory(this.getSize());
      if (!this.type.isMutable()) {
        switch (this.type) {
          case BYTE_ARRAY:
            mem.write(0, (byte[]) this.value, 0, this.getLength());
            break;
          case SHORT_ARRAY:
            mem.write(0, (short[]) this.value, 0, this.getLength());
            break;
          case INT_ARRAY:
            mem.write(0, (int[]) this.value, 0, this.getLength());
            break;
          case LONG_ARRAY:
            mem.write(0, (long[]) this.value, 0, this.getLength());
            break;
          case FLOAT_ARRAY:
            mem.write(0, (float[]) this.value, 0, this.getLength());
            break;
          case DOUBLE_ARRAY:
            mem.write(0, (double[]) this.value, 0, this.getLength());
            break;
          default:
            throw new IllegalArgumentException("Unsupported array type: " + this.type);
        }
      }
      return new GoSlice.ByValue(mem, this.getLength());
    } else { // Primitive type
      return this.value;
    }
  }

  public DataItem reclaimGoParam(Object goParam) {
    if (this.type.equals(DataItemType.STRING)) {
      return new DataItem(this.name, this.type, ((GoString.ByValue) goParam).p);
    }
    
    if (!this.type.isArray()) { // Primitive type
      return new DataItem(this.name, this.type, goParam);
    }

    GoSlice.ByValue slice = (GoSlice.ByValue) goParam;
    if (slice.data == null) {
      return new DataItem(this.name, this.type, null);
    }
    int count = (int) slice.len;
    Object resultValue = null;
    DataItemType resultType = null;

    switch (this.type) {
      case BYTE_ARRAY:
      case BYTE_BUFFER:
        resultType = DataItemType.BYTE_ARRAY;
        resultValue = (Object) slice.data.getByteArray(0, count);
        break;
      case SHORT_ARRAY:
      case SHORT_BUFFER:
        resultType = DataItemType.SHORT_ARRAY;
        resultValue = (Object) slice.data.getShortArray(0, count);
        break;
      case INT_ARRAY:
      case INT_BUFFER:
        resultType = DataItemType.INT_ARRAY;
        resultValue = (Object) slice.data.getIntArray(0, count);
        break;
      case LONG_ARRAY:
      case LONG_BUFFER:
        resultType = DataItemType.LONG_ARRAY;
        resultValue = (Object) slice.data.getLongArray(0, count);
        break;
      case FLOAT_ARRAY:
      case FLOAT_BUFFER:
        resultType = DataItemType.FLOAT_ARRAY;
        resultValue = (Object) slice.data.getFloatArray(0, count);
        break;
      case DOUBLE_ARRAY:
      case DOUBLE_BUFFER:
        resultType = DataItemType.DOUBLE_ARRAY;
        resultValue = (Object) slice.data.getDoubleArray(0, count);
        break;
      case V_BYTE_BUFFER:
        resultType = DataItemType.BYTE_ARRAY;
        resultValue = (Object) slice.data.getByteArray(4, slice.data.getInt(0));
        break;
      default:
        throw new IllegalArgumentException("Cannot reclaim type: " + this.type);
    }

    // Close the memory allocated in createGoParam().
    ((Memory) slice.data).close();

    return new DataItem(this.name, resultType, resultValue);
  }

  private int getLength() {
    if (type.isMutable()) {
      return (Integer) value;
    } else if (type.getJavaType().isArray()) {
      return Array.getLength(value);
    } else if (type.getJavaType().equals(String.class)) {
      return ((String) value).length();
    } else {
      throw new IllegalArgumentException("Cannot get length of type: " + type);
    }
  }

  private long getSize() {
    if (type.isMutable()) {
      return (Integer) value * Native.getNativeSize(type.getNativeType());
    } else if (type.getJavaType().isArray()) {
      return Array.getLength(value) * Native.getNativeSize(type.getNativeType());
    } else if (type.getJavaType().equals(String.class)) {
      return ((String) value).length() * Native.getNativeSize(Character.class);
    } else {
      throw new IllegalArgumentException("Cannot get size of type: " + type);
    }
  }
}
