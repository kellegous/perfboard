/**
 * 
 */
package kellegous.server;

class PathArgs {
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