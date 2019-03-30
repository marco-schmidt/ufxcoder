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
package ufxcoder.io;

import java.math.BigInteger;
import ufxcoder.conversion.Array;
import ufxcoder.conversion.ByteOrder;

/**
 * A chunk of raw data and the offset into a source from which it was read, plus several helper methods to interpret
 * that raw data.
 */
public class Segment
{
  private byte[] data;
  private int length;
  private long offset;
  private int index;
  private ByteOrder byteOrder;

  public int getLength()
  {
    return length;
  }

  public void setLength(final int length)
  {
    this.length = length;
  }

  public long getOffset()
  {
    return offset;
  }

  public void setOffset(final long offset)
  {
    this.offset = offset;
  }

  public byte[] getData()
  {
    return Array.clone(data);
  }

  public byte[] getData(final int numBytes)
  {
    final byte[] result = Array.clone(data, index, numBytes, 0);
    index += numBytes;
    return result;
  }

  public byte[] getData(final int offset, final int numBytes)
  {
    return Array.clone(data, offset, numBytes, 0);
  }

  public void setData(final byte[] data)
  {
    this.data = Array.clone(data);
  }

  public boolean equals(final int internalOffset, final byte[] array, final int arrayOffset, final int numBytes)
  {
    return false;
  }

  public boolean equalsAt(final int index, final byte[] pattern)
  {
    boolean result = false;
    if (pattern != null && data != null && index + pattern.length <= data.length)
    {
      result = true;
      int sourceIndex = index;
      for (int destIndex = 0; destIndex < pattern.length; sourceIndex++, destIndex++)
      {
        if (data[sourceIndex] != pattern[destIndex])
        {
          result = false;
          break;
        }
      }
    }
    return result;
  }

  public int int8()
  {
    int result;
    if (data != null && index >= 0 && index < data.length)
    {
      result = data[index++] & 0xff;
    }
    else
    {
      result = -1;
    }
    return result;
  }

  public int int16()
  {
    final int result = Array.from16(data, index, byteOrder);
    index += 2;
    return result;
  }

  public int int32()
  {
    final int result = Array.from32(data, index, byteOrder);
    index += 4;
    return result;
  }

  public long int64()
  {
    final long result = Array.from64(data, index, byteOrder);
    index += 8;
    return result;
  }

  public BigInteger bigInt(final int numBytes)
  {
    return Array.toBigInteger(getData(numBytes), byteOrder);
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(final int index)
  {
    this.index = index;
  }

  public ByteOrder getByteOrder()
  {
    return byteOrder;
  }

  public void setByteOrder(final ByteOrder byteOrder)
  {
    this.byteOrder = byteOrder;
  }

  public int getNumBytesLeft()
  {
    int result;
    if (data == null || index > length)
    {
      result = 0;
    }
    else
    {
      result = length - index;
    }
    return result;
  }

  public boolean hasBytes(final int num)
  {
    return num <= getNumBytesLeft();
  }
}
