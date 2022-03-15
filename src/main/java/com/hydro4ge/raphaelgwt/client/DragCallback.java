/*
 * Copyright 2011 Hydro4GE, Incorporated. http://www.hydro4ge.com/
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

/**
 * Drag and drop callback
 * @authors cristian.n.miranda, geoffspeicher
 */
public interface DragCallback {
  public void onStart(double x, double y);
  public void onMove(double dx, double dy, double x, double y);
  public void onEnd();
}

