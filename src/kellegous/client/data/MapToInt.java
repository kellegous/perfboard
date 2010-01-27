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

/**
 * 
 * @param <K>
 */
public final class MapToInt<K> extends DataStructure {
  protected MapToInt() {
  }

  public native int get(K key) /*-{
    return this[key];
  }-*/;

  public native void set(K key, int value) /*-{
    this[key] = value;
  }-*/;
}
