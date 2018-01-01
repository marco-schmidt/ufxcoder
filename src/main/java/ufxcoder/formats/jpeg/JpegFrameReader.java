/*
 * Copyright 2017, 2018 the original author or authors.
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
 * Read frame-related information from a JPEG stream.
 */
public class JpegFrameReader
{
  private final JpegProcessor proc;

  public JpegFrameReader(final JpegProcessor proc)
  {
    this.proc = proc;
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
      parseFrameComponents(marker, frame);
    }
    else
    {
      proc.error(Msg.INVALID_FRAME_LENGTH, numComponents, expectedMarkerSize, marker.getLength());
    }
  }

  private void parseFrameComponents(final Marker marker, final JpegFrame frame)
  {
    int index = 0;
    while (index < frame.getNumComponents())
    {
      final JpegFrameComponent comp = parseFrameComponent(marker, frame);
      final int id = comp.getId();
      if (frame.findComponent(id) == null)
      {
        frame.add(comp);
      }
      else
      {
        proc.error(Msg.DUPLICATE_FRAME_COMPONENT, id);
      }
      index++;
    }
  }

  private JpegFrameComponent parseFrameComponent(final Marker marker, final JpegFrame frame)
  {
    final Segment segment = marker.getSegment();

    // read information
    final int id = segment.int8();
    final int samplingFactor = segment.int8();
    final int horiz = samplingFactor >> 4;
    final int vert = samplingFactor & 0x0f;
    final int quantId = segment.int8();

    // check information
    if (horiz < Constants.MIN_COMPONENT_SAMPLING_FACTOR || horiz > Constants.MAX_COMPONENT_SAMPLING_FACTOR)
    {
      proc.error(Msg.INVALID_HORIZONTAL_COMPONENT_SAMPLING_FACTOR, horiz, Constants.MIN_COMPONENT_SAMPLING_FACTOR,
          Constants.MAX_COMPONENT_SAMPLING_FACTOR);
    }
    if (vert < Constants.MIN_COMPONENT_SAMPLING_FACTOR || vert > Constants.MAX_COMPONENT_SAMPLING_FACTOR)
    {
      proc.error(Msg.INVALID_VERTICAL_COMPONENT_SAMPLING_FACTOR, horiz, Constants.MIN_COMPONENT_SAMPLING_FACTOR,
          Constants.MAX_COMPONENT_SAMPLING_FACTOR);
    }
    final int maxQuant = frame.isLossless() ? 0 : 3;
    if (quantId > maxQuant)
    {
      proc.error(Msg.INVALID_QUANTIZATION_TABLE, quantId);
    }

    // create new component object from data
    final JpegFrameComponent comp = new JpegFrameComponent();
    comp.setId(id);
    comp.setHorizontalSamplingFactor(horiz);
    comp.setVerticalSamplingFactor(vert);
    comp.setQuantizationTableId(quantId);
    return comp;
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

}
