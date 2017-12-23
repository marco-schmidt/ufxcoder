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

  /**
   * Invalid number of bits per sample for lossless frames.
   */
  public static final String INVALID_SAMPLE_PRECISION_LOSSLESS = "jpeg.error.invalid_sample_precision_lossless";

  /**
   * Invalid number of bits per sample for extended frames.
   */
  public static final String INVALID_SAMPLE_PRECISION_EXTENDED = "jpeg.error.invalid_sample_precision_extended";

  /**
   * Invalid number of bits per sample for progressive frames.
   */
  public static final String INVALID_SAMPLE_PRECISION_PROGRESSIVE = "jpeg.error.invalid_sample_precision_progressive";

  /**
   * Invalid number of bits per sample for baseline frames.
   */
  public static final String INVALID_SAMPLE_PRECISION_BASELINE = "jpeg.error.invalid_sample_precision_baseline";

  /**
   * Cannot have more than one frame definition in a stream.
   */
  public static final String MULTIPLE_FRAMES = "jpeg.error.multiple_frames";

  /**
   * Start-of-frame width must not be zero.
   */
  public static final String WIDTH_ZERO = "jpeg.error.width_zero";

  /**
   * Start-of-frame must have at least one component.
   */
  public static final String AT_LEAST_ONE_COMPONENT = "jpeg.error.at_least_one_component";

  /**
   * Progressive start-of-frame must have at most four components.
   */
  public static final String INVALID_PROGRESSIVE_TOO_MANY_COMPONENTS = "jpeg.error.progressive_invalid_number_of_component";

  /**
   * Invalid frame length.
   */
  public static final String INVALID_FRAME_LENGTH = "jpeg.error.invalid_frame_length";

  /**
   * Frame length is too small. Special case of {@link #INVALID_FRAME_LENGTH}.
   */
  public static final String FRAME_LENGTH_TOO_SMALL = "jpeg.error.frame_length_too_small";

  /**
   * Horizontal component sampling factor is not in valid range.
   */
  public static final String INVALID_HORIZONTAL_COMPONENT_SAMPLING_FACTOR = "jpeg.error.invalid_horizontal_component_sampling_factor";

  /**
   * Vertical component sampling factor is not in valid range.
   */
  public static final String INVALID_VERTICAL_COMPONENT_SAMPLING_FACTOR = "jpeg.error.invalid_vertical_component_sampling_factor";

  /**
   * Invalid quantization table destination selector.
   */
  public static final String INVALID_QUANTIZATION_TABLE = "jpeg.error.invalid_quantization_table";

  /**
   * Vertical component sampling factor is not in valid range.
   */
  public static final String DUPLICATE_FRAME_COMPONENT = "jpeg.error.duplicate_frame_component";

  /**
   * Invalid number of scan components.
   */
  public static final String INVALID_NUMBER_OF_SCAN_COMPONENTS = "jpeg.error.invalid_number_of_scan_components";

  /**
   * Scan appears before frame in stream.
   */
  public static final String SCAN_BEFORE_FRAME = "jpeg.error.scan_before_frame";

  /**
   * Scan header size differs from expected size.
   */
  public static final String INVALID_SCAN_MARKER_LENGTH = "jpeg.error.invalid_scan_marker_length";

  /**
   * Scan component id was not defined in frame.
   */
  public static final String SCAN_COMPONENT_DEFINED_MORE_THAN_ONCE = "jpeg.error.scan_component_twice";

  /**
   * Scan component id is used more than once in frame.
   */
  public static final String SCAN_COMPONENT_UNDEFINED_IN_FRAME = "jpeg.error.scan_component_undefined";

  /**
   * Found restart marker differing from the expected one in input.
   */
  public static final String UNEXPECTED_RESTART_MARKER = "jpeg.error.unexpected_restart_marker";

  /**
   * Found a marker different from EOI and RST0-7 within scan data.
   */
  public static final String UNKNOWN_MARKER_IN_SCAN_DATA = "jpeg.error.unknown_marker_in_scan";

  /**
   * Input's end was reached prematurely.
   */
  public static final String UNEXPECTED_END_OF_INPUT = "jpeg.error.unexpected_end_of_input";

  private Msg()
  {
  }
}
