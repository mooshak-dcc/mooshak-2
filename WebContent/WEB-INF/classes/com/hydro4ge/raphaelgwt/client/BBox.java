/*
 * Copyright 2010 Hydro4GE, Incorporated. http://www.hydro4ge.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hydro4ge.raphaelgwt.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * bounding box object returned by Element.getBBox()
 */
public class BBox extends JavaScriptObject {

  protected BBox() {}

  public final native double x() /*-{
    if (this.x == undefined)
      return -1;
    else
      return this.x;
  }-*/;

  public final native double y() /*-{
    if (this.y == undefined)
      return -1;
    else
      return this.y;
  }-*/;

  public final native double width() /*-{
    if (this.width == undefined)
      return -1;
    else
      return this.width;
  }-*/;

  public final native double height() /*-{
    if (this.height == undefined)
      return -1;
    else
      return this.height;
  }-*/;

}

