package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Arrays;
import java.util.Date;
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

  public Revision(String id, String author, String[] message, Date date) {
    m_entity = new Entity(KIND, id);
    m_entity.setProperty(AUTHOR, author);
    m_entity.setProperty(MESSAGE, Arrays.asList(message));
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
    final List<String> list =  (List<String>)m_entity.getProperty(MESSAGE);
    final String[] array = new String[list.size()];
    return list.toArray(array);
  }

  public Date date() {
    return (Date)m_entity.getProperty(DATE);
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
