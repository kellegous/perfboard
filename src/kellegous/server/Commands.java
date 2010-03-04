package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Commands {
  private static Date jsonArrayToDate(JsonArray json) {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, (int)json.get(0).asNumber().getInteger());
    cal.set(Calendar.MONTH, (int)json.get(1).asNumber().getInteger());
    cal.set(Calendar.DATE, (int)json.get(2).asNumber().getInteger());
    cal.set(Calendar.HOUR, (int)json.get(3).asNumber().getInteger());
    cal.set(Calendar.MINUTE, (int)json.get(4).asNumber().getInteger());
    cal.set(Calendar.SECOND, (int)json.get(5).asNumber().getInteger());
    return cal.getTime();
  }

  private static JsonArray dateToJsonArray(Date date) {
    final JsonArray json = JsonArray.create();
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    json.add(cal.get(Calendar.YEAR));
    json.add(cal.get(Calendar.MONTH));
    json.add(cal.get(Calendar.DATE));
    json.add(cal.get(Calendar.HOUR));
    json.add(cal.get(Calendar.MINUTE));
    json.add(cal.get(Calendar.SECOND));
    return json;
  }

  private static String[] jsonArrayToStringArray(JsonArray json) {
    final String[] strings = new String[json.getLength()];
    for (int i = 0, n = strings.length; i < n; ++i)
      strings[i] = json.get(i).asString().getString();
    return strings;
  }

  private static JsonArray stringArrayToJsonArray(String[] array) {
    final JsonArray json = JsonArray.create();
    for (String s : array)
      json.add(s);
    return json;
  }

  private static Revision jsonObjectToRevision(JsonObject json) {
    final String id = json.get("id").asString().getString();
    final String[] message = jsonArrayToStringArray(json.get("message").asArray());
    final String author = json.get("author").asString().getString();
    final Date date = jsonArrayToDate(json.get("date").asArray());
    return new Revision(id, author, message, date);
  }

  private static JsonObject revisionToJsonObject(Revision revision) {
    final JsonObject json = JsonObject.create();
    json.put(Revision.ID, revision.id());
    json.put(Revision.AUTHOR, revision.author());
    json.put(Revision.DATE, dateToJsonArray(revision.date()));
    json.put(Revision.MESSAGE, stringArrayToJsonArray(revision.message()));
    return json;
  }

  private static JsonObject createStatusResponse(boolean success) {
    final JsonObject json = JsonObject.create();
    json.put("ok?", success);
    return json;
  }

  static class GetHeadRevision implements Command {
    @Override
    public JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) throws Exception {
      // TODO(knorton): Cache this.
      final Revision revision = Revision.latest(DatastoreServiceFactory.getDatastoreService());
      // TODO(knorton): This is not right in the case of no revision at all.
      return revision == null ? JsonObject.create() : revisionToJsonObject(revision);
    }
  }

  static class RecordRevision implements Command {
    @Override
    public JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) throws Exception {
      jsonObjectToRevision(JsonObject.parse(req.getReader())).save(DatastoreServiceFactory.getDatastoreService());
      return createStatusResponse(true);
    }
  }

  static class LoadCommand implements Command {
    private static JsonArray revisionToArray(Revision revision) {
      final JsonArray result = JsonArray.create();
      result.add(revision.id());
      result.add(stringArrayToJsonArray(revision.message()));
      result.add(revision.author());
      result.add(dateToJsonArray(revision.date()));
      return result;
    }

    private static JsonObject perfDataToJsonObject(PerfData perfData) {
      final JsonObject json = JsonObject.create();
      json.put("revision", perfData.revision());
      json.put("data", perfData.data());
      return json;
    }

    private static JsonArray createResponse(Revision revision, Branch branch, PerfData first, Iterator<PerfData> rest) {
      final JsonArray result = JsonArray.create();
      result.add(revisionToArray(revision));
      result.add(branch.count());
      final JsonArray perfData = JsonArray.create();
      perfData.add(perfDataToJsonObject(first));
      while (rest.hasNext())
        perfData.add(perfDataToJsonObject(rest.next()));
      result.add(perfData);
      return result;
    }

    @Override
    public JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) {
      final String branchName = args.stringArg(0);
      final int limit = args.intArg(1);

      final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
      // TODO(knorton): Retrieve from cache.
      final Branch branch = Branch.find(store, branchName);
      if (branch == null)
        return null;
      // TODO(knorton): Retrieve from cache.
      final Iterator<PerfData> revisions = PerfData.recent(DatastoreServiceFactory.getDatastoreService(), branchName, limit);

      if (!revisions.hasNext())
        // TODO(knorton): Return an empty response.
        return null;

      final PerfData headData = revisions.next();
      // TODO(knorton): Retrieve from cache.
      final Revision headRevision = Revision.find(store, headData.revision());
      return createResponse(headRevision, branch, headData, revisions);
    }
  }

  static class ImportMockData implements Command {
    private static String[] jsonArrayToStringArray(JsonArray json) {
      final String[] result = new String[json.getLength()];
      for (int i = 0, n = result.length; i < n; ++i)
        result[i] = json.get(i).asString().getString();
      return result;
    }

    private static Date jsonArrayToDate(JsonArray json) {
      final Calendar cal = Calendar.getInstance();
      final int[] fields = new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND};
      for (int i = 0, n = fields.length; i < n; ++i)
        cal.set(fields[i], (int)json.get(i).asNumber().getInteger());
      return cal.getTime();
    }

    private static Revision jsonObjectToRevision(JsonObject json) {
      final String revision = json.get("revision").asString().getString();
      final String[] message = jsonArrayToStringArray(json.get("message").asArray());
      final String author = json.get("author").asString().getString();
      final Date date = jsonArrayToDate(json.get("date").asArray());
      return new Revision(revision, author, message, date);
    }

    private static PerfData jsonObjectToPerfData(String branchName, JsonObject json) {
      final String revision = json.get("revision").asString().getString();
      final JsonObject data = json.get("data").asObject();
      final long sortKey = Long.parseLong(revision.substring(1));
      return new PerfData(branchName, revision, sortKey, data);
    }

    @Override
    public JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) throws IOException, JsonException {
      final String branchName = args.stringArg(0);

      final DatastoreService store = DatastoreServiceFactory.getDatastoreService();

      // TODO(knorton): Find branch and exit if it already exists.
      final InputStream stream = getClass().getResourceAsStream("mock-data.json");
      if (stream == null) {
        res.sendError(500);
        return null;
      }

      final InputStreamReader reader = new InputStreamReader(stream);
      final JsonArray revisions = JsonArray.parse(reader);

      final Branch branch = new Branch(branchName);
      branch.setCount(revisions.getLength());
      branch.save(store);

      for (int i = 0, n = revisions.getLength(); i < n; ++i) {
        final JsonObject json = revisions.get(i).asObject();
        jsonObjectToPerfData(branchName, json).save(store);
        // Only save the revision if there isn't one. This is a temporary
        // work around to keep the import from clobbering data from rev_bot.
        final Revision revision = jsonObjectToRevision(json);
        if (Revision.find(store, revision.id()) == null)
          revision.save(store);
      }

      return JsonObject.create();
    }
  }
}
