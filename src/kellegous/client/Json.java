package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Json {

  public native static <T extends JavaScriptObject> T parse(String data) /*-{
    return JSON.parse(data);
  }-*/;

  public native static String stringify(JavaScriptObject data) /*-{
    return JSON.stringify(data);
  }-*/;

  private Json() {

  }
}
