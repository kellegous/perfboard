package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import elemental.json.JsonException;
import elemental.json.JsonNumber;
import elemental.json.JsonObject;
import elemental.json.JsonString;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RpcServlet extends HttpServlet {
  private static void sendJsonResponse(HttpServletResponse res, JsonObject json) throws IOException {
    res.setContentType("application/json");
    json.write(res.getWriter());
  }

  private static void respondToStartCommand(HttpServletRequest req, HttpServletResponse res, JsonObject json) throws IOException {
    final JsonString branch = json.get("branch").asString();
    if (branch == null)
      res.sendError(400);

    final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
    final RevisionData data = RevisionData.current(store, branch.getString());

    final JsonObject result = JsonObject.create();
    if (data != null)
      result.put("revision", data.revision());
    sendJsonResponse(res, result);
  }

  private static void respondToReportResultCommand(HttpServletRequest req, HttpServletResponse res, JsonObject json) throws IOException {
    final JsonString branch = json.get("branch").asString();
    final JsonNumber revision = json.get("revision").asNumber();
    final JsonObject data = json.get("data").asObject();
    if (branch == null || revision == null || data == null)
      res.sendError(400);

    final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
    final RevisionData pd = RevisionData.find(store, branch.getString(), revision.getInteger());
    if (pd == null) {
      new RevisionData(branch.getString(), revision.getInteger(), data).save(store);
    } else {
      pd.updateData(data);
      pd.save(store);
    }

    final JsonObject result = JsonObject.create();
    sendJsonResponse(res, result);
  }

  private static String command(JsonObject json) {
    final JsonString command = json.get("command").asString();
    return (command != null) ? command.getString() : null;
  }

  private static boolean fromAuthorizedAgent(JsonObject json) {
    return true;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    try {
      final JsonObject json = JsonObject.parse(req.getReader());
      final String command = command(json);

      if (!fromAuthorizedAgent(json)) {
        res.sendError(403);
        return;
      }

      if ("start".equals(command))
        respondToStartCommand(req, res, json);
      else if ("report".equals(command))
        respondToReportResultCommand(req, res, json);
      else
        res.sendError(400);
    } catch (JsonException e) {
      res.sendError(400);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    final Iterator<RevisionData> revs = RevisionData.recent(DatastoreServiceFactory.getDatastoreService(), "trunk", 100);
    res.setContentType("text/plain");
    final Writer writer = res.getWriter();
    while (revs.hasNext()) {
      final RevisionData rev = revs.next();
      writer.write(rev.revision() + "\n");
      rev.data().write(writer);
      writer.write("\n");
    }
  }
}
