package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Revision {
  private final Entity m_entity;

  public Revision(Entity entity) {
    assert KIND.equals(entity.getKind());
    m_entity = entity;
  }

  public static final String KIND = "revision";

  public static final String ID = "id";
  public static final String AUTHOR = "author";
  public static final String MESSAGE = "message";
  public static final String DATE = "date";

  private static List<Text> toTextList(String[] message) {
    final List<Text> list = new ArrayList<Text>(message.length);
    for (String line : message)
      list.add(new Text(line));
    return list;
  }

  private static String[] fromTextList(List<Text> list) {
    final String[] message = new String[list.size()];
    for (int i = 0, n = message.length; i < n; ++i)
      message[i] = list.get(i).getValue();
    return message;
  }

  public Revision(String id, String author, String[] message, Date date) {
    m_entity = new Entity(KIND, id);
    m_entity.setProperty(AUTHOR, author);
    m_entity.setProperty(MESSAGE, toTextList(message));
    m_entity.setProperty(DATE, date);
  }

  public String id() {
    return m_entity.getKey().getName();
  }

  public String author() {
    return (String)m_entity.getProperty(AUTHOR);
  }

  @SuppressWarnings("unchecked")
  public String[] message() {
    // TODO(knorton): There is no practical need to store these in Text objects
    // because they will actually be broken into lines and the rewriter will
    // ensure that there are no super long lines.
    return fromTextList((List<Text>)m_entity.getProperty(MESSAGE));
  }

  public Date date() {
    return (Date)m_entity.getProperty(DATE);
  }

  public static Revision latest(DatastoreService store) {
    final Iterator<Entity> iter = store.prepare(new Query(KIND).addSort(DATE, SortDirection.DESCENDING)).asIterator(FetchOptions.Builder.withLimit(1));
    return (iter.hasNext()) ? new Revision(iter.next()) : null;
  }

  public static Revision find(DatastoreService store, String id) {
    try {
      return new Revision(store.get(KeyFactory.createKey(KIND, id)));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  public void save(DatastoreService store) {
    store.put(m_entity);
  }
}
