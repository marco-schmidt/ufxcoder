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
package ufxcoder.formats.tiff;

import ufxcoder.conversion.Array;

/**
 * Various literal values used in TIFF.
 */
public final class Constants
{
  /**
   * Signature of little-endian TIFF files.
   */
  private static final byte[] MAGIC_INTEL =
  {
      0x49, 0x49
  };

  /**
   * Signature of big-endian TIFF files.
   */
  private static final byte[] MAGIC_MOTOROLA =
  {
      0x4d, 0x4d
  };

  /**
   * Identifying version number of standard TIFF files.
   */
  public static final int MAGIC_TIFF = 42;

  /**
   * Identifying version number of TIFF files following the BigTIFF extension.
   */
  public static final int MAGIC_BIG_TIFF = 43;

  /**
   * Minimum number of tags per image file directory.
   */
  public static final int MIN_TAGS_PER_IMAGE_FILE_DIRECTORY = 1;

  /**
   * Size of an image file directory entry of BigTIFF files.
   */
  public static final int TAG_SIZE_BIG = 20;

  /**
   * Size of an image file directory entry of standard TIFF files.
   */
  public static final int TAG_SIZE_REGULAR = 12;

  /**
   * Size of an offset value in bytes of a BigTIFF file.
   */
  public static final int OFFSET_SIZE_BIG = 8;

  /**
   * Size of an offset value in bytes of a regular TIFF file.
   */
  public static final int OFFSET_SIZE_REGULAR = 4;

  /**
   * Number of image file directories in a CR2 file.
   */
  public static final int CR2_IMAGE_FILE_DIRECTORIES = 4;

  /**
   * Color type grayscale/bilevel, white to black from zero to maximum value.
   */
  public static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;

  /**
   * Color type grayscale/bilevel, black to white from zero to maximum value.
   */
  public static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;

  /**
   * Color type RGB (red, green, blue).
   */
  public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;

  /**
   * Color type paletted image (one channel, each value an index into a list of RGB colors).
   */
  public static final int PHOTOMETRIC_INTERPRETATION_PALETTED = 3;

  /**
   * Color type separated, mostly CMYK.
   */
  public static final int PHOTOMETRIC_INTERPRETATION_SEPARATED = 5;

  /**
   * Color type YCbCr, luminance and two color channels.
   */
  public static final int PHOTOMETRIC_INTERPRETATION_Y_CB_CR = 6;

  private Constants()
  {
    // avoid instantiation of this helper class
  }

  public static byte[] getSignatureIntel()
  {
    return Array.clone(MAGIC_INTEL);
  }

  public static byte[] getSignatureMotorola()
  {
    return Array.clone(MAGIC_MOTOROLA);
  }
}
