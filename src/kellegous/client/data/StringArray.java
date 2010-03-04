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
 */
public final class StringArray extends JavaScriptObject {
  public static StringArray create() {
    return JavaScriptObject.createArray().cast();
  }

  protected StringArray() {
  }

  public String get(int index) {
    return this.<JsArray> cast().getString(index);
  }

  public int size() {
    return this.<JsArray> cast().size();
  }

  public void set(int index, String value) {
    this.<JsArray> cast().setString(index, value);
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public String join(String separator) {
    return this.<JsArray> cast().join(separator);
  }

  public StringArray slice(int start, int end) {
    return this.<JsArray> cast().slice(start, end).cast();
  }

  public StringArray slice(int start) {
    return this.<JsArray> cast().slice(start).cast();
  }
  
  public void reverse() {
    this.<JsArray>cast().reverse();
  }  
}
