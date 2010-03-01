package kellegous.client;

import kellegous.client.data.Array;

public class SharedGraphEvents {
  public interface Delegate {
    void shouldHoverOn(int index);

    void shouldRemoveHover();
  }

  public static class Controller {
    private final Array<Delegate> m_delegates = Array.create();

    public void addDelegate(Delegate delegate) {
      m_delegates.append(delegate);
    }

    public void doHoverOn(int index) {
      for (int i = 0, n = m_delegates.size(); i < n; ++i)
        m_delegates.get(i).shouldHoverOn(index);
    }

    public void doRemoveHover() {
      for (int i = 0, n = m_delegates.size(); i < n; ++i)
        m_delegates.get(i).shouldRemoveHover();
    }
  }
}
