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

/**
 * Decode Huffman-encoded symbols as found in a JPEG stream.
 */
public class JpegHuffmanDecoder
{
  private final JpegHuffmanTable table;
  private final int[] huffCode;
  private final int[] huffVal;
  private final int[] minCode;
  private final int[] maxCode;
  private final int[] valPtr;
  private int prev;

  public JpegHuffmanDecoder(final JpegHuffmanTable table)
  {
    this.table = table;
    huffCode = table.getHuffCode();
    huffVal = new int[huffCode.length];
    initHuffVal();
    minCode = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    maxCode = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    valPtr = new int[Constants.MAX_HUFFMAN_CODE_LENGTH];
    initDecoderTables();
    prev = 0;
  }

  private void initHuffVal()
  {
    int huffValIndex = 0;
    for (int bitLength = 1; bitLength <= Constants.MAX_HUFFMAN_CODE_LENGTH; bitLength++)
    {
      int numCodes = table.getNumCodes(bitLength);
      int index = 0;
      while (numCodes > 0)
      {
        huffVal[huffValIndex++] = table.getCode(bitLength, index++);
        numCodes--;
      }
    }
  }

  /**
   * ITU-T81.pdf p. 108 ("Decoder_tables").
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

  public void decodeDataUnit(final int... zz)
  {
    zz[0] = decodeDc();
    decodeAc(zz);
  }

  private void decodeAc(final int... zz)
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
   */
  private int decodeDc()
  {
    final int tt = decode();
    int diff = receive(tt);
    diff = extend(diff, tt);
    final int result = prev + diff;
    prev = result;
    return result;
  }

  /**
   * Figure F.12, p. 105. F.2.2.1, p. 104.
   */
  private int extend(final int value, final int tt)
  {
    return value + tt;
  }

  /**
   * F.2.2.4, p. 110.
   */
  private int receive(final int tt)
  {
    return tt;
  }

  /**
   * F.2.2.3, p. 107. Figure F.16, p. 110.
   */
  private int decode()
  {
    return 0;
  }

  public void decodeAll()
  {
    final int rr = huffCode[0] + huffVal[0] + maxCode[0] + minCode[0] + valPtr[0];
    prev = rr + 1;
  }
}
