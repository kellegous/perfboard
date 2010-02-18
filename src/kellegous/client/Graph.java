package kellegous.client;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Random;

import kellegous.client.data.NumberArray;

public class Graph {
  private static final int HEIGHT = 40;
  private static final int WIDTH = 800;
  private static final int RENDER_MODE = 1;

  private final CanvasElement.Context m_context;

  public Graph(Element parent) {
    final CanvasElement canvas = CanvasElement.create(parent.getOwnerDocument(), WIDTH, HEIGHT);

    parent.appendChild(canvas);

    m_context = canvas.context();
  }

  private static NumberArray randomData(int n, double base) {
    final NumberArray res = NumberArray.create();
    for (int i = 0; i < n; ++i) {
      final double r = Random.nextDouble();
      if (r > 0.8)
        base = base + (Random.nextDouble() * 2.0 - 1.0) * 0.1 * base;
      res.set(i, base);
    }
    return res;
  }

  private static double min(NumberArray data) {
    double min = Double.MAX_VALUE;
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double v = data.get(i);
      if (v < min)
        min = v;
    }
    return min;
  }

  private static double max(NumberArray data) {
    double max = Double.MIN_VALUE;
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double v = data.get(i);
      if (v > max)
        max = v;
    }
    return max;
  }

  public void render() {
    final Duration d = new Duration();
    switch (RENDER_MODE) {
      case 0:
        render0();
        break;
      case 1:
        render1();
        break;
    }
    Debug.log("Graph::render in " + d.elapsedMillis() + "ms.");
  }

  private void render1() {
    final NumberArray data = randomData(400, 24000);
    final double max = max(data);
    final double dx = (float)WIDTH / (float)data.size();
    final double dy = (float)HEIGHT / (float)max;
    final CanvasElement.Context context = m_context;
    context.setFillStyle("#39f");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double y = dy * data.get(i);
      context.fillRect(dx * i, HEIGHT - y, dx / 2.0, y);
    }
  }

  private void render0() {
    final NumberArray data = randomData(400, 24000);
    final double max = max(data);
    // final double min = min(data);
    final double dx = (float)WIDTH / (float)data.size();
    final double dy = (float)HEIGHT / (float)max;
    final double y0 = HEIGHT - dy * data.get(0);

    final CanvasElement.Context context = m_context;
    context.beginPath();
    context.moveTo(0, y0);
    for (int i = 1, n = data.size(); i < n; ++i) {
      final double y = dy * data.get(i);
      context.lineTo(dx * i, HEIGHT - y);
    }
    context.moveTo(0, y0);
    context.setStrokeStyle("#f00");
    context.stroke();
  }
}
