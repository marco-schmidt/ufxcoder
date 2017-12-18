/*
 * Copyright 2017 the original author or authors.
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
package ufxcoder.formats.jpeg;

/**
 * Various literal values used in JPEG streams.
 */
public final class Constants
{
  /**
   * Signature of JPEG files.
   */
  public static final int MARKER_START_OF_IMAGE = 0xffd8;

  /**
   * End-of-stream marker.
   */
  public static final int MARKER_END_OF_IMAGE = 0xffd9;

  /**
   * Mask value that identifies a valid marker.
   */
  public static final int MARKER_MASK = 0xff00;

  private Constants()
  {
    // avoid instantiation of this helper class
  }
}
