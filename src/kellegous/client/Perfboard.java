package kellegous.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleInjector;

import kellegous.client.Controller.Callback;
import kellegous.client.Controller.Response;

public class Perfboard implements EntryPoint {
  private static class MockClient implements Controller.Client {
    @Override
    public void loadAllRevisions(int n, Callback<Response> callback) {
    }

    @Override
    public void loadNewRevisions(double sinceRevision, Callback<Response> callback) {
    }
  }
  
  public interface Resources extends Header.Resources {
  }

  public void onModuleLoad() {
    final Resources resources = GWT.create(Resources.class);
    StyleInjector.inject(resources.headerCss().getText());
    
    final Controller model = new Controller(new MockClient());
    
    final DivElement rootElem = Document.get().getElementById("a").cast();
    new Header(rootElem, model, resources);    
    for (int i = 0;i<10;++i) {
      final Graph graph = new Graph(rootElem);
      Dom.schedule(new Runnable() {
        @Override
        public void run() {
          graph.render();
        }
      });
      
      model.loadAll();
    }
  }
}
