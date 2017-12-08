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

/**
 * Message keys for the TIFF package.
 */
public final class Msg
{
  /**
   * Header does not contain one of the two supported byte order signatures {@link Constants#getSignatureIntel()} and
   * {@link Constants#getSignatureMotorola()}.
   */
  public static final String INVALID_BYTE_ORDER = "tiff.error.invalid_byte_order";

  /**
   * Header does not contain one of the two supported versions {@link Constants#MAGIC_TIFF} and
   * {@link Constants#MAGIC_BIG_TIFF}.
   */
  public static final String INVALID_VERSION = "tiff.error.invalid_version";

  /**
   * There was an I/O error trying to read the global header.
   */
  public static final String CANNOT_READ_GLOBAL_HEADER = "tiff.error.cannot_read_global_header";

  /**
   * There was an I/O error trying to read an offset value.
   */
  public static final String CANNOT_READ_OFFSET = "tiff.error.cannot_read_offset";

  /**
   * There was an I/O error trying to read an offset value.
   */
  public static final String INVALID_FIELD_TYPE = "tiff.error.invalid_field_type";

  /**
   * Data offsets must not be odd.
   */
  public static final String ODD_FILE_OFFSET = "tiff.error.validation.odd_image_file_directory_offset";

  /**
   * Combination of file offset and size invalid.
   */
  public static final String INVALID_OFFSET_AND_SIZE = "tiff.error.file_offset_and_size";

  /**
   * Big TIFF offsets must be eight bytes large.
   */
  public static final String INVALID_BIG_TIFF_OFFSET_SIZE = "tiff.error.invalid_big_tiff_offset_size";

  /**
   * Big TIFF field after offset size must contain value zero.
   */
  public static final String INVALID_BIG_TIFF_OFFSET_ZERO = "tiff.error.invalid_big_tiff_offset_zero";

  private Msg()
  {
  }
}
