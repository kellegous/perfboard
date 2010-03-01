package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.topspin.ui.client.MouseMoveEvent;
import com.google.gwt.topspin.ui.client.MouseMoveListener;
import com.google.gwt.topspin.ui.client.MouseOutEvent;
import com.google.gwt.topspin.ui.client.MouseOutListener;

import kellegous.client.data.Array;
import kellegous.client.data.Numbers;
import kellegous.client.model.Model;
import kellegous.client.model.PerfData;
import kellegous.client.model.Model.SizeData;

public class SizeGraphView {

  private class Controller implements Model.Listener, MouseMoveListener,
      MouseOutListener, SharedGraphEvents.Delegate {
    void attach(Model model,
        SharedGraphEvents.Controller selectionController) {
      model.addListener(this);
      MouseMoveEvent.addMouseMoveListener(SizeGraphView.this, m_canvas, this);
      MouseOutEvent.addMouseOutListener(SizeGraphView.this, m_canvas, this);
      selectionController.addDelegate(this);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
      final int x = event.getNativeEvent().getClientX()
          - m_canvas.getOffsetLeft();
      int index = (int) (x / m_barWidth);
      m_selectionController.doHoverOn(index);
    }

    @Override
    public void didLoad(Model model) {
      update(model);
    }

    @Override
    public void serverDidStartResponding(Model model) {
    }

    @Override
    public void serverDidStopResponding(Model model) {
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
      m_selectionController.doRemoveHover();
    }

    @Override
    public void shouldHoverOn(int index) {
      hoverOn(index);
    }

    @Override
    public void shouldRemoveHover() {
      hoverOn(-1);
    }
  }

  private final CanvasElement m_canvas;
  private final CanvasElement.Context m_context;
  private final String m_tag;
  private final SpanElement m_change;
  private final Css m_css;
  private final SharedGraphEvents.Controller m_selectionController;

  private final double m_barWidth;

  private Array<SizeData> m_data;
  private int m_hoveredIndex = -1;

  // TODO(knorton): Need fields for selected & hovered bars.

  public interface Css extends CssResource {
    int padding();

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

  public SizeGraphView(Css css, Element parent, Model model,
      SharedGraphEvents.Controller selectionController, String tag,
      String labelText) {
    final Document document = parent.getOwnerDocument();
    final DivElement graph = document.createDivElement();
    final CanvasElement canvas = CanvasElement.create(document, css.width(),
        css.height());
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

    m_canvas = canvas;
    m_context = canvas.context();
    m_selectionController = selectionController;
    m_tag = tag;
    m_change = change;
    m_css = css;
    m_barWidth = (double) css.width() / (double) model.selectionSize();

    new Controller().attach(model, selectionController);
  }

  private void hoverOn(int index) {
    m_hoveredIndex = index;
    render();
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

  private void render() {
    final Array<SizeData> data = m_data;
    if (m_data == null)
      return;

    final int width = m_css.width();
    final int height = m_css.height();
    final int padding = m_css.padding();

    final double max = max(data);
    final double dx = m_barWidth;
    final double dy = (double) (height - padding) / (double) max;

    final double bw = 0.75 * dx;
    final CanvasElement.Context context = m_context;
    m_context.clearRect(0, 0, width, height);

    // main bar.
    context.setFillStyle("#39f");
    for (int i = 0, n = data.size(); i < n; ++i) {
      if (i == m_hoveredIndex) {
        context.save();
        context.setFillStyle("#999");
        context.fillRect(dx * i, 0, bw, height);
        context.restore();
      }
      final double y = dy * data.get(i).max();
      context.fillRect(dx * i, height - y, bw, y);
    }

    // min/max bar.
    context.setFillStyle("rgba(0, 0, 0, 0.4)");
    for (int i = 0, n = data.size(); i < n; ++i) {
      final Model.SizeData d = data.get(i);
      final double y = dy * d.max();
      context.fillRect(dx * i, height - y, bw, y - dy * d.min());
    }
  }

  private static Array<Model.SizeData> toData(Array<PerfData> results,
      String tag) {
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
    final double maxChange = (double) (current.max() - last.max())
        / (double) last.max();
    final double minChange = (double) (current.min() - last.min())
        / (double) last.min();

    // Return the worst result.
    return Math.max(maxChange, minChange);
  }

  private void update(Model model) {
    m_data = toData(model.results(), m_tag);

    // Render graphs.
    render();

    if (m_data.size() < 2)
      // TODO(knorton): Hide the pct change indicator.
      return;

    double change = computePercentChange(m_data);
    double absChange = Math.abs(change);
    if (absChange < 0.01) {
      m_change.setInnerText(" " + Numbers.toFixed(absChange * 100, 1) + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeMeh());
    } else if (change < 0) {
      m_change.setInnerText("\u2193" + Numbers.toFixed(absChange * 100, 1)
          + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeYay());
    } else {
      m_change.setInnerText("\u2191" + Numbers.toFixed(absChange * 100, 1)
          + "%");
      m_change.setClassName(m_css.change() + " " + m_css.changeBoo());
    }
  }
}
