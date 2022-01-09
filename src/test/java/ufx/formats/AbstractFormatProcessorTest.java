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
package ufx.formats;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import ufxcoder.app.AppConfig;
import ufxcoder.formats.AbstractFormatProcessor;

public abstract class AbstractFormatProcessorTest
{
  public abstract AbstractFormatProcessor createProcessor();

  public AbstractFormatProcessor create(final byte[] data)
  {
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    config.setLocale(Locale.ENGLISH);
    final AbstractFormatProcessor proc = createProcessor();
    proc.setConfig(config);
    proc.open(data);
    return proc;
  }

  @Test
  public void testExtractFileExtension()
  {
    Assert.assertNull("Extract from null yields null.", AbstractFormatProcessor.extractFileExtension(null));
    Assert.assertNull("No path separator or dot yields null.", AbstractFormatProcessor.extractFileExtension("aaa"));
    Assert.assertEquals("File name without path separator and extension yields that extension.", "aaa",
        AbstractFormatProcessor.extractFileExtension("file.aaa"));
  }
}
