package kellegous.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RpcServlet extends HttpServlet {

  interface Command {
    JsonValue execute(PathArgs args, HttpServletRequest req, HttpServletResponse res) throws Exception;
  }

  private static class LoadCommand implements Command {

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

    private static JsonArray stringArrayToJsonArray(String[] array) {
      final JsonArray json = JsonArray.create();
      for (String s : array)
        json.add(s);
      return json;
    }

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

  static class PathArgs {
    private final String[] m_args;

    PathArgs(String[] args) {
      m_args = args;
    }

    String command() {
      return m_args[1];
    }

    String stringArg(int i) {
      return m_args[i + 2];
    }

    int intArg(int i) {
      return Integer.parseInt(stringArg(i));
    }

    static PathArgs fromPath(String path) {
      if (path == null)
        return null;
      return new PathArgs(path.split("/"));
    }
  }

  private final Map<String, Command> m_commands = new HashMap<String, Command>();

  {
    m_commands.put("load", new LoadCommand());
    m_commands.put("importmockdata", new ImportMockDataCommand());
  }

  private static Writer setupWriter(HttpServletRequest req, HttpServletResponse res) throws IOException {
    final String accept = req.getHeader("accept-encoding");
    if (accept != null && accept.indexOf("gzip") != -1) {
      res.addHeader("Content-Encoding", "gzip");
      return new OutputStreamWriter(new GZIPOutputStream(res.getOutputStream()));
    } else {
      return res.getWriter();
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    final PathArgs args = PathArgs.fromPath(req.getPathInfo());
    if (args == null)
      return;

    final Command command = m_commands.get(args.command());
    if (command == null) {
      res.sendError(404);
      return;
    }

    try {
      final JsonValue result = command.execute(args, req, res);
      if (result != null) {
        res.setContentType("application/json");
        final Writer writer = setupWriter(req, res);
        try {
          result.write(writer);
        } finally {
          writer.close();
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
