package elemental.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JsonArray implements JsonValue {
  private final List<JsonValue> values = new ArrayList<JsonValue>();

  public JsonArray() {
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
    return null;
  }

  public int getLength() {
    return values.size();
  }

  public JsonValue get(int index) {
    final JsonValue value = values.get(index);
    return (value == null) ? JsonValue.NULL : value;
  }

  public void add(JsonValue value) {
    values.add(value);
  }

  public void add(boolean value) {
    add(JsonBoolean.create(value));
  }

  public void add(long value) {
    add(JsonNumber.create(value));
  }

  public void add(double value) {
    add(JsonNumber.create(value));
  }

  public void add(String value) {
    add(JsonString.create(value));
  }

  public static JsonArray create() {
    return new JsonArray();
  }

  @Override
  public JsonArray asArray() {
    return this;
  }

  public static JsonArray parse(Reader reader) throws IOException,
      JsonException {
    final JsonArray arr = new Tokenizer(reader).nextValue().asArray();
    if (arr == null) {
      throw new JsonException("Object is not a JSON array.");
    }
    return arr;
  }

  static JsonArray parse(Tokenizer tokenizer) throws IOException, JsonException {
    final JsonArray array = new JsonArray();
    int c = tokenizer.nextNonWhitespace();
    assert c == '[';
    while (true) {
      c = tokenizer.nextNonWhitespace();
      switch (c) {
        case ']':
          return array;
        default:
          tokenizer.back(c);
          array.add(tokenizer.nextValue());
          final int d = tokenizer.nextNonWhitespace();
          switch (d) {
            case ']':
              return array;
            case ',':
              break;
            default:
              throw new JsonException("Invalid array: expected , or ]");
          }
      }
    }
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write('[');
    for (int i = 0, n = values.size(); i < n; ++i) {
      if (i != 0) {
        writer.write(',');
      }
      values.get(i).write(writer);
    }
    writer.write(']');
  }

  @Override
  public boolean isArray() {
    return true;
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
    return false;
  }

  @Override
  public boolean isNull() {
    return false;
  }
}
