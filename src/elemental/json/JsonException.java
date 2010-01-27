package elemental.json;

public class JsonException extends Exception {
  private static final long serialVersionUID = 7298125286568970814L;

  public JsonException() {
  }

  public JsonException(String message) {
    super(message);
  }

  public JsonException(String message, Throwable cause) {
    super(message, cause);
  }

  public JsonException(Throwable cause) {
    super(cause);
  }
}
