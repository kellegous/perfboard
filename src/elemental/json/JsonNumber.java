package elemental.json;

import java.io.IOException;
import java.io.Writer;

public abstract class JsonNumber implements JsonValue {
  private static class JsonInteger extends JsonNumber {
    private final long value;

    public JsonInteger(long value) {
      this.value = value;
    }

    @Override
    public long getInteger() {
      return value;
    }

    @Override
    public double getDecimal() {
      return value;
    }

    @Override
    public void write(Writer writer) throws IOException {
      writer.write(Long.toString(value));
    }

    @Override
    public boolean isNull() {
      return false;
    }
  }

  private static class JsonDecimal extends JsonNumber {
    private final double value;

    public JsonDecimal(double value) {
      this.value = value;
    }

    @Override
    public double getDecimal() {
      return value;
    }

    @Override
    public long getInteger() {
      return (long) value;
    }

    @Override
    public void write(Writer writer) throws IOException {
      writer.write(Double.toString(value));
    }

    @Override
    public boolean isNull() {
      return false;
    }
  }

  private JsonNumber() {
  }

  public abstract long getInteger();

  public abstract double getDecimal();

  public static JsonNumber create(long value) {
    return new JsonInteger(value);
  }

  public static JsonNumber create(int value) {
    return new JsonInteger(value);
  }

  public static JsonNumber create(double value) {
    return new JsonDecimal(value);
  }

  @Override
  public JsonArray asArray() {
    return null;
  }

  @Override
  public JsonBoolean asBoolean() {
    return null;
  }

  @Override
  public JsonNumber asNumber() {
    return this;
  }

  @Override
  public JsonObject asObject() {
    return null;
  }

  @Override
  public JsonString asString() {
    return null;
  }

  @Override
  public boolean isArray() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isBoolean() {
    return false;
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean isObject() {
    return false;
  }

  @Override
  public boolean isString() {
    return false;
  }
}
