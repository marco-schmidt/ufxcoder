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
 * Message keys for the JPEG package.
 */
public final class Msg
{
  /**
   * There was an I/O error trying to read a header.
   */
  public static final String CANNOT_READ_HEADER = "jpeg.error.cannot_read_header";

  /**
   * Marker is invalid.
   */
  public static final String INVALID_MARKER = "jpeg.error.invalid_marker";

  /**
   * First marker is not start-of-image.
   */
  public static final String FIRST_MARKER_NOT_SOI = "jpeg.error.first_marker_not_soi";

  /**
   * Start-of-image marker must only appear as first marker.
   */
  public static final String SOI_FIRST_MARKER_ONLY = "jpeg.error.soi_first_marker_only";

  /**
   * General error when reading from input stream.
   */
  public static final String READING_ERROR = "jpeg.error.reading_error";

  /**
   * There is data after the last marker.
   */
  public static final String EXTRANEOUS_DATA_AFTER_END_OF_STREAM = "jpeg.error.extraneous_data_after_end_of_stream";

  private Msg()
  {
  }
}
