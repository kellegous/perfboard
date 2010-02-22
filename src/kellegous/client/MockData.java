package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.data.Array;
import kellegous.client.data.Callback;
import kellegous.client.data.IntArray;
import kellegous.client.data.StringArray;
import kellegous.client.model.Client;

public class MockData {
  private final static class Revision extends JavaScriptObject {
    @SuppressWarnings("unused")
    protected Revision() {
    }

    native String revision() /*-{
      return this.revision;
    }-*/;

    native String author() /*-{
      return this.author;
    }-*/;

    native StringArray message() /*-{
      return this.message;
    }-*/;

    native IntArray date() /*-{
      return this.date;
    }-*/;

    JavaScriptObject toArray() {
      final JavaScriptObject struct = JavaScriptObject.createArray();
      struct.<StringArray> cast().set(0, revision());
      struct.<Array<StringArray>> cast().set(1, message());
      struct.<StringArray> cast().set(2, author());
      struct.<Array<IntArray>> cast().set(3, date());
      return struct;
    }
  }

  private static Client.LoadResponse create(Array<Revision> revisions, int offset, int n) {
    assert offset + n < revisions.size();
    final int size = revisions.size();

    final Array<Revision> selected = revisions.slice(size - offset - n, size - offset);
    final Revision revision = selected.get(selected.size() - 1);

    final Array<JavaScriptObject> response = Array.create();
    response.set(0, revision.toArray());
    response.<IntArray> cast().set(1, size);
    response.set(2, selected);
    return response.cast();
  }

  public static Client createClient() {
    return new ClientImpl();
  }

  private static class ClientImpl implements Client {
    private static final String URL = "/mock-data.json";

    private Array<Revision> m_data;

    @Override
    public void load(final int n, final Callback<LoadResponse> callback) {
      final int offset = 35;
      if (m_data == null) {
        Xhr.getJson(URL, new Xhr.Callback<JavaScriptObject>() {

          @Override
          public void didFail() {
            callback.didFail();
          }

          @Override
          public void didCallback(JavaScriptObject value) {
            m_data = value.cast();
            callback.didCallback(create(m_data, offset, n));
          }
        });
      } else {
        callback.didCallback(create(m_data, offset, n));
      }
    }
  }
}
