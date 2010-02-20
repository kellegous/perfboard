package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import kellegous.client.data.Array;
import kellegous.client.data.NumberArray;
import kellegous.client.model.Model;
import kellegous.client.model.PerfData;

// TODO(knorton): Add increase/decrease indicators.
// TODO(knorton): Draw max/min bars.
public class SizeGraph implements Model.Listener {

  private final CanvasElement.Context m_context;
  private final String m_tag;
  private final int m_width, m_height;

  public interface Css extends CssResource {
    int width();

    int height();

    String canvas();

    String graph();

    String info();
  }

  public interface Resources extends ClientBundle {
    @Source("resources/size-graph.css")
    Css sizeGraphCss();
  }

  public SizeGraph(Css css, Element parent, Model model, String tag) {
    final Document document = parent.getOwnerDocument();
    final DivElement graph = document.createDivElement();
    final CanvasElement canvas = CanvasElement.create(document, css.width(), css.height());
    final DivElement info = document.createDivElement();

    graph.setClassName(css.graph());
    canvas.setClassName(css.canvas());
    info.setClassName(css.info());

    graph.appendChild(info);
    graph.appendChild(canvas);
    parent.appendChild(graph);

    // TODO(knorton): Probably want a dedicated label.
    info.setInnerText(tag);

    m_context = canvas.context();
    m_tag = tag;
    m_width = css.width();
    m_height = css.height();

    model.addListener(this);
  }

  private static double max(Array<Model.SizeData> data) {
    double max = Double.MIN_VALUE;
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double v = data.get(i).max();
      if (v > max)
        max = v;
    }
    return max;
  }

  private void render(Array<Model.SizeData> data) {
    final double max = max(data);
    final double dx = (float)m_width / (float)data.size();
    final double dy = (float)m_height / (float)max;
    final CanvasElement.Context context = m_context;
    m_context.clearRect(0, 0, m_width, m_height);
    context.setFillStyle("#39f");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double y = dy * data.get(i).max();
      context.fillRect(dx * i, m_height - y, dx / 2.0, y);
    }

    context.setFillStyle("rgba(0, 0, 0, 0.4)");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final Model.SizeData d = data.get(i);
      final double y = dy * d.max();
      context.fillRect(dx * i, m_height - y, dx / 2.0, y - dy * d.min());
    }
  }

  private static Array<Model.SizeData> toData2(Array<PerfData> results, String tag) {
    // TODO(knorton): Supply an initial size.
    final Array<Model.SizeData> data = Array.create();
    for (int i = 0, n = results.size(); i < n; ++i)
      data.append(results.get(i).sizeDataFor(tag));
    return data;
  }

  private static NumberArray toData(Array<PerfData> results, String tag) {
    // TODO(knorton): Supply a size.
    final NumberArray data = NumberArray.create();
    for (int i = 0, n = results.size(); i < n; ++i)
      data.set(i, results.get(i).sizeDataFor(tag).max());
    return data;
  }

  @Override
  public void serverDidStartResponding(Model model) {
  }

  @Override
  public void serverDidStopResponding(Model model) {
  }

  @Override
  public void didLoad(Model model) {
    render(toData2(model.results(), m_tag));
  }
}
