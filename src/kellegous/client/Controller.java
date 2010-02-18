package kellegous.client;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.data.Array;
import kellegous.client.data.IntArray;
import kellegous.client.data.StringArray;

public class Controller {

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

    public final Array<RevisionPerf> results() {
      return this.<Array<Array<RevisionPerf>>> cast().get(1);
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
    void allRevisionsDidLoad(Controller controller);

    void allRevisionsDidFailToLoad(Controller controller);

    void newRevisionsDidLoad(Controller controller);

    void serverDidStopResponding(Controller controller);

    void serverDidStartResponding(Controller controller);
  }

  public static class RevisionPerf extends JavaScriptObject {
    protected RevisionPerf() {
    }

    public final native JavaScriptObject data() /*-{
      return this.data;
    }-*/;

    public final native double id() /*-{
      return this.id;
    }-*/;
  }

  public static class Revision {
    private final double m_number;
    private final String m_author;
    private final StringArray m_message;

    private Revision(double number, String author, StringArray message) {
      m_number = number;
      m_author = author;
      m_message = message;
    }

    public double id() {
      return m_number;
    }

    private native static String number(double n) /*-{
      return "r" + n;
    }-*/;

    public String number() {
      return number(m_number);
    }

    public String author() {
      return m_author;
    }

    public StringArray message() {
      return m_message;
    }
  }

  private Array<RevisionPerf> m_results;

  private Revision m_currentRevision;

  private final Array<Listener> m_listeners = Array.create();

  private final Client m_client;

  private final int m_size;

  private boolean m_serverIsResponding = true;

  public Controller(Client client) {
    this(client, 400);
  }

  public Controller(Client client, int n) {
    m_client = client;
    m_size = n;
  }

  private void dispatchAllRevisionsDidLoad() {
    Debug.log("dispatchAllRevisionsDidLoad HEAD=" + currentRevision().number());
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

  public Array<RevisionPerf> results() {
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
    m_client.loadNewRevisions(m_currentRevision.id(), new Callback<Response>() {
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
