package kellegous.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Random;

import kellegous.client.data.NumberArray;

public class Graph {
  private final CanvasElement.Context m_context;

  public Graph(Element parent) {
    final CanvasElement canvas = CanvasElement.create(parent.getOwnerDocument(), 800, 600);

    parent.appendChild(canvas);

    m_context = canvas.context();
  }

  private NumberArray randomData(int n, double base) {
    final NumberArray res = NumberArray.create();
    for (int i = 0; i < n; ++i) {
      final double r = Random.nextDouble();
      if (r > 0.8)
        base = base + (Random.nextDouble() * 2.0 - 1.0) * 0.1 * base;
      res.set(i, base);
    }
    return res;
  }

  public void render() {
    final NumberArray data = randomData(400, 24000);
    final CanvasElement.Context context = m_context;
  }
}
