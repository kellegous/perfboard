package kellegous.client;

public class Dom {
  private Dom() {
  }
 
  public native static void scheduleRepeating(Runnable command, int interval) /*-{
    $wnd.setInterval(function() {
      command.@java.lang.Runnable::run()();
    }, interval);
  }-*/;
  
  public static void schedule(Runnable command) {
    schedule(command, 0);
  }
  
  public native static void schedule(Runnable command, int timeout) /*-{
    $wnd.setTimeout(function() {
      command.@java.lang.Runnable::run()();
    }, 0);
  }-*/;
}
