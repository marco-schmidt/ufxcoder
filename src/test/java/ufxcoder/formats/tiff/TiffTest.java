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

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ufxcoder.app.AppConfig;

public class TiffTest
{
  private TiffProcessor proc;

  @Before
  public void setup()
  {
    // BasicConfigurator.configure();
    proc = new TiffProcessor();
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    proc.setConfig(config);
  }

  @Test
  public void testDateTimeCorrect()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNotNull("Correct date time string must yield non-null result.",
        val.parseDateTimeString("1970:01:01 12:00:00\000"));
  }

  @Test
  public void testDateTimeNull()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (null) must yield null result.", val.parseDateTimeString(null));
  }

  @Test
  public void testDateTimeEmpty()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (empty string) must yield null result.", val.parseDateTimeString(""));
  }

  @Test
  public void testDateTimeText()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (empty string) must yield null result.",
        val.parseDateTimeString("not a date"));
  }

  @Test
  public void testDateTimeMonth13()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (month 13) must yield null result.",
        val.parseDateTimeString("1970:13:01 12:00:00\000"));
  }

  @Test
  public void testDateTime1900February29()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (1900-02-29) must yield null result.",
        val.parseDateTimeString("1900:02:29 12:00:00\000"));
  }

  @Test
  public void testDateTime1997February29()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNull("Incorrect date time string (1997-02-29) must yield null result.",
        val.parseDateTimeString("1997:02:29 12:00:00\000"));
  }

  @Test
  public void testDateTime2000February29()
  {
    final TiffValidator val = new TiffValidator(proc);
    Assert.assertNotNull("Correct date time string must yield non-null result.",
        val.parseDateTimeString("2000:02:29 12:00:00\000"));
  }
}
