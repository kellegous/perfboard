package kellegous.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleInjector;

public class Perfboard implements EntryPoint {
  public interface Resources extends Header.Resources {
  }

  public void onModuleLoad() {
    final Resources resources = GWT.create(Resources.class);
    StyleInjector.inject(resources.headerCss().getText());
    final DivElement rootElem = Document.get().getElementById("a").cast();
    final Model model = new Model();
    new Header(rootElem, model, resources);
    final Graph graph = new Graph(rootElem);
    Dom.schedule(new Runnable() {
      @Override
      public void run() {
        graph.render();
      }
    });
  }
}
