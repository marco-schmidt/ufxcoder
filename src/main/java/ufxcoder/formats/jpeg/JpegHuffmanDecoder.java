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

/**
 * Decode Huffman-encoded symbols as found in a JPEG stream.
 */
public class JpegHuffmanDecoder
{
  private final JpegHuffmanTable table;
  private final JpegScanReader reader;
  private final int[] huffCode;
  private final int[] huffVal;
  private final int[] minCode;
  private final int[] maxCode;
  private final int[] valPtr;

  public JpegHuffmanDecoder(final JpegHuffmanTable table, final JpegScanReader reader)
  {
    this.table = table;
    this.reader = reader;
    generateTableSize(table);
    generateCodeTable(table);
    huffCode = table.getHuffCode();
    huffVal = new int[huffCode.length];
    initHuffVal();
    minCode = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    maxCode = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    valPtr = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    initDecoderTables();
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
    table.setHuffSize(Array.clone(huffSize, 0, huffSizeIndex, 0));
  }

  /**
   * Generate an array with the actual codes. ITU-T81.pdf p. 52.
   */
  private void generateCodeTable(final JpegHuffmanTable table)
  {
    final int[] huffSize = table.getHuffSize();
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

  private void initHuffVal()
  {
    int huffValIndex = 0;
    for (int bitLength = 1; bitLength <= Constants.MAX_HUFFMAN_CODE_LENGTH; bitLength++)
    {
      int numCodes = table.getNumCodes(bitLength - 1);
      int index = 0;
      while (numCodes > 0)
      {
        huffVal[huffValIndex++] = table.getCode(bitLength - 1, index++);
        numCodes--;
      }
    }
  }

  /**
   * Initialize arrays {@link #maxCode}, {@link #minCode} and {@link #valPtr}. ITU-T81.pdf p. 108 ("Decoder_tables").
   */
  private void initDecoderTables()
  {
    int bitLength = 0; // "i" in document
    int codeIndex = 0; // "j" in document
    while (true)
    {
      bitLength++;
      if (bitLength > Constants.MAX_HUFFMAN_CODE_LENGTH)
      {
        break;
      }
      final int numCodes = table.getNumCodes(bitLength - 1);
      if (numCodes == 0)
      {
        maxCode[bitLength - 1] = -1;
      }
      else
      {
        valPtr[bitLength - 1] = codeIndex;
        minCode[bitLength - 1] = huffCode[codeIndex];
        codeIndex = codeIndex + numCodes - 1;
        maxCode[bitLength - 1] = huffCode[codeIndex];
        codeIndex++;
      }
    }
  }

  /**
   * Decode AC coefficients and put them into argument array.
   *
   * @param zz
   *          array with at least 64 entries, elements with index 1 to 63 will be changed in this method
   */
  public void decodeAc(final int... zz)
  {
    for (int index = 1; index < 8 * 8; index++)
    {
      zz[index] = 0;
      if (zz.length >= index)
      {
        break;
      }
    }
  }

  /**
   * F.2.2.1, p. 104.
   *
   * @return decoded DC coefficient
   */
  public int decodeDc()
  {
    final int tt = decode();
    int diff = receive(tt);
    diff = extend(diff, tt);
    return diff;
  }

  /**
   * Figure F.12, p. 105. F.2.2.1, p. 104.
   */
  private int extend(final int value, final int tt)
  {
    return value + tt;
  }

  /**
   * Return a number of bits from input. ITU-T81.pdf F.2.2.4, p. 110. Figure F.17, p. 110.
   */
  private int receive(final int numBits)
  {
    int result = 0;
    int bitsLeft = numBits;
    while (bitsLeft != 0)
    {
      result = (result << 1) | reader.nextBit();
      bitsLeft--;
    }
    return result;
  }

  /**
   * F.2.2.3, p. 107. Figure F.16, p. 110.
   */
  private int decode()
  {
    int bitLength = 1; // "i" in document
    int code = reader.nextBit();
    while (code < maxCode[bitLength - 1])
    {
      bitLength++;
      code = (code << 1) | reader.nextBit();
    }
    int valueIndex = valPtr[bitLength - 1]; // "j" in document
    valueIndex = valueIndex + code - minCode[bitLength - 1];
    return huffVal[valueIndex];
  }
}
