/*
 * Copyright 2017, 2018, 2019 the original author or authors.
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

import ufxcoder.conversion.Array;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.io.Segment;

/**
 * Read quantization table information from input.
 */
public class JpegQuantizationReader
{
  private final JpegProcessor proc;

  public JpegQuantizationReader(final JpegProcessor proc)
  {
    this.proc = proc;
  }

  public void readTables(final Marker marker)
  {
    final Segment segment = marker.getSegment();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    while (proc.isSuccess() && segment.hasBytes(1))
    {
      final JpegQuantizationTable table = new JpegQuantizationTable();
      readPrecisionId(marker, table);
      checkTable(table);
      if (proc.isSuccess())
      {
        readData(marker, table);
        if (proc.isSuccess())
        {
          desc.add(table);
        }
      }
    }
  }

  private void readData(final Marker marker, final JpegQuantizationTable table)
  {
    int numBytes;
    switch (table.getPrecision())
    {
    case Constants.QUANTIZATION_PRECISION_8_BITS:
    {
      numBytes = Constants.MINIMUM_CODED_UNIT_ELEMENTS;
      break;
    }
    case Constants.QUANTIZATION_PRECISION_16_BITS:
    {
      numBytes = Constants.MINIMUM_CODED_UNIT_ELEMENTS * 2;
      break;
    }
    default:
    {
      // error has been handled in JpegFrameReader or checkTable in this class
      numBytes = 0;
      break;
    }
    }
    if (numBytes > 0)
    {
      final Segment segment = marker.getSegment();
      final int bytesLeft = segment.getNumBytesLeft();
      if (bytesLeft >= numBytes)
      {
        initTable(table, segment.getData(numBytes));
      }
      else
      {
        proc.error(Msg.NOT_ENOUGH_DATA_FOR_QUANTIZATION_TABLE, bytesLeft, numBytes);
      }
    }
  }

  private void initTable(final JpegQuantizationTable table, final byte[] srcData)
  {
    final int[] destData = new int[Constants.MINIMUM_CODED_UNIT_ELEMENTS];
    final int incr = table.getPrecision() == Constants.QUANTIZATION_PRECISION_8_BITS ? 1 : 2;
    int destIndex = 0;
    for (int srcIndex = 0; destIndex < destData.length; srcIndex += incr, destIndex++)
    {
      int value;
      if (incr == 2)
      {
        value = Array.from16(srcData, srcIndex, ByteOrder.BigEndian);
      }
      else
      {
        value = srcData[srcIndex] & 0xff;
      }
      destData[destIndex] = value;
    }
    table.setData(destData);
  }

  private void readPrecisionId(final Marker marker, final JpegQuantizationTable table)
  {
    final Segment segment = marker.getSegment();
    final int value = segment.int8();
    table.setId(value & 0x0f);
    table.setPrecision((value >> 4) & 0x0f);
  }

  private void checkTable(final JpegQuantizationTable table)
  {
    final int id = table.getId();
    if (id > Constants.MAX_QUANTIZATION_TABLE_DEST_IDENTIFIER)
    {
      proc.error(Msg.INVALID_QUANTIZATION_TABLE_DEST_IDENTIFIER, id, Constants.MAX_QUANTIZATION_TABLE_DEST_IDENTIFIER);
    }
    final int precision = table.getPrecision();
    if (precision != Constants.QUANTIZATION_PRECISION_8_BITS && precision != Constants.QUANTIZATION_PRECISION_16_BITS)
    {
      proc.error(Msg.INVALID_QUANTIZATION_TABLE_PRECISION, precision);
    }
  }
}
