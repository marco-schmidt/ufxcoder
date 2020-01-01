/*
 * Copyright 2017, 2018, 2019, 2020 the original author or authors.
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Various helper methods converting date and time.
 */
public class TiffDateTime
{
  public Date parseDateTimeString(final String str)
  {
    Date result;
    if (str == null)
    {
      result = null;
    }
    else
    {
      final DateFormat parser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss'\u0000'", Locale.ENGLISH);
      parser.setLenient(false);
      try
      {
        result = parser.parse(str);
      }
      catch (ParseException e)
      {
        result = null;
      }
    }
    return result;
  }
}
