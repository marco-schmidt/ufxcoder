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

import ufxcoder.conversion.Array;
import ufxcoder.io.Segment;

/**
 * Read Huffman table information from input.
 */
public class JpegHuffmanReader
{
  private final JpegProcessor proc;

  public JpegHuffmanReader(final JpegProcessor proc)
  {
    this.proc = proc;
  }

  public void readTables(final Marker marker)
  {
    final Segment segment = marker.getSegment();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    final JpegFrame frame = desc.getFrame();
    while (proc.isSuccess() && segment.hasBytes(1))
    {
      final JpegHuffmanTable table = new JpegHuffmanTable();
      readTableIdClass(marker, table);
      readCodeLengths(marker, table);
      readCodes(marker, table);
      if (frame != null)
      {
        checkTable(frame, table);
      }
      if (proc.isSuccess())
      {
        generateTableSize(table);
        generateCodeTable(table);
        desc.add(table);
      }
    }
  }

  private void readCodes(final Marker marker, final JpegHuffmanTable table)
  {
    final Segment segment = marker.getSegment();
    for (int lengthIndex = 0; lengthIndex < Constants.MAX_HUFFMAN_CODE_LENGTH; lengthIndex++)
    {
      final int numCodes = table.getNumCodes(lengthIndex);
      if (segment.hasBytes(numCodes))
      {
        int index = 0;
        while (index < numCodes)
        {
          table.setCode(lengthIndex, index++, segment.int8());
        }
      }
      else
      {
        proc.error(Msg.MARKER_TOO_SMALL_FOR_HUFFMAN_CODES, numCodes, lengthIndex + 1);
      }
    }
  }

  private void readCodeLengths(final Marker marker, final JpegHuffmanTable table)
  {
    final Segment segment = marker.getSegment();
    if (segment.hasBytes(Constants.MAX_HUFFMAN_CODE_LENGTH))
    {
      for (int index = 0; index < Constants.MAX_HUFFMAN_CODE_LENGTH; index++)
      {
        table.setNumCodes(index, segment.int8());
      }
    }
    else
    {
      proc.error(Msg.MARKER_TOO_SMALL_FOR_HUFFMAN_CODE_LENGTHS);
    }
  }

  private void readTableIdClass(final Marker marker, final JpegHuffmanTable table)
  {
    final Segment segment = marker.getSegment();
    final int value = segment.int8();
    table.setId(value & 0x0f);
    table.setTableClass((value >> 4) & 0x0f);
  }

  public void checkTable(final JpegFrame frame, final JpegHuffmanTable table)
  {
    final int maxId = frame.isBaseline() ? 1 : 3;
    final int id = table.getId();
    if (id > maxId)
    {
      proc.error(Msg.INVALID_HUFFMAN_TABLE_DEST_IDENTIFIER, id, maxId);
    }
    final int tableClass = table.getTableClass();
    final int maxClass = frame.isLossless() ? 0 : 1;
    if (tableClass > maxClass)
    {
      proc.error(Msg.INVALID_HUFFMAN_TABLE_CLASS, tableClass, maxClass);
    }
  }

  /**
   * Generate an array with the length of each code. ITU-T81.pdf p. 50f.
   */
  private void generateTableSize(final JpegHuffmanTable table)
  {
    // initial size is the maximum theoretic size if each of the sixteen bit lengths was defined as the maximum byte
    // value 255; that many codes do not make sense, but the call to clone at the bottom of this method truncates the
    // array as needed
    final int[] huffSize = new int[Constants.MAX_HUFFMAN_CODE_LENGTH * 255];
    int bitLength = 1; // "i" in document
    int indexLength = 1; // index within one bit length, "j" in document
    int huffSizeIndex = 0; // "k" in document
    do
    {
      final int numCodes = table.getNumCodes(bitLength - 1);
      while (indexLength <= numCodes)
      {
        huffSize[huffSizeIndex] = bitLength;
        indexLength++;
        huffSizeIndex++;
      }
      bitLength++;
      indexLength = 1;
    }
    while (bitLength <= Constants.MAX_HUFFMAN_CODE_LENGTH);
    table.setSizes(Array.clone(huffSize, 0, huffSizeIndex, 0));
  }

  /**
   * Generate an array with the actual codes. ITU-T81.pdf p. 52.
   */
  private void generateCodeTable(final JpegHuffmanTable table)
  {
    final int[] huffSize = table.getSizes();
    final int[] huffCode = new int[huffSize.length];
    int huffSizeIndex = 0; // "k" in document
    int code = 0;
    int si = huffSize[0];
    while (true)
    {
      do
      {
        huffCode[huffSizeIndex] = code;
        code++;
        huffSizeIndex++;
      }
      while (huffSizeIndex != huffSize.length && si == huffSize[huffSizeIndex]);
      if (huffSizeIndex == huffSize.length)
      {
        break;
      }
      do
      {
        code = code << 1;
        si++;
      }
      while (si != huffSize[huffSizeIndex]);
    }
    table.setHuffCode(huffCode);
  }
}
