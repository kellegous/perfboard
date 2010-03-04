/*
 * Copyright 2008 Kelly Norton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package kellegous.client.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 */
final class JsArray extends JavaScriptObject {

  private static native String getType(boolean value) /*-{
    return typeof value;
  }-*/;

  private static native String getType(double value) /*-{
    return typeof value;
  }-*/;

  private static native String getType(Object value) /*-{
    return typeof value;
  }-*/;

  private static native String getType(String value) /*-{
    return typeof value;
  }-*/;

  private static boolean isBoolean(boolean value) {
    return "boolean".equals(getType(value));
  }

  private static boolean isNumber(double value) {
    return "number".equals(getType(value));
  }

  private static boolean isObject(Object value) {
    return "object".equals(getType(value));
  }

  private static boolean isString(String value) {
    return "string".equals(getType(value));
  }

  protected JsArray() {
  }

  public <T> T get(int index) {
    assert indexIsInBounds(index);
    return this.<T> getImpl(index);
  }

  public boolean getBoolean(int index) {
    assert indexIsInBounds(index);
    return getBooleanImpl(index);
  }

  public byte getByte(int index) {
    assert indexIsInBounds(index);
    return getByteImpl(index);
  }

  public char getChar(int index) {
    assert indexIsInBounds(index);
    return getCharImpl(index);
  }

  public double getDouble(int index) {
    assert indexIsInBounds(index);
    return getDoubleImpl(index);
  }

  public float getFloat(int index) {
    assert indexIsInBounds(index);
    return getFloatImpl(index);
  }

  public int getInt(int index) {
    assert indexIsInBounds(index);
    return getIntImpl(index);
  }

  public long getLong(int index) {
    assert indexIsInBounds(index);
    // Cast to long force long emulation.
    return (long)getDoubleImpl(index);
  }

  public native int size() /*-{
    return this.length;
  }-*/;

  public String getString(int index) {
    assert indexIsInBounds(index);
    return getStringImpl(index);
  }

  public native boolean remove(int index) /*-{
    return delete this[index];
  }-*/;

  public native String join(String separator) /*-{
    return this.join(separator);
  }-*/;

  public native JsArray slice(int start, int end) /*-{
    return this.slice(start, end);
  }-*/;
  
  public native JsArray slice(int start) /*-{
    return this.slice(start);
  }-*/;

  public native void resize(int size) /*-{
    this.length = size;
  }-*/;

  public <T> void set(int index, T value) {
    // TODO(knorton): GWT 2.0 doesn't represent Java objects as objects in
    // DevMode.
    assert !GWT.isScript() || isObject(value);
    setImpl(index, value);
  }

  public void setBoolean(int index, boolean value) {
    assert isBoolean(value);
    setBooleanImpl(index, value);
  }

  public void setNumber(int index, double value) {
    assert isNumber(value);
    setNumberImpl(index, value);
  }

  public void setString(int index, String value) {
    assert isString(value);
    setStringImpl(index, value);
  }

  public native void reverse() /*-{
    this.reverse();
  }-*/;

  private native boolean getBooleanImpl(int index) /*-{
    return this[index];
  }-*/;

  private native byte getByteImpl(int index) /*-{
    return this[index];
  }-*/;

  private native char getCharImpl(int index) /*-{
    return this[index];
  }-*/;

  private native double getDoubleImpl(int index) /*-{
    return this[index];
  }-*/;

  private native float getFloatImpl(int index) /*-{
    return this[index];
  }-*/;

  private native <T> T getImpl(int index) /*-{
    return this[index];
  }-*/;

  private native int getIntImpl(int index) /*-{
    return this[index];
  }-*/;

  private native String getStringImpl(int index) /*-{
    return this[index];
  }-*/;

  private boolean indexIsInBounds(int index) {
    return index < size() && index >= 0;
  }

  private native void setBooleanImpl(int index, boolean value) /*-{
    this[index] = value;
  }-*/;

  private native void setImpl(int index, Object value) /*-{
    this[index] = value;
  }-*/;

  private native void setNumberImpl(int index, double value) /*-{
    this[index] = value;
  }-*/;

  private native void setStringImpl(int index, String value) /*-{
    this[index] = value;
  }-*/;
}
