package elemental.json;

import java.io.IOException;
import java.io.Writer;

public interface JsonValue {
  JsonValue NULL = new JsonValue() {

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
      return null;
    }

    @Override
    public void write(Writer writer) throws IOException {
      writer.append("null");
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
      return false;
    }

    @Override
    public boolean isNull() {
      return true;
    }
  };

  JsonNumber asNumber();

  JsonString asString();

  JsonBoolean asBoolean();

  JsonObject asObject();

  JsonArray asArray();

  boolean isNumber();

  boolean isString();

  boolean isBoolean();

  boolean isObject();

  boolean isArray();

  boolean isNull();

  void write(Writer writer) throws IOException;
}
