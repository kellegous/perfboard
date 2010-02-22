/**
 * 
 */
package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kellegous.server.RpcServlet.Command;
import kellegous.server.RpcServlet.PathArgs;

class ImportMockDataCommand implements Command {
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
      jsonObjectToRevision(json).save(store);
    }

    return JsonObject.create();
  }
}