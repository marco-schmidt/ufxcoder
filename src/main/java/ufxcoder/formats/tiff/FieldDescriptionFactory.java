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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Provide Tiff {@link FieldDescription} objects.
 */
public final class FieldDescriptionFactory
{
  /**
   * Horizontal size of image in pixels.
   */
  public static final FieldDescription IMAGE_WIDTH = new FieldDescription(256,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), 1, null, 1, 1, null, false);
  /**
   * Vertical size of image in pixel rows (also known as scanlines).
   */
  public static final FieldDescription IMAGE_LENGTH = new FieldDescription(257,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), 1, null, 1, 1, null, false);
  /**
   * Data compression of image data.
   */
  private static final FieldDescription COMPRESSION = new FieldDescription(259,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), null, null, 1, 1, Integer.valueOf(1), false);
  /**
   * How is image data to be interpreted?
   */
  public static final FieldDescription PHOTOMETRIC_INTERPRETATION = new FieldDescription(262,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), 0, 4, 1, 1, null, true);
  /**
   * File positions, one for each strip.
   */
  public static final FieldDescription STRIP_OFFSETS = new FieldDescription(273,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(8), null, 1, Integer.MAX_VALUE, null, false);
  /**
   * Number of pixel components.
   */
  public static final FieldDescription SAMPLES_PER_PIXEL = new FieldDescription(277,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), Long.valueOf(1), null, 1, 1, null, false);
  /**
   * Number of pixel rows per strip, with the possible exception of the last one, which only contains the remaining
   * number of rows.
   */
  public static final FieldDescription ROWS_PER_STRIP = new FieldDescription(278,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(1), null, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, false);
  /**
   * Number of bytes used to store each strip's data, after compression.
   */
  public static final FieldDescription STRIP_BYTE_COUNTS = new FieldDescription(279,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(1), null, 1, Integer.MAX_VALUE, null, false);
  /**
   * Date and time of creation.
   */
  public static final FieldDescription DATE_TIME = new FieldDescription(306,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Char
      })), null, null, 20, 20, null, false);
  /**
   * Function to be applied to image data before compression.
   */
  public static final FieldDescription PREDICTOR = new FieldDescription(317,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), Integer.valueOf(1), Integer.valueOf(3), 1, 1, 1, false);
  /**
   * Width of a tile.
   */
  public static final FieldDescription TILE_WIDTH = new FieldDescription(322,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(16), Long.valueOf(1), 1, Integer.MAX_VALUE, null, false);
  /**
   * Height of a tile.
   */
  public static final FieldDescription TILE_LENGTH = new FieldDescription(323,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(16), Long.valueOf(1), 1, Integer.MAX_VALUE, null, false);
  /**
   * File positions, one for each tile.
   */
  public static final FieldDescription TILE_OFFSETS = new FieldDescription(324,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Long
      })), Long.valueOf(8), null, 1, Integer.MAX_VALUE, null, false);
  /**
   * Number of bytes used to store each tile's data, after compression.
   */
  public static final FieldDescription TILE_BYTE_COUNTS = new FieldDescription(325,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short, FieldType.Long
      })), Long.valueOf(1), null, 1, Integer.MAX_VALUE, null, false);

  /**
   * Offsets to sub image file directories.
   */
  public static final FieldDescription SUB_IMAGE_FILE_DIRECTORIES = new FieldDescription(330,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Long, FieldType.IfdOffset
      })), 8, null, 1, Integer.MAX_VALUE, null, false);
  /**
   * Number of pixel components beyond regular image data.
   */
  public static final FieldDescription EXTRA_SAMPLES = new FieldDescription(338,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), 0, 2, 0, Integer.MAX_VALUE, null, false);
  /**
   * Offset to JPEG stream.
   */
  public static final FieldDescription JPEG_INTERCHANGE_FORMAT = new FieldDescription(513,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Long
      })), 8, null, 1, 1, null, false);
  /**
   * Size of JPEG stream.
   */
  public static final FieldDescription JPEG_INTERCHANGE_FORMAT_LENGTH = new FieldDescription(514,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Long
      })), 8, null, 1, 1, null, false);
  /**
   * XMP metadata.
   */
  public static final FieldDescription XMP = new FieldDescription(700,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Byte, FieldType.Undefined
      })), null, null, 0, Integer.MAX_VALUE, null, false);
  /**
   * Offset to GPS image file directory.
   */
  public static final FieldDescription GPS = new FieldDescription(34853,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Long, FieldType.IfdOffset
      })), 8, null, 1, Integer.MAX_VALUE, null, false);
  /**
   * Date and time of creation of original image.
   */
  public static final FieldDescription DATE_TIME_ORIGINAL = new FieldDescription(36867,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Char
      })), null, null, 20, 20, null, false);

  /**
   * Date and time when image was digitized (e.g. scanned).
   */
  public static final FieldDescription DATE_TIME_DIGITIZED = new FieldDescription(36868,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Char
      })), null, null, 20, 20, null, false);

  /**
   * DNG version (four numeric parts).
   */
  public static final FieldDescription DNG_VERSION = new FieldDescription(50706,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Byte
      })), null, null, 4, 4, null, false);
  /**
   * CR2 slice information (three short values).
   */
  public static final FieldDescription CR2_SLICE_INFORMATION = new FieldDescription(50752,
      new HashSet<FieldType>(Arrays.asList(new FieldType[]
      {
          FieldType.Short
      })), null, null, 3, 3, null, false);
  /**
   * TIFF field descriptions to be used for validation.
   */
  private static final FieldDescription[] ALL =
  {
      IMAGE_WIDTH, IMAGE_LENGTH, COMPRESSION, PHOTOMETRIC_INTERPRETATION, STRIP_OFFSETS, ROWS_PER_STRIP,
      STRIP_BYTE_COUNTS, DATE_TIME, PREDICTOR, TILE_WIDTH, TILE_LENGTH, TILE_OFFSETS, TILE_BYTE_COUNTS,
      SUB_IMAGE_FILE_DIRECTORIES, GPS, DATE_TIME_ORIGINAL, DATE_TIME_DIGITIZED, DNG_VERSION, CR2_SLICE_INFORMATION
  };

  private FieldDescriptionFactory()
  {
  }

  public static List<FieldDescription> getDescriptions()
  {
    return Arrays.asList(ALL);
  }
}
