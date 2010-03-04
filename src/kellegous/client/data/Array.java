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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @param <T>
 */
public final class Array<T> extends DataStructure {
  public static <T> Array<T> create() {
    return JavaScriptObject.createArray().cast();
  }

  protected Array() {
  }

  public void append(T value) {
    set(size(), value);
  }

  public T get(int index) {
    return this.<JsArray> cast().<T> get(index);
  }

  public int size() {
    return this.<JsArray> cast().size();
  }

  public boolean remove(int index) {
    return this.<JsArray> cast().remove(index);
  }

  public void resize(int size) {
    this.<JsArray> cast().resize(size);
  }

  public void set(int index, T value) {
    this.<JsArray> cast().set(index, value);
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public static int size(JavaScriptObject obj) {
    return obj.<JsArray> cast().size();
  }

  public Array<T> slice(int start, int end) {
    return this.<JsArray> cast().slice(start, end).cast();
  }

  public Array<T> slice(int start) {
    return this.<JsArray> cast().slice(start).cast();
  }

  public void reverse() {
    this.<JsArray> cast().reverse();
  }
}
