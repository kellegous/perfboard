package kellegous.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class NumberArray extends DataStructure {
  public static NumberArray create() {
    return JavaScriptObject.createArray().cast();
  }

  protected NumberArray() {
  }

  public final double get(int index) {
    return this.<JsArray> cast().getDouble(index);
  }

  public final int size() {
    return this.<JsArray> cast().size();
  }

  public final void set(int index, double value) {
    this.<JsArray> cast().setNumber(index, value);
  }
}
