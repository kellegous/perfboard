package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public class SectionHeaderView {
  public interface Css extends CssResource{
    String header();
  }
  
  public interface Resources extends ClientBundle {
    @Source("resources/section-header.css")
    Css sectionHeaderCss();
  }
  
  public static void add(Resources resources, Element parent, String html) {
    final Document document = parent.getOwnerDocument();
    final DivElement header = document.createDivElement();
    
    header.setClassName(resources.sectionHeaderCss().header());
    
    parent.appendChild(header);
    
    header.setInnerHTML(html);
  }
}
