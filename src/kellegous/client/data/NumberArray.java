package kellegous.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class NumberArray extends DataStructure {
  public static NumberArray create() {
    return JavaScriptObject.createArray().cast();
  }

  protected NumberArray() {
  }

  public double get(int index) {
    return this.<JsArray> cast().getDouble(index);
  }

  public int size() {
    return this.<JsArray> cast().size();
  }

  public void set(int index, double value) {
    this.<JsArray> cast().setNumber(index, value);
  }
}
