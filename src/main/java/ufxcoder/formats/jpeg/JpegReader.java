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

import ufxcoder.io.Segment;

/**
 * Read information from JPEG input streams.
 */
public class JpegReader
{
  private final JpegProcessor proc;

  public JpegReader(final JpegProcessor processor)
  {
    proc = processor;
  }

  public void parseMarker(final Marker marker)
  {
    final int id = marker.getId();
    switch (id)
    {
    case Constants.MARKER_START_OF_FRAME_0:
    case Constants.MARKER_START_OF_FRAME_1:
    case Constants.MARKER_START_OF_FRAME_2:
    case Constants.MARKER_START_OF_FRAME_3:
    case Constants.MARKER_START_OF_FRAME_5:
    case Constants.MARKER_START_OF_FRAME_6:
    case Constants.MARKER_START_OF_FRAME_7:
    case Constants.MARKER_START_OF_FRAME_9:
    case Constants.MARKER_START_OF_FRAME_10:
    case Constants.MARKER_START_OF_FRAME_11:
    case Constants.MARKER_START_OF_FRAME_13:
    case Constants.MARKER_START_OF_FRAME_14:
    case Constants.MARKER_START_OF_FRAME_15:
      new JpegFrameReader(proc).parseStartOfFrame(marker);
      break;
    case Constants.MARKER_START_OF_SCAN:
      new JpegScanReader(proc).readScan(marker);
      break;
    case Constants.MARKER_DEFINE_HUFFMAN_TABLES:
      parseHuffmanTable(marker);
      break;
    default:
      break;
    }
  }

  private void parseHuffmanTable(final Marker marker)
  {
    final Segment segment = marker.getSegment();
    for (int numBits = 1; numBits <= 16; numBits++)
    {
      segment.int8();
    }
  }
}
