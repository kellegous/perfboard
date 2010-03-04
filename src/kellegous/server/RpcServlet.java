package kellegous.server;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RpcServlet extends HttpServlet {

  private final Map<String, Command> m_commands = createCommandMap();

  private static Map<String, Command> createCommandMap() {
    final Map<String, Command> commands = new HashMap<String, Command>();
    commands.put("load", new Commands.LoadCommand());
    commands.put("import-mock-data", new Commands.ImportMockData());
    commands.put("get-head-revision", new Commands.GetHeadRevision());
    commands.put("record-revision", new Commands.RecordRevision());
    return commands;
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

  private void invoke(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
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

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // TODO(knorton): Validate the command.
    invoke(req, res);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // TODO(knorton): Validate the Command.
    invoke(req, res);
  }
}
