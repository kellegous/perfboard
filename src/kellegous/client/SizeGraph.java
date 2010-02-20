package kellegous.client;

import com.google.gwt.dom.client.Element;

import kellegous.client.Model.PerfData;
import kellegous.client.data.Array;
import kellegous.client.data.NumberArray;

public class SizeGraph implements Model.Listener {
  private static final int HEIGHT = 40;
  private static final int WIDTH = 800;
  private static final int RENDER_MODE = 1;

  private final CanvasElement.Context m_context;
  private final String m_tag;

  public SizeGraph(Element parent, Model model, String tag) {
    final CanvasElement canvas = CanvasElement.create(parent.getOwnerDocument(), WIDTH, HEIGHT);
    parent.appendChild(canvas);
    m_context = canvas.context();
    m_tag = tag;
    model.addListener(this);
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

  private void render1(NumberArray data) {
    final double max = max(data);
    final double dx = (float)WIDTH / (float)data.size();
    final double dy = (float)HEIGHT / (float)max;
    final CanvasElement.Context context = m_context;
    m_context.clearRect(0, 0, WIDTH, HEIGHT);
    context.setFillStyle("#39f");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double y = dy * data.get(i);
      context.fillRect(dx * i, HEIGHT - y, dx / 2.0, y);
    }
  }

  private void render0(NumberArray data) {
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

  @Override
  public void allRevisionsDidFailToLoad(Model model) {
  }

  @Override
  public void allRevisionsDidLoad(Model model) {
    render(model.results());
  }

  private static NumberArray toData(Array<PerfData> results, String tag) {
    // TODO(knorton): Supply a size.
    final NumberArray data = NumberArray.create();
    for (int i = 0, n = results.size(); i < n; ++i)
      data.set(i, results.get(i).sizeDataFor(tag).max());
    return data;
  }

  private void render(Array<PerfData> results) {
    switch (RENDER_MODE) {
      case 0:
        render0(toData(results, m_tag));
        break;
      case 1:
        render1(toData(results, m_tag));
        break;
    }
  }

  @Override
  public void newRevisionsDidLoad(Model model) {
  }

  @Override
  public void serverDidStartResponding(Model model) {
  }

  @Override
  public void serverDidStopResponding(Model model) {
  }
}
