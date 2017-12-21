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
      parseStartOfFrame(marker);
      break;
    case Constants.MARKER_DEFINE_HUFFMAN_TABLES:
      parseHuffmanTable(marker);
      break;
    default:
      break;
    }
  }

  protected void parseStartOfFrame(final Marker marker)
  {
    final JpegFileDescription desc = proc.getJpegFileDescription();
    if (desc.getFrame() != null)
    {
      proc.error(Msg.MULTIPLE_FRAMES);
    }
    if (marker.getLength() - 2 < Constants.MIN_FRAME_LENGTH)
    {
      proc.error(Msg.FRAME_LENGTH_TOO_SMALL, marker.getLength(), Constants.MIN_FRAME_LENGTH);
    }
    if (proc.isSuccess())
    {
      parseStartOfFrameContent(marker);
    }
  }

  protected void parseStartOfFrameContent(final Marker marker)
  {
    final JpegFrame frame = new JpegFrame();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    desc.setFrame(frame);

    // determine various properties from marker
    final int id = marker.getId();
    frame.setBaseline(id == Constants.MARKER_START_OF_FRAME_0);
    frame.setExtended(id == Constants.MARKER_START_OF_FRAME_1 || id == Constants.MARKER_START_OF_FRAME_5
        || id == Constants.MARKER_START_OF_FRAME_9 || id == Constants.MARKER_START_OF_FRAME_13);
    frame.setProgressive(id == Constants.MARKER_START_OF_FRAME_2 || id == Constants.MARKER_START_OF_FRAME_6
        || id == Constants.MARKER_START_OF_FRAME_10 || id == Constants.MARKER_START_OF_FRAME_14);
    frame.setLossless(id == Constants.MARKER_START_OF_FRAME_3 || id == Constants.MARKER_START_OF_FRAME_7
        || id == Constants.MARKER_START_OF_FRAME_11 || id == Constants.MARKER_START_OF_FRAME_15);

    // read fixed part of SOF
    final Segment segment = marker.getSegment();
    frame.setSamplePrecision(segment.int8());
    frame.setHeight(segment.int16());
    frame.setWidth(segment.int16());
    final int numComponents = segment.int8();

    // check values
    checkSamplePrecision(frame);
    if (frame.getWidth() == 0)
    {
      proc.error(Msg.WIDTH_ZERO);
    }
    frame.setNumComponents(numComponents);
    if (numComponents < 1)
    {
      proc.error(Msg.AT_LEAST_ONE_COMPONENT);
    }
    else
    {
      if (frame.isProgressive() && numComponents > Constants.PROGRESSIVE_MAX_COMPONENTS)
      {
        proc.error(Msg.INVALID_PROGRESSIVE_TOO_MANY_COMPONENTS, numComponents);
      }
    }

    // read remaining part of SOF: definition of components
    final int expectedMarkerSize = 2 + 6 + numComponents * 3;
    if (marker.getLength() == expectedMarkerSize)
    {
      parseFrameComponentDefinitions(marker, frame);
    }
    else
    {
      proc.error(Msg.INVALID_FRAME_LENGTH, numComponents, expectedMarkerSize, marker.getLength());
    }
  }

  private void parseFrameComponentDefinitions(final Marker marker, final JpegFrame frame)
  {
    final Segment segment = marker.getSegment();
    int index = 0;
    while (index < frame.getNumComponents())
    {
      segment.int8();
      segment.int8();
      segment.int8();
      index++;
    }
  }

  /**
   * Check sample precision for given frame. ITU-T T.81 page 35f, see "Table B.2 – Frame header parameter sizes and
   * values".
   *
   * @param frame
   *          check this frame's sample precision
   */
  protected void checkSamplePrecision(final JpegFrame frame)
  {
    final int samplePrecision = frame.getSamplePrecision();
    if (frame.isLossless() && (samplePrecision < 2 || samplePrecision > 16))
    {
      proc.error(Msg.INVALID_SAMPLE_PRECISION_LOSSLESS, samplePrecision);
    }
    if (frame.isExtended() && samplePrecision != 8 && samplePrecision != 12)
    {
      proc.error(Msg.INVALID_SAMPLE_PRECISION_EXTENDED, samplePrecision);
    }
    if (frame.isProgressive() && samplePrecision != 8 && samplePrecision != 12)
    {
      proc.error(Msg.INVALID_SAMPLE_PRECISION_PROGRESSIVE, samplePrecision);
    }
    if (frame.isBaseline() && samplePrecision != 8)
    {
      proc.error(Msg.INVALID_SAMPLE_PRECISION_BASELINE, samplePrecision);
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
