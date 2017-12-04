/*
 * Copyright 2017 the original author or authors.
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

import org.junit.Assert;
import org.junit.Test;

public class ArrayTest
{
  private static final byte[] ZERO_ONE_TWO_THREE = new byte[]
  {
      0, 1, 2, 3
  };

  @Test
  public void testClone()
  {
    Assert.assertNull("Clone of null array is null.", Array.clone(null));
    final byte[] clone = Array.clone(ZERO_ONE_TWO_THREE);
    Assert.assertArrayEquals("Clone content must be identical to original.", ZERO_ONE_TWO_THREE, clone);
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
    Assert.assertEquals("Big endian of zero-one-two-three must be 66051.", 3 + (2 * 256) + 65536,
        Array.from32(ZERO_ONE_TWO_THREE, 0, ByteOrder.BigEndian));
    Assert.assertEquals("Big endian of zero-one-two-three must be 66051.", 3 + (2 * 256) + 65536,
        Array.from32Big(ZERO_ONE_TWO_THREE, 0));
    Assert.assertEquals("Little endian of zero-one-two-three must be .", 0 + (1 * 256) + 2 * 65536 + 3 * 16777216,
        Array.from32(ZERO_ONE_TWO_THREE, 0, ByteOrder.LittleEndian));
    Assert.assertEquals("Little endian of zero-one-two-three must be .", 0 + (1 * 256) + 2 * 65536 + 3 * 16777216,
        Array.from32Little(ZERO_ONE_TWO_THREE, 0));
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
}
