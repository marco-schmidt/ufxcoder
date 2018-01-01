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
package ufxcoder.conversion;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class with array-related operations.
 */
public final class Array
{
  private Array()
  {
    // to avoid instantiation
  }

  public static byte[] clone(final byte[] array)
  {
    return clone(array, 0);
  }

  /**
   * Create a new array that is a concatenation of the content of the array argument and enough space for a number of
   * additional bytes.
   *
   * @param array
   *          the array to copy data from
   * @param additionalBytes
   *          number of bytes that are available in the cloned version after the content of array
   * @return new array with the described characteristics
   */
  public static byte[] clone(final byte[] array, final int additionalBytes)
  {
    return array == null ? null : clone(array, 0, array.length, additionalBytes);
  }

  public static byte[] clone(final byte[] array, final int offset, final int numBytes, final int additionalBytes)
  {
    byte[] result = null;
    if (array != null && additionalBytes >= 0)
    {
      final long newArraySizeLong = (long) numBytes + (long) additionalBytes;
      if (newArraySizeLong > Integer.MAX_VALUE)
      {
        throw new IllegalArgumentException(String.format(
            "Combined size of existing array length %d and additional bytes %d goes beyond maximum array size %d.",
            array.length, additionalBytes, Integer.MAX_VALUE));
      }
      result = new byte[numBytes + additionalBytes];
      System.arraycopy(array, offset, result, 0, numBytes);
    }
    return result;
  }

  public static int from16(final byte[] buffer, final int offset, final ByteOrder order)
  {
    return order == ByteOrder.BigEndian ? from16Big(buffer, offset) : from16Little(buffer, offset);
  }

  public static int from16Big(final byte[] buffer, final int offset)
  {
    final int value1 = buffer[offset] & 0xff;
    final int value2 = buffer[offset + 1] & 0xff;
    return value1 << 8 | value2;
  }

  public static int from16Little(final byte[] buffer, final int offset)
  {
    final int value1 = buffer[offset] & 0xff;
    final int value2 = buffer[offset + 1] & 0xff;
    return value2 << 8 | value1;
  }

  public static int from32(final byte[] buffer, final int offset, final ByteOrder order)
  {
    return order == ByteOrder.BigEndian ? from32Big(buffer, offset) : from32Little(buffer, offset);
  }

  public static int from32Big(final byte[] buffer, final int offset)
  {
    final int value1 = buffer[offset] & 0xff;
    final int value2 = buffer[offset + 1] & 0xff;
    final int value3 = buffer[offset + 2] & 0xff;
    final int value4 = buffer[offset + 3] & 0xff;
    return value1 << 24 | value2 << 16 | value3 << 8 | value4;
  }

  public static int from32Little(final byte[] buffer, final int offset)
  {
    final int value1 = buffer[offset] & 0xff;
    final int value2 = buffer[offset + 1] & 0xff;
    final int value3 = buffer[offset + 2] & 0xff;
    final int value4 = buffer[offset + 3] & 0xff;
    return value4 << 24 | value3 << 16 | value2 << 8 | value1;
  }

  public static long from64(final byte[] buffer, final int offset, final ByteOrder order)
  {
    return order == ByteOrder.BigEndian ? from64Big(buffer, offset) : from64Little(buffer, offset);
  }

  public static long from64Big(final byte[] buffer, final int offset)
  {
    final long value1 = buffer[offset] & 0xff;
    final long value2 = buffer[offset + 1] & 0xff;
    final long value3 = buffer[offset + 2] & 0xff;
    final long value4 = buffer[offset + 3] & 0xff;
    final int value5 = buffer[offset + 4] & 0xff;
    final int value6 = buffer[offset + 5] & 0xff;
    final int value7 = buffer[offset + 6] & 0xff;
    final int value8 = buffer[offset + 7] & 0xff;
    return value1 << 56 | value2 << 48 | value3 << 40 | value4 << 32 | value5 << 24 | value6 << 16 | value7 << 8
        | value8;
  }

  public static long from64Little(final byte[] buffer, final int offset)
  {
    final int value1 = buffer[offset] & 0xff;
    final int value2 = buffer[offset + 1] & 0xff;
    final int value3 = buffer[offset + 2] & 0xff;
    final int value4 = buffer[offset + 3] & 0xff;
    final long value5 = buffer[offset + 4] & 0xff;
    final long value6 = buffer[offset + 5] & 0xff;
    final long value7 = buffer[offset + 6] & 0xff;
    final long value8 = buffer[offset + 7] & 0xff;
    return value8 << 56 | value7 << 48 | value6 << 40 | value5 << 32 | value4 << 24 | value3 << 16 | value2 << 8
        | value1;
  }

  public static BigInteger toBigInteger(final byte[] data, final int offset, final int numBytes,
      final ByteOrder byteOrder)
  {
    return toBigInteger(clone(data, offset, numBytes, 0), byteOrder);
  }

  public static BigInteger toBigInteger(final byte[] data, final ByteOrder byteOrder)
  {
    BigInteger result;
    if (data != null && data.length > 0)
    {
      byte[] array = clone(data);
      if (byteOrder == ByteOrder.LittleEndian)
      {
        int index1 = 0;
        int index2 = array.length - 1;
        while (index1 < index2)
        {
          final byte temp = array[index1];
          array[index1] = array[index2];
          array[index2] = temp;
          index1++;
          index2--;
        }
      }
      result = new BigInteger(array);
    }
    else
    {
      result = null;
    }
    return result;
  }

  private static boolean equals(final byte[] array1, final int index1, final byte[] array2, final int index2,
      final int numElements)
  {
    boolean result = true;
    int i1 = index1;
    int i2 = index2;
    int num = numElements;
    while (num > 0)
    {
      if (array1[i1] != array2[i2])
      {
        result = false;
        break;
      }
      i1++;
      i2++;
      num--;
    }
    return result;
  }

  public static int indexOf(final byte[] data, final int initialIndex, final byte[] pattern)
  {
    int result = -2;
    if (data == null || pattern == null || initialIndex < 0 || initialIndex >= data.length
        || pattern.length > data.length)
    {
      result = -1;
    }
    else
    {
      int currentInitial = initialIndex;
      while (currentInitial + pattern.length <= data.length)
      {
        if (equals(data, currentInitial, pattern, 0, pattern.length))
        {
          result = currentInitial;
          break;
        }
        currentInitial++;
      }
    }
    return result;
  }

  /**
   * Create a {@link java.util.Set} of Integer values from argument int values so that the resulting set holds all the
   * different int values found in the arguments.
   *
   * @param array
   *          input int values
   * @return Set of Integer values
   */
  public static Set<Integer> toSet(final int... array)
  {
    final int capacity = array == null ? 0 : array.length;
    final Set<Integer> result = new HashSet<>(capacity);
    if (array != null)
    {
      for (final int value : array)
      {
        result.add(Integer.valueOf(value));
      }
    }
    return result;
  }
}
