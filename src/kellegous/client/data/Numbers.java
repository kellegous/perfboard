package kellegous.client.data;

public class Numbers {
  public static native String toFixed(double number, int n) /*-{
    return number.toFixed(n);
  }-*/;

  public static String toInt(double number) {
    return toFixed(number, 0);
  }

  private Numbers() {
  }
}
