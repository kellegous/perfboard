package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

public class Branch {
  public static final String KIND = "branch";

  public static final String COUNT = "count";

  private final Entity m_entity;

  public Branch(Entity entity) {
    assert KIND.equals(entity.getKind());
    m_entity = entity;
  }

  public Branch(String name) {
    m_entity = new Entity(KIND, name);
    setCount(0);
  }

  public String name() {
    return m_entity.getKey().getName();
  }

  public int count() {
    return ((Long)m_entity.getProperty(COUNT)).intValue();
  }

  public void setCount(int count) {
    m_entity.setProperty(COUNT, Long.valueOf(count));
  }

  public int increment() {
    final int newCount = count() + 1;
    setCount(newCount);
    return newCount;
  }

  public static Branch find(DatastoreService store, String name) {
    try {
      return new Branch(store.get(KeyFactory.createKey(KIND, name)));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  public void save(DatastoreService store) {
    store.put(m_entity);
  }
}
