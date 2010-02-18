package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.Controller.Response;
import kellegous.client.data.Array;
import kellegous.client.data.IntArray;
import kellegous.client.data.StringArray;

public class MockData {
  private static class Revision extends JavaScriptObject {
    @SuppressWarnings("unused")
    protected Revision() {
    }

    final native int revision() /*-{
      return this.revision;
    }-*/;

    final native String author() /*-{
      return this.author;
    }-*/;

    final native JavaScriptObject data() /*-{
      return this.data;
    }-*/;

    final native StringArray message() /*-{
      return this.message;
    }-*/;

    @SuppressWarnings("unused")
    final native IntArray date() /*-{
      return this.date;
    }-*/;
  }

  private static Controller.Response create(Array<Revision> revisions, int offset, int n) {
    assert offset + n < revisions.size();
    final Revision revision = revisions.get(offset);

    // Contruct head [rev, message, author]
    final JavaScriptObject head = JavaScriptObject.createArray();
    head.<IntArray> cast().set(0, revision.revision());
    head.<Array<StringArray>> cast().set(1, revision.message());
    head.<StringArray> cast().set(2, revision.author());

    // Contruct tail []
    final Array<JavaScriptObject> tail = Array.create();
    for (int i = 0; i < n; ++i)
      tail.append(revisions.get(offset + i).data());

    final Array<JavaScriptObject> response = Array.create();
    response.set(0, head);
    response.set(1, tail);
    Debug.log("Mock Response: " + Json.stringify(response));
    return response.cast();
  }

  public static class Client implements Controller.Client {
    private static final String URL = "/mock-data.json";

    private Array<Revision> m_data;

    private void load(final Controller.Callback<JavaScriptObject> callback) {
      Xhr.getJson(URL, new Xhr.Callback<JavaScriptObject>() {

        @Override
        public void didFail() {
          callback.didFail();
        }

        @Override
        public void didCallback(JavaScriptObject value) {
          assert m_data == null;
          m_data = value.cast();
          callback.didCallback(value);
        }
      });
    }

    @Override
    public void loadAllRevisions(final int n, final Controller.Callback<Response> callback) {
      if (m_data != null) {
        callback.didCallback(create(m_data, 0, n));
        return;
      }

      load(new Controller.Callback<JavaScriptObject>() {
        @Override
        public void didCallback(JavaScriptObject value) {
          callback.didCallback(create(m_data, 0, n));
        }

        @Override
        public void didFail() {
          callback.didFail();
        }
      });
    }

    @Override
    public void loadNewRevisions(double sinceRevision, Controller.Callback<Response> callback) {
    }
  }
}
