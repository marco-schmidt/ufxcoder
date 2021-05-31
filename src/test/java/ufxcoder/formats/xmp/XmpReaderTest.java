/*
 * Copyright 2017, 2018, 2019, 2020, 2021 the original author or authors.
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
package ufxcoder.formats.xmp;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import ufxcoder.app.AppConfig;
import ufxcoder.formats.tiff.TiffFileDescription;
import ufxcoder.formats.tiff.TiffProcessor;

public class XmpReaderTest
{
  public TiffProcessor create(final byte[] data)
  {
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    config.setLocale(Locale.ENGLISH);
    final TiffProcessor proc = new TiffProcessor();
    proc.setFileDescription(new TiffFileDescription());
    proc.setConfig(config);
    proc.open(data);
    return proc;
  }

  @Test
  public void testXmpReading()
  {
    final TiffProcessor processor = create(new byte[]
    {});
    final XmpReader reader = new XmpReader(processor);
    Assert.assertFalse("Do not find XMP in empty array.", reader.parseXmp(new byte[]
    {
        0
    }));
    Assert.assertEquals("Index of non-existing packet is -1.", -1, XmpReader.findXpacket(new byte[]
    {}, 0));
  }
}
