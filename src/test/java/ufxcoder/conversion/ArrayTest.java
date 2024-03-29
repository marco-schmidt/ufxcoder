/*
 * Copyright 2017, 2018, 2019, 2020, 2021, 2022 the original author or authors.
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
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class ArrayTest
{
  private static final byte[] ZERO_ONE_TWO_THREE = new byte[]
  {
      0, 1, 2, 3
  };
  private static final byte[] ZERO_ONE_TO_SEVEN = new byte[]
  {
      0, 1, 2, 3, 4, 5, 6, 7,
  };
  private static final byte[] ZEROES = new byte[]
  {
      0, 0, 0, 0, 0, 0, 0, 0
  };

  @Test
  public void testClone()
  {
    Assert.assertNull("Clone of null array is null.", Array.clone((byte[]) null));
    Assert.assertNull("Clone of null array and valid offset is null.", Array.clone((byte[]) null, 0));
    Assert.assertNull("Clone of null array and invalid offset is null.", Array.clone((byte[]) null, -1));

    Assert.assertNull("Clone of null int array and valid offset is null.", Array.clone((int[]) null, 0));
    Assert.assertNull("Clone of non-null int array and invalid offset is null.", Array.clone(new int[]
    {}, -1));
    Assert.assertNull("Clone of null int array and invalid additional elements is null.",
        Array.clone((int[]) null, 0, 0, -1));

    Assert.assertNull("Clone of null array and invalid additional elements is null.",
        Array.clone((byte[]) null, 0, 0, -1));
    Assert.assertNull("Clone of null array and valid additional elements is null.",
        Array.clone((byte[]) null, 0, 0, 0));
    Assert.assertNull("Clone of non-null array and invalid additional elements is null.", Array.clone(new byte[]
    {}, 0, 0, -1));
    Assert.assertNull("Clone of non-null array and invalid offset is null.", Array.clone(new byte[]
    {}, -1));
    Assert.assertEquals("Clone of one-element array with ten additional elements has length 11.", 11,
        Array.clone(new byte[]
        {
            1
        }, 10).length);
    final byte[] clone = Array.clone(ZERO_ONE_TWO_THREE);
    Assert.assertArrayEquals("Clone content must be identical to original.", ZERO_ONE_TWO_THREE, clone);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCloneByteTooLarge()
  {
    Array.clone(new byte[]
    {
        0
    }, 0, 1, Integer.MAX_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCloneIntTooLarge()
  {
    Array.clone(new int[]
    {
        0
    }, 0, 1, Integer.MAX_VALUE);
  }

  @Test
  public void testFrom16()
  {
    Assert.assertEquals("Big endian of zero-one must be one.", 1,
        Array.from16(ZERO_ONE_TWO_THREE, 0, ByteOrder.BigEndian));
    Assert.assertEquals("Big endian of zero-one must be one.", 1, Array.from16Big(ZERO_ONE_TWO_THREE, 0));
    Assert.assertEquals("Little endian of zero-one must be 256.", 256,
        Array.from16(ZERO_ONE_TWO_THREE, 0, ByteOrder.LittleEndian));
    Assert.assertEquals("Little endian of zero-one must be 256.", 256, Array.from16Little(ZERO_ONE_TWO_THREE, 0));
  }

  @Test
  public void testFrom32()
  {
    Assert.assertEquals("Big endian of zero-one-two-three must be 66051.", 3 + 2 * 256 + 65536,
        Array.from32(ZERO_ONE_TWO_THREE, 0, ByteOrder.BigEndian));
    Assert.assertEquals("Big endian of zero-one-two-three must be 66051.", 3 + 2 * 256 + 65536,
        Array.from32Big(ZERO_ONE_TWO_THREE, 0));
    Assert.assertEquals("Little endian of zero-one-two-three must be .", 0 + 1 * 256 + 2 * 65536 + 3 * 16777216,
        Array.from32(ZERO_ONE_TWO_THREE, 0, ByteOrder.LittleEndian));
    Assert.assertEquals("Little endian of zero-one-two-three must be .", 0 + 1 * 256 + 2 * 65536 + 3 * 16777216,
        Array.from32Little(ZERO_ONE_TWO_THREE, 0));
  }

  @Test
  public void testFrom64()
  {
    Assert.assertEquals("Big endian of zeroes must be zero.", 0, Array.from64(ZEROES, 0, ByteOrder.BigEndian));
    Assert.assertEquals("Little endian of zeroes must be zero.", 0, Array.from64(ZEROES, 0, ByteOrder.LittleEndian));
    Assert.assertEquals("Big endian of zero-to-seven must be 283686952306183L.", 283686952306183L,
        Array.from64(ZERO_ONE_TO_SEVEN, 0, ByteOrder.BigEndian));
    Assert.assertEquals("Big endian of zero-to-seven must be 506097522914230528L.", 506097522914230528L,
        Array.from64(ZERO_ONE_TO_SEVEN, 0, ByteOrder.LittleEndian));
  }

  @Test
  public void testIndexOf()
  {
    Assert.assertEquals("Fail with null data.", -1, Array.indexOf(null, 0, null));
    Assert.assertEquals("Fail with null pattern (non-null data).", -1, Array.indexOf(ZEROES, 0, null));
    Assert.assertEquals("Fail with negative index (both data and pattern non-null).", -1, Array.indexOf(new byte[]
    {}, -1, new byte[]
    {}));
    Assert.assertEquals("Fail with null pattern.", -1, Array.indexOf(ZEROES, ZEROES.length + 1, ZERO_ONE_TWO_THREE));
    Assert.assertEquals("Fail with pattern longer than data.", -1, Array.indexOf(ZERO_ONE_TWO_THREE, 0, ZEROES));
  }

  @Test
  public void testIndexOfNoMatch()
  {
    final byte[] data = new byte[]
    {
        34, 99, 25
    };
    final byte[] pattern = new byte[]
    {
        34, 17
    };
    Assert.assertTrue("Searching for pattern not contained must yield negative value.",
        Array.indexOf(data, 0, pattern) < 0);
  }

  @Test
  public void testIndexMatchFirst()
  {
    final byte[] data = new byte[]
    {
        12, 34, 17, 25, 34, 17
    };
    final byte[] pattern = new byte[]
    {
        34, 17
    };
    final int initialIndex = 0;
    Assert.assertEquals("Searching for pattern contained at index 1 and 4 must return the first match, 1.", 1,
        Array.indexOf(data, initialIndex, pattern));
  }

  @Test
  public void testToBigIntegerNull()
  {
    Assert.assertNull("Null input leads to null output.", Array.toBigInteger(null, ByteOrder.BigEndian));
    Assert.assertNull("Null input leads to null output.", Array.toBigInteger(null, ByteOrder.LittleEndian));
  }

  @Test
  public void testToBigIntegerEmpty()
  {
    final byte[] array = new byte[0];
    Assert.assertNull("Empty input leads to null output.", Array.toBigInteger(array, ByteOrder.BigEndian));
    Assert.assertNull("Empty input leads to null output.", Array.toBigInteger(array, ByteOrder.LittleEndian));
  }

  @Test
  public void testToBigIntegerSingleByte()
  {
    final byte value = 99;
    final byte[] array = new byte[]
    {
        value
    };
    Assert.assertEquals("Single byte leads to exactly that value.", value,
        Array.toBigInteger(array, ByteOrder.BigEndian).longValue());
  }

  @Test
  public void testToBigIntegerTwoBytes()
  {
    final byte[] array = new byte[]
    {
        1, 4
    };
    Assert.assertEquals("Two bytes big endian.", 1 * 256 + 4,
        Array.toBigInteger(array, ByteOrder.BigEndian).longValue());
    Assert.assertEquals("Two bytes little endian.", 4 * 256 + 1,
        Array.toBigInteger(array, ByteOrder.LittleEndian).longValue());
  }

  @Test
  public void testToBigInteger()
  {
    Assert.assertEquals("Four bytes big endian.", BigInteger.ZERO,
        Array.toBigInteger(ZEROES, 0, 2, ByteOrder.BigEndian));
  }

  @Test
  public void testToBigIntegerFourBytes()
  {
    final byte[] array = new byte[]
    {
        1, 2, 3, 4
    };
    Assert.assertEquals("Four bytes big endian.", 1 * 16777216L + 2 * 65536L + 3 * 256L + 4,
        Array.toBigInteger(array, ByteOrder.BigEndian).longValue());
    Assert.assertEquals("Four bytes little endian.", 4 * 16777216L + 3 * 65536L + 2 * 256L + 1,
        Array.toBigInteger(array, ByteOrder.LittleEndian).longValue());
  }

  @Test
  public void testToSetNull()
  {
    final Set<Integer> set = Array.toSet(null);
    Assert.assertNotNull("Null array leads to non-null set.", set);
    Assert.assertEquals("Null array leads to size zero set.", 0, set.size());
  }

  @Test
  public void testToSetEmpty()
  {
    final Set<Integer> set = Array.toSet();
    Assert.assertNotNull("Empty array leads to non-null set.", set);
    Assert.assertEquals("Empty array leads to size zero set.", 0, set.size());
  }

  @Test
  public void testToSetInputDuplicates()
  {
    final Set<Integer> set = Array.toSet(17, Integer.MIN_VALUE, 17, Integer.MAX_VALUE);
    Assert.assertNotNull("Test array leads to non-null set.", set);
    Assert.assertEquals("Test array has three unique values.", 3, set.size());
    Assert.assertTrue("Test array contains 17.", set.contains(17));
    Assert.assertFalse("Test array does not contain 0.", set.contains(0));
  }
}
