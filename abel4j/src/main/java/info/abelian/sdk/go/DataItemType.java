package info.abelian.sdk.go;

public enum DataItemType {
  // Void type
  VOID("void", Void.class, null, false),

  // Primitive types
  BYTE("byte", Byte.class, Byte.TYPE, false),
  SHORT("short", Short.class, Short.TYPE, false),
  INT("int", Integer.class, Integer.TYPE, false),
  LONG("long", Long.class, Long.TYPE, false),
  FLOAT("float", Float.class, Float.TYPE, false),
  DOUBLE("double", Double.class, Double.TYPE, false),

  // String type
  STRING("string", String.class, null, false),

  // Array types
  BYTE_ARRAY("byte[]", Byte[].class, Byte.TYPE, false),
  SHORT_ARRAY("short[]", Short[].class, Short.TYPE, false),
  INT_ARRAY("int[]", Integer[].class, Integer.TYPE, false),
  LONG_ARRAY("long[]", Long[].class, Long.TYPE, false),
  FLOAT_ARRAY("float[]", Float[].class, Float.TYPE, false),
  DOUBLE_ARRAY("double[]", Double[].class, Double.TYPE, false),

  // Buffer types (fixed size)
  BYTE_BUFFER("ByteBuffer", Byte[].class, Byte.TYPE, true),
  SHORT_BUFFER("ShortBuffer", Short[].class, Short.TYPE, true),
  INT_BUFFER("IntBuffer", Integer[].class, Integer.TYPE, true),
  LONG_BUFFER("LongBuffer", Long[].class, Long.TYPE, true),
  FLOAT_BUFFER("FloatBuffer", Float[].class, Float.TYPE, true),
  DOUBLE_BUFFER("DoubleBuffer", Double[].class, Double.TYPE, true),

  // VBuffer type (variable size)
  V_BYTE_BUFFER("VByteBuffer", byte[].class, Byte.TYPE, true);

  // Fields
  private String name;
  private Class<?> javaType;
  private Class<?> nativeType;
  private boolean mutable;

  // Constructors
  private DataItemType(String name, Class<?> javaType, Class<?> nativeType, boolean mutable) {
    this.name = name;
    this.javaType = javaType;
    this.nativeType = nativeType;
    this.mutable = mutable;
  }

  public String getName() {
    return name;
  }

  public Class<?> getJavaType() {
    return javaType;
  }

  public Class<?> getNativeType() {
    return nativeType;
  }

  public boolean isPrimitive() {
    return !javaType.isArray();
  }

  public boolean isArray() {
    return javaType.isArray();
  }

  public boolean isMutable() {
    return mutable;
  }

  public static DataItemType fromName(String name) {
    for (DataItemType type : DataItemType.values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    return null;
  }

  public static DataItemType fromType(Class<?> type, boolean mutable) {
    for (DataItemType dataType : DataItemType.values()) {
      if (dataType.getJavaType().equals(type) && dataType.isMutable() == mutable) {
        return dataType;
      }
    }
    return null;
  }
}
