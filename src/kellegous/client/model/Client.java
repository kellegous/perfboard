package kellegous.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.data.Array;
import kellegous.client.data.Callback;
import kellegous.client.data.Date;
import kellegous.client.data.IntArray;
import kellegous.client.data.StringArray;

public interface Client {
  // [[rev, message, author, date], count, [...]]
  final static class LoadResponse extends JavaScriptObject {
    private static Date dateFromIntArray(IntArray array) {
      assert array.size() == 6;
      return Date.create(array.get(0), array.get(1), array.get(2), array.get(3), array.get(4), array.get(5), 0);
    }

    protected LoadResponse() {
    }

    public Revision head() {
      assert Array.size(this) == 3;
      final JavaScriptObject data = this.<Array<JavaScriptObject>> cast().get(0);
      final String revision = data.<StringArray> cast().get(0);
      final StringArray message = data.<Array<StringArray>> cast().get(1);
      final String author = data.<StringArray> cast().get(2);
      final IntArray date = data.<Array<IntArray>> cast().get(3);
      return new Revision(revision, author, message, dateFromIntArray(date));
    }

    public int numberOfRevisions() {
      assert Array.size(this) == 3;
      return this.<IntArray> cast().get(1);
    }

    public Array<PerfData> results() {
      assert Array.size(this) == 3;
      return this.<Array<Array<PerfData>>> cast().get(2);
    }
  }

  void load(int n, Callback<LoadResponse> callback);
}
