package kellegous.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class Xhr {
  public interface Callback<T> {
    void didCallback(T value);

    void didFail();
  }

  public static void getJson(String url, final Callback<JavaScriptObject> callback) {
    get(url, new Callback<String>() {
      @Override
      public void didCallback(String value) {
        try {
        callback.didCallback(Json.parse(value));
        } catch (JavaScriptException e) {
          didFail();
        }
      }

      @Override
      public void didFail() {
        callback.didFail();
      }
    });
  }

  public static void get(String url, final Callback<String> callback) {
    final XMLHttpRequest xhr = XMLHttpRequest.create();
    xhr.open("GET", url);
    xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
      @Override
      public void onReadyStateChange(XMLHttpRequest xhr) {
        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
          if (xhr.getStatus() == 200)
            callback.didCallback(xhr.getResponseText());
          else
            callback.didFail();
        }
      }
    });
    xhr.send();
  }

  private Xhr() {
  }
}
