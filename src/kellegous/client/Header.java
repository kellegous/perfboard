package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public class Header implements Controller.Listener {

  public interface Css extends CssResource {
    String author();

    String revision();

    String header();

    String title();

    String message();
  }

  public interface Resources extends ClientBundle {

    @Source("resources/header.css")
    Css headerCss();
  }

  private final DivElement m_message;
  private final DivElement m_revision;
  private final DivElement m_author;

  public Header(Element parent, Controller model, Resources resources) {
    final Document document = parent.getOwnerDocument();

    final DivElement root = document.createDivElement();
    final DivElement title = document.createDivElement();
    final DivElement revision = document.createDivElement();
    final DivElement author = document.createDivElement();
    final DivElement message = document.createDivElement();

    final Css css = resources.headerCss();
    root.setClassName(css.header());
    title.setClassName(css.title());
    revision.setClassName(css.revision());
    author.setClassName(css.author());
    message.setClassName(css.message());

    title.appendChild(revision);
    title.appendChild(author);
    root.appendChild(title);
    root.appendChild(message);
    parent.appendChild(root);

    m_message = message;
    m_revision = revision;
    m_author = author;

    model.addListener(this);
  }

  /*
   * private void update(Model model) { final Model.Revision revision =
   * model.currentRevision(); m_revision.setInnerText(revision.number());
   * m_author.setInnerText(revision.author());
   * m_message.setInnerText(revision.message()); }
   */

  @Override
  public void allRevisionsDidFailToLoad(Controller model) {
  }

  @Override
  public void allRevisionsDidLoad(Controller model) {
  }

  @Override
  public void newRevisionsDidLoad(Controller model) {
  }

  @Override
  public void serverDidStartResponding(Controller model) {
  }

  @Override
  public void serverDidStopResponding(Controller model) {
  }
}
