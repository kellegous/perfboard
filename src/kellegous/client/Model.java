package kellegous.client;

import kellegous.client.data.Array;

public class Model {
  public interface Listener {
    void revisionWindowWillUpdate(double revision, double n);

    void revisionWindowDidUpdate(Array<Revision> revisions);

    void revisionWindowFetchFailed(double revision, double n);
  }

  public static class Revision {
    private final double m_number;
    private final String m_author;
    private final String m_message;

    private Revision(double number, String author, String message) {
      m_number = number;
      m_author = author;
      m_message = message;
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

    public String message() {
      return m_message;
    }
  }

  private final Array<Revision> m_revisions = Array.create();

  private final Array<Listener> m_listeners = Array.create();

  public Model() {
    m_revisions.append(new Revision(7455, "jat", "Fix -bindAddress handling in JUnitShell.  To preserve existing\n" //
        + "behavior without rearchitecting JUnitShell and the RunStyles,\n" //
        + "we disallow -bindAddress in JUnitShell and pretend it was always as if -bindAddress 0.0.0.0 was given."));
  }

  public Revision currentRevision() {
    return m_revisions.isEmpty() ? null : m_revisions.get(m_revisions.size() - 1);
  }

  public void addListener(Listener listener) {
    m_listeners.append(listener);
  }

  private void dispatchRevisionWindowWillUpdate(double revision, double n) {
    for (int i = 0, m = m_listeners.size(); i < m; ++i)
      m_listeners.get(i).revisionWindowWillUpdate(revision, n);
  }

  private void dispatchRevisionWindowDidUpdate(Array<Revision> revisions) {
    for (int i = 0, n = m_listeners.size(); i < n; ++i)
      m_listeners.get(i).revisionWindowDidUpdate(revisions);
  }

  private void dispatchRevisionWindowFetchFailed(double revision, double n) {
    for (int i = 0, m = m_listeners.size(); i < m; ++i)
      m_listeners.get(i).revisionWindowFetchFailed(revision, n);
  }

  public void load(double revision, double n) {
  }
}
