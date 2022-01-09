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

import org.junit.Assert;
import org.junit.Test;
import ufx.formats.AbstractFormatProcessorTest;
import ufxcoder.formats.AbstractFormatProcessor;

/**
 * Test {@link TiffProcessor} with various input streams.
 */
public class TiffProcessorTest extends AbstractFormatProcessorTest
{
  @Test
  public void testInputTooShort()
  {
    final TiffProcessor proc = (TiffProcessor) create(new byte[]
    {
        0x17
    });
    proc.process();
    final TiffFileDescription desc = proc.getTiffFileDescription();
    Assert.assertTrue("Expect reading error because input is too short for even a two-byte marker. ",
        desc.containsEvent(Msg.CANNOT_READ_GLOBAL_HEADER));
  }

  @Test
  public void testInputUnknownByteOrder()
  {
    final TiffProcessor proc = (TiffProcessor) create(new byte[]
    {
        0x4a, 0x4b, 0x00, 0x00
    });
    proc.process();
    final TiffFileDescription desc = proc.getTiffFileDescription();
    Assert.assertTrue("Expect inability to identify byte order. ", desc.containsEvent(Msg.INVALID_BYTE_ORDER));
  }

  @Override
  public AbstractFormatProcessor createProcessor()
  {
    return new TiffProcessor();
  }
}
