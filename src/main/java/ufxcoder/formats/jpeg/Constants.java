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
   * Start-of-frame 0 marker (baseline).
   */
  public static final int MARKER_START_OF_FRAME_0 = 0xffc0;

  /**
   * Start-of-frame 1 marker (extended sequential DCT).
   */
  public static final int MARKER_START_OF_FRAME_1 = 0xffc1;

  /**
   * Start-of-frame 2 marker (progressive DCT).
   */
  public static final int MARKER_START_OF_FRAME_2 = 0xffc2;

  /**
   * Start-of-frame 3 marker (lossless sequential).
   */
  public static final int MARKER_START_OF_FRAME_3 = 0xffc3;

  /**
   * Start-of-frame 5 marker (differential sequential DCT).
   */
  public static final int MARKER_START_OF_FRAME_5 = 0xffc5;

  /**
   * Start-of-frame 6 marker (differential progressive DCT).
   */
  public static final int MARKER_START_OF_FRAME_6 = 0xffc6;

  /**
   * Start-of-frame 7 marker (differential lossless sequential).
   */
  public static final int MARKER_START_OF_FRAME_7 = 0xffc7;

  /**
   * Start-of-frame 9 marker (extended sequential DCT).
   */
  public static final int MARKER_START_OF_FRAME_9 = 0xffc9;

  /**
   * Start-of-frame 10 marker (progressive DCT).
   */
  public static final int MARKER_START_OF_FRAME_10 = 0xffca;

  /**
   * Start-of-frame 11 marker (lossless sequential).
   */
  public static final int MARKER_START_OF_FRAME_11 = 0xffcb;

  /**
   * Start-of-frame 13 marker (differential sequential DCT).
   */
  public static final int MARKER_START_OF_FRAME_13 = 0xffcd;

  /**
   * Start-of-frame 14 marker (differential progressive DCT).
   */
  public static final int MARKER_START_OF_FRAME_14 = 0xffce;

  /**
   * Start-of-frame 15 marker (differential lossless sequential).
   */
  public static final int MARKER_START_OF_FRAME_15 = 0xffcf;

  /**
   * Signature of JPEG files.
   */
  public static final int MARKER_START_OF_IMAGE = 0xffd8;

  /**
   * End-of-stream marker.
   */
  public static final int MARKER_END_OF_IMAGE = 0xffd9;

  /**
   * Start-of-scan marker.
   */
  public static final int MARKER_START_OF_SCAN = 0xffda;

  /**
   * Mask value that identifies a valid marker.
   */
  public static final int MARKER_MASK = 0xff00;

  /**
   * Marker with tables to be used for Huffman entropy coding.
   */
  public static final int MARKER_DEFINE_HUFFMAN_TABLES = 0xffdb;

  private Constants()
  {
    // avoid instantiation of this helper class
  }
}
