package kellegous.client;

public class Debug {
  public interface Logger {
    void log(String message);
  }
  
  public static class ConsoleLogger implements Logger {
    @Override
    public native void log(String message) /*-{
      $wnd.console && $wnd.console.log(message);
    }-*/;
  }
  
  private static Logger m_logger;

  public static void init(Logger logger) {
    assert m_logger == null;
    m_logger = logger;
  }
  
  public static void log(String message) {
    if (m_logger != null)
      m_logger.log(message);
  }
  
  public static boolean enabled() {
    return m_logger != null;
  }
}
