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
 * A single Huffman table as defined in a DHT marker.
 */
public class JpegHuffmanTable
{
  private final int[][] codes = new int[Constants.MAX_HUFFMAN_CODE_LENGTH][];
  private int id;
  private int tableClass;
  private int[] sizes;
  private int[] huffCode;

  public int getId()
  {
    return id;
  }

  public void setId(final int id)
  {
    this.id = id;
  }

  public int getTableClass()
  {
    return tableClass;
  }

  public void setTableClass(final int tc)
  {
    this.tableClass = tc;
  }

  public int getNumCodes(final int index)
  {
    final int[] array = codes[index];
    return array == null ? 0 : array.length;
  }

  public void setNumCodes(final int index, final int length)
  {
    codes[index] = new int[length];
  }

  public void setCode(final int lengthIndex, final int index, final int value)
  {
    codes[lengthIndex][index] = value;
  }

  public int[] getSizes()
  {
    return Array.clone(sizes);
  }

  public void setSizes(final int... sizes)
  {
    this.sizes = Array.clone(sizes);
  }

  public int[] getHuffCode()
  {
    return Array.clone(huffCode);
  }

  public void setHuffCode(final int... huffCode)
  {
    this.huffCode = Array.clone(huffCode);
  }
}
