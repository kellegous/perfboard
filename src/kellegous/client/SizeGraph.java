package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import kellegous.client.data.Array;
import kellegous.client.data.Numbers;
import kellegous.client.model.Model;
import kellegous.client.model.PerfData;

// TODO(knorton): Add increase/decrease indicators.
// TODO(knorton): Draw max/min bars.
public class SizeGraph implements Model.Listener {

  private final CanvasElement.Context m_context;
  private final String m_tag;
  private final int m_width, m_height;
  private final SpanElement m_change;
  private final Css m_css;

  public interface Css extends CssResource {
    int width();

    int height();

    String canvas();

    String graph();

    String info();

    String label();

    String change();

    String changeMeh();

    String changeYay();

    String changeBoo();
  }

  public interface Resources extends ClientBundle {
    @Source("resources/size-graph.css")
    Css sizeGraphCss();
  }

  public SizeGraph(Css css, Element parent, Model model, String tag, String labelText) {
    final Document document = parent.getOwnerDocument();
    final DivElement graph = document.createDivElement();
    final CanvasElement canvas = CanvasElement.create(document, css.width(), css.height());
    final DivElement info = document.createDivElement();
    final SpanElement label = document.createSpanElement();
    final SpanElement change = document.createSpanElement();

    graph.setClassName(css.graph());
    canvas.setClassName(css.canvas());
    info.setClassName(css.info());
    change.setClassName(css.change());
    label.setClassName(css.label());

    label.setInnerText(labelText);

    graph.appendChild(info);
    graph.appendChild(canvas);
    parent.appendChild(graph);
    info.appendChild(label);
    info.appendChild(change);

    m_context = canvas.context();
    m_tag = tag;
    // TODO(knorton): These fields can be removed.
    m_width = css.width();
    m_height = css.height();
    m_change = change;
    m_css = css;

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

    final double bw = 0.75 * dx;
    final CanvasElement.Context context = m_context;
    m_context.clearRect(0, 0, m_width, m_height);
    context.setFillStyle("#39f");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final double y = dy * data.get(i).max();
      context.fillRect(dx * i, m_height - y, bw, y);
    }

    context.setFillStyle("rgba(0, 0, 0, 0.4)");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final Model.SizeData d = data.get(i);
      final double y = dy * d.max();
      context.fillRect(dx * i, m_height - y, bw, y - dy * d.min());
    }
  }

  private static Array<Model.SizeData> toData(Array<PerfData> results, String tag) {
    // TODO(knorton): Supply an initial size.
    final Array<Model.SizeData> data = Array.create();
    for (int i = 0, n = results.size(); i < n; ++i)
      data.append(results.get(i).sizeDataFor(tag));
    return data;
  }

  private static double computePercentChange(Array<Model.SizeData> results) {
    final int n = results.size();
    final Model.SizeData current = results.get(n - 1);
    final Model.SizeData last = results.get(n - 2);
    final double maxChange = (double)(current.max() - last.max()) / (double)last.max();
    final double minChange = (double)(current.min() - last.min()) / (double)last.min();

    // Return the worst result.
    return Math.max(maxChange, minChange);
  }

  private void update(Model model) {
    final Array<Model.SizeData> data = toData(model.results(), m_tag);

    // Render graphs.
    render(data);

    if (data.size() < 2)
      // TODO(knorton): Hide the pct change indicator.
      return;

    double change = computePercentChange(data);
    double absChange = Math.abs(change);
    if (absChange < 0.01) {
      m_change.setInnerText(" " + Numbers.toFixed(absChange * 100, 1) + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeMeh());
    } else if (change < 0) {
      m_change.setInnerText("\u2193" + Numbers.toFixed(absChange * 100, 1) + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeYay());
    } else {
      m_change.setInnerText("\u2191" + Numbers.toFixed(absChange * 100, 1) + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeBoo());
    }
  }

  @Override
  public void serverDidStartResponding(Model model) {
  }

  @Override
  public void serverDidStopResponding(Model model) {
  }

  @Override
  public void didLoad(Model model) {
    update(model);
  }
}
