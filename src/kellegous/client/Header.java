package kellegous.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import kellegous.client.data.StringArray;

public class Header implements Model.Listener {

  private static final int MAX_LINES_IN_HEADER = 4;

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

  public Header(Element parent, Model model, Resources resources) {
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

  private void clearMessage() {
    m_message.setInnerText("");
  }

  private void appendToMessage(String text) {
    final DivElement e = m_message.getOwnerDocument().createDivElement();
    e.setInnerText(text.isEmpty() ? "\u00a0" : text);
    m_message.appendChild(e);
  }

  private void update(Model model) {
    final Model.Revision revision = model.currentRevision();
    m_revision.setInnerText(Model.Revision.format(revision));
    m_author.setInnerText(Model.Revision.shortenAuthor(revision.author()));

    // TODO(knorton): Just reuse existing divs.
    // TODO(knorton): Linkify the message.
    // TODO(knorton): Big lines need to be broken in some cases, so
    // this is going to be better done by the bot ... so the message
    // should actually arrive here as HTML ... making all this trivial.
    
    clearMessage();
    final StringArray message = revision.message();
    final int n = message.size();

    if (n > MAX_LINES_IN_HEADER) {
      // Too many lines, truncate.
      for (int i = 0, m = MAX_LINES_IN_HEADER - 1; i < m; ++i)
        appendToMessage(message.get(i));
      appendToMessage("\u2026");
    } else {
      for (int i = 0;i<n;++i)
        appendToMessage(message.get(i));
    }
  }

  @Override
  public void allRevisionsDidFailToLoad(Model model) {
  }

  @Override
  public void allRevisionsDidLoad(Model model) {
    update(model);
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
