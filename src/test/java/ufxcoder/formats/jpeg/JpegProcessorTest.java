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
package ufxcoder.formats.jpeg;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import ufxcoder.app.AppConfig;
import ufxcoder.app.ProcessMode;

/**
 * Test {@link JpegProcessor} with various input streams.
 */
public class JpegProcessorTest
{
  private JpegProcessor create(final byte[] data)
  {
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    config.setLocale(Locale.ENGLISH);
    final JpegProcessor proc = new JpegProcessor();
    proc.setConfig(config);
    proc.open(data);
    return proc;
  }

  @Test
  public void testInputTooShort()
  {
    final JpegProcessor proc = create(new byte[]
    {
        0x17
    });
    proc.process();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    Assert.assertTrue("Expect reading error because input is too short for even a two-byte marker. ",
        desc.containsEvent(Msg.CANNOT_READ_HEADER));
  }

  @Test
  public void testInvalidMarker()
  {
    final JpegProcessor proc = create(new byte[]
    {
        (byte) 0x9a, 0x44
    });
    proc.process();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    Assert.assertTrue("Expect invalid marker error.", desc.containsEvent(Msg.INVALID_MARKER));
  }

  @Test
  public void testFirstMarkerNotStartOfImage()
  {
    // first two bytes are a correct marker but not the right one
    final JpegProcessor proc = create(new byte[]
    {
        (byte) 0xff, (byte) 0xda
    });
    proc.process();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    Assert.assertTrue("Expect first marker to be start-of-image.", desc.containsEvent(Msg.FIRST_MARKER_NOT_SOI));
  }

  @Test
  public void testIdentifyCorrectMarker()
  {
    final JpegProcessor proc = create(new byte[]
    {
        (byte) 0xff, (byte) 0xd8
    });
    proc.getConfig().setMode(ProcessMode.Identify);
    proc.process();
    final JpegFileDescription desc = proc.getJpegFileDescription();
    Assert.assertTrue("Expect start-of-image marker to warrant absence of errors.", desc.isSuccess());
    Assert.assertTrue("Expect start-of-image marker to warrant successful identification.", proc.isFormatIdentified());
  }
}
