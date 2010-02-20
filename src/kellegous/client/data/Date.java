package kellegous.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class Date extends JavaScriptObject {
  protected Date() {
  }

  public native static Date create() /*-{
    return new Date;
  }-*/;

  public native static Date create(double milliseconds) /*-{
    return new Date(milliseconds);
  }-*/;

  public native static Date create(String date) /*-{
    return new Date(date);
  }-*/;

  public native static Date create(int year, int month, int day, int hours, int minutes, int seconds, int milliseconds) /*-{
    return new Date(year, month, day, hours, minutes, seconds, milliseconds);
  }-*/;

  public final native int getDate() /*-{
    return this.getDate();
  }-*/;

  public final native int getDay() /*-{
    return this.getDay();
  }-*/;

  public final native int getFullYear() /*-{
    return this.getFullYear();
  }-*/;

  public final native int getHours() /*-{
    return this.getHours();
  }-*/;

  public final native int getMilliseconds() /*-{
    return this.getMilliseconds();
  }-*/;

  public final native int getMinutes() /*-{
    return this.getMinutes();
  }-*/;

  public final native int getMonth() /*-{
    return this.getMonth();
  }-*/;

  public final native int getSeconds() /*-{
    return this.getSeconds();
  }-*/;

  public final native double getTime() /*-{
    return this.getTime();
  }-*/;

  public final native int getTimezoneOffset() /*-{
    return this.getTimezoneOffset();
  }-*/;

  // TODO(knorton): Finish this.
}
