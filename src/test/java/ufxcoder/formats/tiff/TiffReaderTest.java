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
package ufxcoder.formats.tiff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.io.Segment;

public class TiffReaderTest
{
  private static final byte[] BIG_ENDIAN = new byte[]
  {
      0x4d, 0x4d, 0x00, 0x2a
  };
  private TiffReader reader;
  private Segment segment;
  private TiffFileDescription desc;

  @Before
  public void setup()
  {
    desc = new TiffFileDescription();
    final TiffProcessor proc = new TiffProcessor();
    proc.setFileDescription(desc);
    reader = new TiffReader(proc);
    segment = new Segment();
  }

  @Test
  public void testExtractByteOrderBigEndian()
  {
    segment.setData(BIG_ENDIAN);
    segment.setIndex(0);
    final boolean result = reader.extractByteOrder(desc, segment);
    Assert.assertTrue("Correct header yields positive result.", result);
    Assert.assertFalse("Correct header yields no warnings or errors.", desc.hasWarningOrHigher());
    Assert.assertEquals("Magic byte sequence is detected as su.", ByteOrder.BigEndian, desc.getByteOrder());
  }
}
