/*
 * Copyright 2017, 2018, 2019, 2020, 2021 the original author or authors.
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import ufxcoder.io.BufferedSource;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Read and process a JPEG scan.
 */
public class JpegScanReader
{
  private final JpegProcessor proc;
  private final BufferedSource input;
  private int expectedRestart;
  private int bitMask;
  private int byteValue;
  private int dcPredictor;

  public JpegScanReader(final JpegProcessor processor)
  {
    proc = processor;
    final SeekableSource source = proc.getSource();
    input = new BufferedSource(source, 4096);
    expectedRestart = Constants.MARKER_MIN_RESTART_INTERVAL;
  }

  public void readScan(final Marker marker)
  {
    final JpegFrame frame = proc.getJpegFileDescription().getFrame();
    if (frame == null)
    {
      proc.error(Msg.SCAN_BEFORE_FRAME);
    }
    final Segment segment = marker.getSegment();
    final int numComponents = segment.int8();
    if (numComponents < Constants.MIN_SCAN_COMPONENTS || numComponents > Constants.MAX_SCAN_COMPONENTS)
    {
      proc.error(Msg.INVALID_NUMBER_OF_SCAN_COMPONENTS, numComponents, Constants.MIN_SCAN_COMPONENTS,
          Constants.MAX_SCAN_COMPONENTS);
    }
    else
    {
      final JpegScan scan = new JpegScan();
      if (frame != null)
      {
        frame.add(scan);
      }
      scan.setNumComponents(numComponents);
      // check marker length
      final int expectedLength = 2 + 1 + numComponents * 2 + 3;
      if (marker.getLength() == expectedLength)
      {
        readScanHeader(segment, frame, scan);
        if (proc.isSuccess())
        {
          readScanData();
          // final JpegBaselineHuffmanDecoder dec = new JpegBaselineHuffmanDecoder(proc, this);
          // dec.decode();
        }
      }
      else
      {
        proc.error(Msg.INVALID_SCAN_MARKER_LENGTH, numComponents, expectedLength, marker.getLength());
      }
    }
  }

  /**
   * Return a single bit from input. ITU-T81.pdf F2.2.5, p. 110. Figure F.18, p. 111.
   *
   * @return bit value, either 0 or 1
   */
  public int nextBit()
  {
    if (bitMask == 0)
    {
      byteValue = nextByte();
      bitMask = 128;
    }
    final int result = (byteValue & bitMask) == 0 ? 0 : 1;
    bitMask >>= 1;
    return result;
  }

  /**
   * Read the next byte from input, handling read errors, escaped 0xff values, restart markers, non-restart markers.
   *
   * @return payload byte or -1 if input has ended or errors have occurred
   */
  public int nextByte()
  {
    int result;
    try
    {
      result = input.next();
      if (result == 0xff)
      {
        result = handleFf();
      }
      else
      {
        if (result == -1)
        {
          proc.error(Msg.UNEXPECTED_END_OF_INPUT);
        }
      }
    }
    catch (IOException e)
    {
      proc.error(Msg.READING_ERROR, e.getMessage());
      result = -1;
    }
    return result;
  }

  private int handleFf() throws IOException
  {
    int result;
    final int second = input.next();
    if (second == 0)
    {
      // 0xff 0x00 => 0xff
      result = 0xff;
    }
    else
    {
      // nothing after 0xff => truncated input
      if (second == -1)
      {
        proc.error(Msg.UNEXPECTED_END_OF_INPUT);
        result = -1;
      }
      else
      {
        // this is a marker
        final int id = Constants.MARKER_MASK | second;
        if (id >= Constants.MARKER_MIN_RESTART_INTERVAL && id <= Constants.MARKER_MAX_RESTART_INTERVAL)
        {
          // restart interval
          if (id == expectedRestart)
          {
            expectedRestart = id == Constants.MARKER_MAX_RESTART_INTERVAL ? Constants.MARKER_MIN_RESTART_INTERVAL
                : id + 1;
            result = nextByte();
          }
          else
          {
            proc.error(Msg.UNEXPECTED_RESTART_MARKER, expectedRestart, id);
            result = -1;
          }
        }
        else
        {
          // a marker other than restart:
          // scan is over, position underlying input two bytes back
          input.seekBack(2);
          result = -1;
        }
      }
    }
    return result;
  }

  public void readScanData()
  {
    int result;
    do
    {
      result = nextByte();
    }
    while (result != -1);
  }

  public void readScanDataLegacy()
  {
    while (true)
    {
      try
      {
        final int value = input.next();
        if (value == 0xff)
        {
          final int second = input.next();
          if (second != 0)
          {
            if (second == -1)
            {
              proc.error(Msg.UNEXPECTED_END_OF_INPUT);
              break;
            }
            final int id = Constants.MARKER_MASK | second;
            if (id >= Constants.MARKER_MIN_RESTART_INTERVAL && id <= Constants.MARKER_MAX_RESTART_INTERVAL)
            {
              // restart interval
              if (id == expectedRestart)
              {
                expectedRestart = id == Constants.MARKER_MAX_RESTART_INTERVAL ? Constants.MARKER_MIN_RESTART_INTERVAL
                    : id + 1;
              }
              else
              {
                proc.error(Msg.UNEXPECTED_RESTART_MARKER, expectedRestart, id);
              }
            }
            else
            {
              // scan is over, position underlying input two bytes back
              input.seekBack(2);
              break;
            }
          }
        }
        else
        {
          if (value == -1)
          {
            proc.error(Msg.UNEXPECTED_END_OF_INPUT);
            break;
          }
        }
      }
      catch (IOException e)
      {
        proc.error(Msg.READING_ERROR, e.getMessage());
      }
    }
  }

  private void readScanHeader(final Segment segment, final JpegFrame frame, final JpegScan scan)
  {
    final Set<Integer> componentIds = new HashSet<>();
    for (int i = 0; i < scan.getNumComponents(); i++)
    {
      final int compId = segment.int8();
      if (frame.findComponent(compId) == null)
      {
        proc.error(Msg.SCAN_COMPONENT_UNDEFINED_IN_FRAME, compId);
      }
      if (componentIds.contains(Integer.valueOf(compId)))
      {
        proc.error(Msg.SCAN_COMPONENT_DEFINED_MORE_THAN_ONCE, compId);
      }
      else
      {
        componentIds.add(compId);
      }
      final int acdc = segment.int8();

      final JpegScanComponent comp = new JpegScanComponent();
      comp.setAc(acdc >> 4);
      comp.setDc(acdc & 0x0f);
      comp.setId(compId);

      if (proc.isSuccess())
      {
        scan.add(comp);
      }
    }
    scan.setStartSpectral(segment.int8());
    scan.setEndSpectral(segment.int8());
    final int approx = segment.int8();
    scan.setApproxHigh(approx >> 4);
    scan.setApproxLow(approx & 0x0f);
  }

  public int getDcPredictor()
  {
    return dcPredictor;
  }

  public void setDcPredictor(final int dcPredictor)
  {
    this.dcPredictor = dcPredictor;
  }
}
