package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.Model.Response;
import kellegous.client.data.Array;
import kellegous.client.data.IntArray;
import kellegous.client.data.NumberArray;
import kellegous.client.data.StringArray;

public class MockData {
  private static class Revision extends JavaScriptObject {
    @SuppressWarnings("unused")
    protected Revision() {
    }

    final native double revision() /*-{
      return this.revision;
    }-*/;

    final native String author() /*-{
      return this.author;
    }-*/;

    final native StringArray message() /*-{
      return this.message;
    }-*/;

    @SuppressWarnings("unused")
    final native IntArray date() /*-{
      return this.date;
    }-*/;
  }

  private static Model.Response create(Array<Revision> revisions, int offset, int n) {
    assert offset + n < revisions.size();
    final Revision revision = revisions.get(offset);

    // TODO(knorton): Add date.
    // Contruct head [rev, message, author]
    final JavaScriptObject head = JavaScriptObject.createArray();
    head.<NumberArray> cast().set(0, revision.revision());
    head.<Array<StringArray>> cast().set(1, revision.message());
    head.<StringArray> cast().set(2, revision.author());

    // Contruct tail []
    final Array<JavaScriptObject> tail = Array.create();
    for (int i = 0; i < n; ++i)
      tail.append(revisions.get(offset + i));

    // Debug
    if (Debug.enabled()) {
      for (int i = 0; i < n; ++i)
        Debug.log("revision loaded: " + revisions.get(offset + i).revision());
    }

    final Array<JavaScriptObject> response = Array.create();
    response.set(0, head);
    response.set(1, tail);
    return response.cast();
  }

  public static class Client implements Model.Client {
    private static final String URL = "/mock-data.json";

    private Array<Revision> m_data;

    private int m_offset = 0;

    private void load(final Model.Callback<JavaScriptObject> callback) {
      Debug.log("Loading data...");
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
    public void loadAllRevisions(final int n, final Model.Callback<Response> callback) {
      m_offset++;
      if (m_data != null) {
        callback.didCallback(create(m_data, m_offset, n));
        return;
      }

      load(new Model.Callback<JavaScriptObject>() {
        @Override
        public void didCallback(JavaScriptObject value) {
          callback.didCallback(create(m_data, m_offset, n));
        }

        @Override
        public void didFail() {
          callback.didFail();
        }
      });
    }

    @Override
    public void loadNewRevisions(double sinceRevision, Model.Callback<Response> callback) {
    }
  }
}
