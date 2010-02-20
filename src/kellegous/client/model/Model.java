package kellegous.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.Debug;
import kellegous.client.data.Array;
import kellegous.client.data.Callback;
import kellegous.client.data.IntArray;
import kellegous.client.model.Client.LoadResponse;

public class Model {
  public interface Listener {
    void didLoad(Model model);

    void serverDidStopResponding(Model model);

    void serverDidStartResponding(Model model);
  }

  public static class SizeData extends JavaScriptObject {
    protected SizeData() {
    }

    public final int max() {
      return this.<IntArray> cast().get(1);
    }

    public final int min() {
      return this.<IntArray> cast().get(0);
    }

    static SizeData as(JavaScriptObject object) {
      assert object != null;
      assert object.<IntArray> cast().size() == 2;
      return object.cast();
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

  private void dispatchDidLoad() {
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).didLoad(this);
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

  public void load() {
    m_client.load(m_size, new Callback<LoadResponse>() {
      @Override
      public void didFail() {
        dispatchServerDidStopResponding();
      }

      @Override
      public void didCallback(LoadResponse value) {
        // TODO(knorton): Rename to selectedRevision;
        m_currentRevision = value.head();
        m_results = value.results();
        if (Debug.enabled())
          Debug.log("Loaded revisions " + m_results.get(0).revision() + " - " + m_results.get(m_results.size() - 1).revision());
        dispatchServerDidStartResponding();
        dispatchDidLoad();
      }
    });
  }
}
