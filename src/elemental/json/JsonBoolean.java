package elemental.json;

import java.io.IOException;
import java.io.Writer;

public class JsonBoolean implements JsonValue {
  public static final JsonBoolean TRUE = new JsonBoolean(true);

  public static final JsonBoolean FALSE = new JsonBoolean(false);

  private final boolean value;

  private JsonBoolean(boolean value) {
    this.value = value;
  }

  public boolean getBoolean() {
    return value;
  }

  public static JsonBoolean create(boolean value) {
    return value ? TRUE : FALSE;
  }

  @Override
  public JsonArray asArray() {
    return null;
  }

  @Override
  public JsonBoolean asBoolean() {
    return this;
  }

  @Override
  public JsonNumber asNumber() {
    return null;
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
  public void write(Writer writer) throws IOException {
    writer.write(Boolean.toString(value));
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean isNumber() {
    return false;
  }

  @Override
  public boolean isObject() {
    return false;
  }

  @Override
  public boolean isString() {
    return false;
  }

  @Override
  public boolean isNull() {
    return false;
  }
}
