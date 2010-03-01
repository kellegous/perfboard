package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import kellegous.client.model.Model;

public class RevisionCalloutView {
  public interface Css extends CssResource {
    String container();

    String text();

    int offset();

    int width();

    int padding();
  }

  public interface Resources extends ClientBundle {
    @Source("resources/selection-hover.css")
    Css selectionHoverCss();
  }

  private static final int GRAPH_WIDTH = 800; /* px */

  private static class Controller implements SharedGraphEvents.Delegate {
    private final SpanElement m_text;
    private final Css m_css;
    private final Model m_model;

    Controller(Element parent, Resources resources, Model model) {
      final Css css = resources.selectionHoverCss();
      final Document document = parent.getOwnerDocument();
      final DivElement element = document.createDivElement();
      final SpanElement text = document.createSpanElement();

      element.setClassName(css.container());
      text.setClassName(css.text());

      element.appendChild(text);
      parent.appendChild(element);

      m_text = text;
      m_css = css;
      m_model = model;
    }

    @Override
    public void shouldHoverOn(int index) {
      if (!m_model.loaded())
        return;
      final double offset = m_css.offset() // left graph offset
          - m_css.width() / 2.0 // 1/2 the bar width
          - m_css.padding() // bar's left padding
          + index * (double)GRAPH_WIDTH / (double)m_model.selectionSize();
      final Style style = m_text.getStyle();
      style.setVisibility(Visibility.VISIBLE);
      style.setMarginLeft(offset, Unit.PX);
      m_text.setInnerText(m_model.results().get(index).revision());
    }

    @Override
    public void shouldRemoveHover() {
      m_text.getStyle().setVisibility(Visibility.HIDDEN);
    }

  }

  public static void attach(Element parent, Resources resources, Model model, SharedGraphEvents.Controller selectionController) {
    selectionController.addDelegate(new Controller(parent, resources, model));
  }
}
