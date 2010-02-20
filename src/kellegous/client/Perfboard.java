package kellegous.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;

import kellegous.client.model.Model;

public class Perfboard implements EntryPoint {

  public static class Logger implements Debug.Logger, ClickListener {
    private static Css m_css;

    private final DivElement m_root;
    private final DivElement m_container;
    private final DivElement m_title;

    private boolean m_collapsed;
    private int m_count;

    interface Css extends CssResource {
      String logger();

      String title();

      String container();

      String entry();

      String containerHeight();
    }

    interface Resources extends ClientBundle {
      @Source("resources/logger.css")
      Css css();
    }

    private static Css css() {
      if (m_css != null)
        return m_css;

      final Css css = GWT.<Resources> create(Resources.class).css();
      StyleInjector.inject(css.getText());
      return css;
    }

    public Logger(Document document) {
      final Css css = css();
      final DivElement root = document.createDivElement();
      final DivElement container = document.createDivElement();
      final DivElement title = document.createDivElement();

      root.setClassName(css.logger());
      container.setClassName(css.container());
      title.setClassName(css.title());

      root.appendChild(title);
      root.appendChild(container);
      document.getBody().appendChild(root);

      m_root = root;
      m_container = container;
      m_title = title;
      m_css = css;

      ClickEvent.addClickListener(title, title, this);
      update();
    }

    private void update() {
      m_title.setInnerText("Debug Log (" + m_count + ")");
    }

    @Override
    public void log(String message) {
      final DivElement entry = m_root.getOwnerDocument().createDivElement();
      entry.setInnerText(message);
      entry.setClassName(m_css.entry());
      m_container.insertBefore(entry, m_container.getFirstChild());
      m_count++;
      update();
    }

    @Override
    public void onClick(ClickEvent event) {
      m_collapsed = !m_collapsed;
      m_container.getStyle().setProperty("height", m_collapsed ? "0" : m_css.containerHeight());
    }

  }

  public interface Resources extends Header.Resources, SizeGraph.Resources {
  }

  public void onModuleLoad() {
    final String[] sizeTags = new String[] {"mail", "json", "showcase", "hello", "dynatable",};
    Debug.init(new Logger(Document.get()));
    final Resources resources = GWT.create(Resources.class);
    StyleInjector.inject(resources.headerCss().getText() + resources.sizeGraphCss().getText());

    final Model model = new Model(MockData.createClient());

    final DivElement rootElem = Document.get().getElementById("a").cast();
    new Header(rootElem, model, resources);
    for (int i = 0, n = sizeTags.length; i < n; ++i)
      new SizeGraph(resources.sizeGraphCss(), rootElem, model, sizeTags[i]);
    model.load();
  }
}
