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
public final class IntArray extends DataStructure {
  public static IntArray create() {
    return JavaScriptObject.createArray().cast();
  }

  protected IntArray() {
  }

  public int get(int index) {
    return this.<JsArray> cast().getInt(index);
  }

  public int size() {
    return this.<JsArray> cast().size();
  }

  public void set(int index, int value) {
    this.<JsArray> cast().setNumber(index, value);
  }
  
  public boolean isEmpty() {
    return size() == 0;
  }
}
