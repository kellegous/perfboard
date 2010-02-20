package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.data.Array;
import kellegous.client.data.Date;
import kellegous.client.data.IntArray;
import kellegous.client.data.Numbers;
import kellegous.client.data.StringArray;

public class Model {

  public static class Response extends JavaScriptObject {
    protected Response() {
    }

    public final Revision revision() {
      JavaScriptObject data = this.<Array<JavaScriptObject>> cast().get(0);
      double id = data.<IntArray> cast().get(0);
      final StringArray message = data.<Array<StringArray>> cast().get(1);
      final String author = data.<StringArray> cast().get(2);
      return new Revision(id, author, message);
    }

    public final Array<PerfData> results() {
      return this.<Array<Array<PerfData>>> cast().get(1);
    }
  }
  public interface Callback<T> {
    void didCallback(T value);

    void didFail();
  }

  public interface Client {
    void loadAllRevisions(int n, Callback<Response> callback);

    void loadNewRevisions(double sinceRevision, Callback<Response> callback);
  }

  public interface Listener {
    void allRevisionsDidLoad(Model model);

    void allRevisionsDidFailToLoad(Model model);

    void newRevisionsDidLoad(Model model);

    void serverDidStopResponding(Model model);

    void serverDidStartResponding(Model model);
  }

  public static class SizeData extends JavaScriptObject {
    protected SizeData() {
    }

    public final int max() {
      return this.<IntArray> cast().get(0);
    }

    public final int min() {
      return this.<IntArray> cast().get(1);
    }

    private static SizeData as(JavaScriptObject object) {
      assert object != null;
      assert object.<IntArray> cast().size() == 2;
      return object.cast();
    }
  }

  public final static class PerfData extends JavaScriptObject {
    protected PerfData() {
    }

    public native double revision() /*-{
      return this.revision;
    }-*/;

    private native JavaScriptObject dataFor(String name) /*-{
      return this.data[name];
    }-*/;

    public SizeData sizeDataFor(String name) {
      return SizeData.as(dataFor(name));
    }

    private static String toString(PerfData pd) {
      return Json.stringify(pd);
    }
  }

  public static class Revision {
    private final double m_revision;
    private final String m_author;
    private final StringArray m_message;
    private final Date m_date;

    private Revision(double revision, String author, StringArray message) {
      m_revision = revision;
      m_author = author;
      m_message = message;
      m_date = Date.create(); // TODO(knorton): Fix this.
    }

    public double revision() {
      return m_revision;
    }

    public static String format(Revision r) {
      return "r" + Numbers.toInt(r.revision());
    }

    public static String shortenAuthor(String author) {
      // TODO(knorton): What to do about random Googlers who are now committing?
      int index = author.indexOf("@google.com");
      if (index >= 0)
        return author.substring(0, index);

      index = author.indexOf("@fabbott-svn");
      if (index >= 0)
        return author.substring(0, index);

      if (author.indexOf("gwt.team.") == 0)
        return author.substring("gwt.team.".length());
      
      if (author.indexOf("gwt.mirrorbot@") == 0)
        return "MirrorBot";

      return author;
    }

    public String author() {
      return m_author;
    }

    public StringArray message() {
      return m_message;
    }

    public Date date() {
      return m_date;
    }
  }

  private Array<PerfData> m_results;

  private Revision m_currentRevision;

  private final Array<Listener> m_listeners = Array.create();

  private final Client m_client;

  private final int m_size;

  private boolean m_serverIsResponding = true;

  public Model(Client client) {
    this(client, 400);
  }

  public Model(Client client, int n) {
    m_client = client;
    m_size = n;
  }

  private void dispatchAllRevisionsDidLoad() {
    Debug.log("dispatchAllRevisionsDidLoad HEAD=" + Revision.format(currentRevision()));
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).allRevisionsDidLoad(this);
  }

  private void dispatchAllRevisionsDidFailToLoad() {
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).allRevisionsDidFailToLoad(this);
  }

  private void dispatchNewRevisionsDidLoad() {
    Debug.log("dispatchNewRevisionsDidLoad");
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).newRevisionsDidLoad(this);
  }

  private void dispatchServerDidStopResponding() {
    if (!m_serverIsResponding)
      return;
    m_serverIsResponding = false;
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).serverDidStopResponding(this);
  }

  private void dispatchServerDidStartResponding() {
    if (m_serverIsResponding)
      return;
    m_serverIsResponding = true;
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).serverDidStartResponding(this);
  }

  public Array<PerfData> results() {
    return m_results;
  }

  public Revision currentRevision() {
    return m_currentRevision;
  }

  public void addListener(Listener listener) {
    m_listeners.append(listener);
  }

  public void loadAll() {
    m_client.loadAllRevisions(m_size, new Callback<Response>() {
      @Override
      public void didCallback(Response response) {
        m_currentRevision = response.revision();
        m_results = response.results();

        // Debug
        if (Debug.enabled()) {
          for (int i = 0, n = m_results.size(); i < n; ++i)
            Debug.log(PerfData.toString(m_results.get(i)));
        }

        dispatchAllRevisionsDidLoad();
      }

      @Override
      public void didFail() {
        dispatchAllRevisionsDidFailToLoad();
      }
    });
  }

  public void loadNew() {
    assert m_results != null;
    m_client.loadNewRevisions(m_currentRevision.revision(), new Callback<Response>() {
      @Override
      public void didCallback(Response value) {
        dispatchServerDidStartResponding();
        // TODO(knorton): Merge the new results with the existing.
        dispatchNewRevisionsDidLoad();
      }

      @Override
      public void didFail() {
        dispatchServerDidStopResponding();
      }
    });
  }
}
