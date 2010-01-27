package elemental.json;

import elemental.base.Pair;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonObject implements JsonValue, Iterable<Pair<String, JsonValue>> {
  private static class Iter implements Iterator<Pair<String, JsonValue>> {
    private final Iterator<Map.Entry<String, JsonValue>> iter;

    Iter(Iterator<Map.Entry<String, JsonValue>> iter) {
      this.iter = iter;
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public Pair<String, JsonValue> next() {
      final Map.Entry<String, JsonValue> entry = iter.next();
      return new Pair<String, JsonValue>(entry.getKey(), entry.getValue());
    }

    @Override
    public void remove() {
      iter.remove();
    }

  }

  public static JsonObject create() {
    return new JsonObject();
  }

  public static JsonObject parse(Reader reader) throws JsonException,
      IOException {
    return JsonObject.parse(new Tokenizer(reader));
  }

  static JsonObject parse(Tokenizer tokenizer) throws IOException,
      JsonException {
    final JsonObject object = new JsonObject();
    int c = tokenizer.next();
    if (c != '{') {
      throw new JsonException("Payload does not begin with {");
    }

    while (true) {
      c = tokenizer.nextNonWhitespace();
      switch (c) {
        case '}':
          // We're done.
          return object;
        case '"':
          tokenizer.back(c);
          // Ready to start a key.
          final String key = tokenizer.nextString();
          if (tokenizer.nextNonWhitespace() != ':') {
            throw new JsonException("Invalid object: expecting \":\"");
          }
          // TODO(knorton): Make sure this key is not already set.
          object.put(key, tokenizer.nextValue());
          switch (tokenizer.nextNonWhitespace()) {
            case ',':
              break;
            case '}':
              return object;
            default:
              throw new JsonException("Invalid object: expecting } or ,");
          }
          break;
        case ',':
          break;
        default:
          throw new JsonException("Invalid object: ");
      }
    }
  }

  private final Map<String, JsonValue> properties = new HashMap<String, JsonValue>();

  public JsonObject() {
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
    return this;
  }

  @Override
  public JsonString asString() {
    return null;
  }

  public JsonValue get(String key) {
    final JsonValue value = properties.get(key);
    return (value == null) ? JsonValue.NULL : value;
  }

  public boolean isEmpty() {
    return properties.isEmpty();
  }

  @Override
  public Iterator<Pair<String, JsonValue>> iterator() {
    return new Iter(properties.entrySet().iterator());
  }

  public void put(String key, boolean val) {
    put(key, JsonBoolean.create(val));
  }

  public void put(String key, double val) {
    put(key, JsonNumber.create(val));
  }

  public void put(String key, JsonValue val) {
    properties.put(key, val);
  }

  public void put(String key, long val) {
    put(key, JsonNumber.create(val));
  }

  public void put(String key, String val) {
    put(key, JsonString.create(val));
  }

  @Override
  public void write(Writer writer) throws IOException {
    boolean first = true;
    writer.write('{');
    for (Map.Entry<String, JsonValue> e : properties.entrySet()) {
      if (!first) {
        writer.append(',');
      } else {
        first = false;
      }
      JsonString.write(e.getKey(), writer);
      writer.append(':');
      e.getValue().write(writer);
    }
    writer.write('}');
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
    return true;
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
