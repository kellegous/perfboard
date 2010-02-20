/**
 * 
 */
package kellegous.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.Json;
import kellegous.client.model.Model.SizeData;

public final class PerfData extends JavaScriptObject {
  protected PerfData() {
  }

  public native String revision() /*-{
    return this.revision;
  }-*/;

  private native JavaScriptObject dataFor(String name) /*-{
    return this.data[name];
  }-*/;

  public SizeData sizeDataFor(String name) {
    return SizeData.as(dataFor(name));
  }

  static String toString(PerfData pd) {
    return Json.stringify(pd);
  }
}