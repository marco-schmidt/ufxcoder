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
package ufxcoder.formats.tiff;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ufxcoder.app.AppConfig;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.io.Segment;

public class TiffReaderTest
{
  /**
   * Correct little-endian signature followed by 42 encoded in the wrong (big-endian) order.
   */
  private static final byte[] BROKEN_LITTLE_ENDIAN_REGULAR_TIFF = new byte[]
  {
      0x49, 0x49, 0x00, 0x2a
  };
  /**
   * Correct big-endian signature followed by 42 encoded in the correct way.
   */
  private static final byte[] CORRECT_BIG_ENDIAN_REGULAR_TIFF = new byte[]
  {
      0x4d, 0x4d, 0x00, 0x2a
  };
  private TiffReader reader;
  private Segment segment;
  private TiffFileDescription desc;
  private TiffProcessor proc;

  @Before
  public void setup()
  {
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    config.setLocale(Locale.ENGLISH);
    proc = new TiffProcessor();
    proc.setConfig(config);
    desc = new TiffFileDescription();
    proc.setFileDescription(desc);
    reader = new TiffReader(proc);
    segment = new Segment();
  }

  @Test
  public void testExtractCorrectBigEndianRegular()
  {
    desc.reset();
    segment.setData(CORRECT_BIG_ENDIAN_REGULAR_TIFF);
    segment.setIndex(0);

    final boolean result = reader.extractByteOrder(desc, segment);
    Assert.assertTrue("Correct header yields positive result.", result);
    Assert.assertFalse("Correct header yields no warnings or errors.", desc.hasWarningOrHigher());
    Assert.assertEquals("Magic byte sequence is detected as such.", ByteOrder.BigEndian, desc.getByteOrder());

    reader.extractVersion(desc, segment);
    Assert.assertTrue("Correct header yields positive result.", proc.isFormatIdentified());
    Assert.assertFalse("Regular TIFF.", desc.isBig());
    Assert.assertFalse("Still no warnings.", desc.hasWarningOrHigher());
  }

  @Test
  public void testExtractBrokenLittleEndianRegular()
  {
    desc.reset();
    segment.setData(BROKEN_LITTLE_ENDIAN_REGULAR_TIFF);
    segment.setIndex(0);

    final boolean result = reader.extractByteOrder(desc, segment);
    Assert.assertTrue("Correct header yields positive result.", result);
    Assert.assertFalse("Correct header yields no warnings or errors.", desc.hasWarningOrHigher());
    Assert.assertEquals("Magic byte sequence is detected as such.", ByteOrder.LittleEndian, desc.getByteOrder());

    reader.extractVersion(desc, segment);
    Assert.assertFalse("Incorrect version leads to negative result.", proc.isFormatIdentified());
    Assert.assertTrue("At least one error.", desc.hasWarningOrHigher());
    Assert.assertTrue("Contains 'invalid version' event.", desc.containsEvent(Msg.INVALID_VERSION));
  }
}
