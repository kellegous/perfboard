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
 * @param <V>
 */
public final class MapFromString<V> extends DataStructure {
  public static <T> MapFromString<T> create() {
    return JavaScriptObject.createObject().cast();
  }

  protected MapFromString() {
  }

  public native V get(String key) /*-{
    return this[key + ':'];
  }-*/;

  public native void set(String key, V value) /*-{
    this[key + ':'] = value;
  }-*/;
}
