package elemental.json;

import java.io.IOException;
import java.io.Writer;

public class JsonString implements JsonValue {
  private final String value;

  private JsonString(String value) {
    this.value = value;
  }

  public String getString() {
    return value;
  }

  public static JsonString create(String value) {
    return new JsonString(value);
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
    return null;
  }

  @Override
  public JsonObject asObject() {
    return null;
  }

  @Override
  public JsonString asString() {
    return this;
  }

  static void write(String data, Writer writer) throws IOException {
    if (data == null) {
      writer.append("null");
      return;
    }

    writer.append('"');
    for (int i = 0, n = data.length(); i < n; ++i) {
      final char c = data.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          writer.append('\\').append(c);
          break;
        case '\b':
          writer.append("\\b");
          break;
        case '\t':
          writer.append("\\t");
          break;
        case '\n':
          writer.append("\\n");
          break;
        case '\f':
          writer.append("\\f");
          break;
        case '\r':
          writer.append("\\r");
          break;
        default:
          // TODO(knorton): The json.org code encodes ranges of characters in
          // the form u####. Given that JSON is supposed to be UTF-8, I don't
          // understand why you would want to do that.
          writer.append(c);
      }
    }
    writer.append('"');
  }

  @Override
  public void write(Writer writer) throws IOException {
    write(value, writer);
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return false;
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
    return true;
  }

  @Override
  public boolean isNull() {
    return false;
  }
}
