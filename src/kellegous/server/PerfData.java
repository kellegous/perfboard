package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import elemental.base.Pair;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

public class PerfData {
  private static JsonObject stringToJson(String json) throws JsonException {
    final StringReader reader = new StringReader(json);
    try {
      return JsonObject.parse(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String jsonToString(JsonObject json) {
    final StringWriter writer = new StringWriter();
    try {
      json.write(writer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return writer.toString();
  }

  private static final String KIND = "perf-data";

  public static final String BRANCH = "branch";
  public static final String REVISION = "revision";
  public static final String DATA = "data";
  public static final String SORT_KEY = "sort-key";

  private final Entity m_entity;

  private PerfData(Entity entity) {
    assert KIND.equals(entity.getKind());
    m_entity = entity;
  }

  public PerfData(String branch, String revision, long sortKey, JsonObject data) {
    m_entity = new Entity(KIND, keyFor(branch, revision));
    m_entity.setProperty(BRANCH, branch);
    m_entity.setProperty(REVISION, revision);
    m_entity.setProperty(SORT_KEY, sortKey);
    setData(data);
  }

  public String branch() {
    return (String)m_entity.getProperty(BRANCH);
  }

  public String revision() {
    return (String)m_entity.getProperty(REVISION);
  }

  public JsonObject data() {
    final String data = (String)m_entity.getProperty(DATA);
    if (data == null)
      return null;
    try {
      return stringToJson(data);
    } catch (JsonException e) {
      return null;
    }
  }

  public void setData(JsonObject data) {
    if (data == null)
      m_entity.removeProperty(DATA);
    else
      m_entity.setProperty(DATA, jsonToString(data));
  }

  private static JsonObject merge(JsonObject a, JsonObject b) {
    for (Pair<String, JsonValue> p : b)
      a.put(p.getA(), p.getB());
    return a;
  }

  public void updateData(JsonObject data) {
    final JsonObject current = data();
    if (current == null)
      setData(data);
    else
      setData(merge(current, data));
  }

  public void save(DatastoreService store) {
    store.put(m_entity);
  }

  private static String keyFor(String branch, String revision) {
    return branch + revision;
  }

  public static PerfData find(DatastoreService store, String branch, String revision) {
    try {
      return new PerfData(store.get(KeyFactory.createKey(KIND, keyFor(branch, revision))));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  private static class Iter implements Iterator<PerfData> {
    private final Iterator<Entity> m_iter;

    public Iter(Iterator<Entity> iter) {
      m_iter = iter;
    }

    @Override
    public boolean hasNext() {
      return m_iter.hasNext();
    }

    @Override
    public PerfData next() {
      return new PerfData(m_iter.next());
    }

    @Override
    public void remove() {
      m_iter.remove();
    }
  }

  public static PerfData current(DatastoreService store, String branch) {
    final Iterator<PerfData> iter = recent(store, branch, 1);
    return iter.hasNext() ? iter.next() : null;
  }

  public static Iterator<PerfData> recent(DatastoreService store, String branch, int limit) {
    final Query query = new Query(KIND).addFilter(BRANCH, FilterOperator.EQUAL, branch).addSort(SORT_KEY, SortDirection.DESCENDING);
    return new Iter(store.prepare(query).asIterator(FetchOptions.Builder.withLimit(limit)));
  }
}
