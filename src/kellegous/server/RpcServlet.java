package kellegous.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RpcServlet extends HttpServlet {

  private static class DataArgs {
    private static final long HEAD = -1;
    private final long m_revision;
    private final int m_count;

    private DataArgs(long revision, int count) {
      m_revision = revision;
      m_count = count;
    }

    long revision() {
      return m_revision;
    }

    int count() {
      return m_count;
    }

    static DataArgs parse(String query) {
      if (query.length() == 1)
        return new DataArgs(HEAD, 400);
      final int index = query.indexOf('+', 1);
      if (index < 0)
        return new DataArgs(Long.parseLong(query.substring(1)), 400);
      else
        return new DataArgs(Long.parseLong(query.substring(1, index)), Integer.parseInt(query.substring(index + 1)));
    }
  }

  private static void doDataQuery(DataArgs args, HttpServletResponse res) {

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    final String query = req.getQueryString();
    if (query == null || query.length() < 2)
      return;

    switch (query.indexOf(0)) {
      case 'D':
        doDataQuery(DataArgs.parse(query), res);
        break;
      case 'R':
        break;
    }
  }
}
