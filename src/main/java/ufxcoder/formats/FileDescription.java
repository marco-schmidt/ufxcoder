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
package ufxcoder.formats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ufxcoder.conversion.ByteOrder;

/**
 * Data class with information on a single file. May be extended for specific file formats.
 */
public class FileDescription
{
  private File file;
  private ByteOrder byteOrder;
  private final List<String> errorMessages = new ArrayList<String>();
  private final List<ProcessorEvent> events = new ArrayList<ProcessorEvent>();

  public String getErrorMessage()
  {
    String result;
    if (errorMessages.isEmpty())
    {
      result = null;
    }
    else
    {
      if (errorMessages.size() == 1)
      {
        result = errorMessages.get(0);
      }
      else
      {
        final StringBuffer sb = new StringBuffer();
        int msgNr = 1;
        for (final String msg : errorMessages)
        {
          if (sb.length() > 0)
          {
            sb.append(' ');
          }
          sb.append('(');
          sb.append(msgNr++);
          sb.append(") ");
          sb.append(msg);
        }
        result = sb.toString();
      }
    }
    return result;
  }

  public void addErrorMessage(final String errorMessage)
  {
    errorMessages.add(errorMessage);
  }

  public boolean containsEvent(final String key)
  {
    boolean result = false;
    for (final ProcessorEvent event : events)
    {
      if (key.equals(event.getMessageKey()))
      {
        result = true;
        break;
      }
    }
    return result;
  }

  public boolean hasWarningOrHigher()
  {
    boolean result = false;
    for (final ProcessorEvent event : events)
    {
      final EventSeverity severity = event.getSeverity();
      if (severity == EventSeverity.Warning || severity == EventSeverity.Error)
      {
        result = true;
        break;
      }
    }
    return result;
  }

  public void addEvent(final EventSeverity severity, final String messageKey, final String message)
  {
    final ProcessorEvent event = new ProcessorEvent(severity);
    event.setMessage(message);
    event.setMessageKey(messageKey);
    events.add(event);
    if (severity == EventSeverity.Error)
    {
      addErrorMessage(message);
    }
  }

  public ByteOrder getByteOrder()
  {
    return byteOrder;
  }

  public void setByteOrder(final ByteOrder byteOrder)
  {
    this.byteOrder = byteOrder;
  }

  public File getFile()
  {
    return file;
  }

  public String getAbsolutePath()
  {
    return file == null ? "" : file.getAbsolutePath();
  }

  public void setFile(final File file)
  {
    this.file = file;
  }

  public boolean isSuccess()
  {
    return errorMessages.isEmpty() && !hasWarningOrHigher();
  }

  public void reset()
  {
    setFile(null);
    setByteOrder(null);
    errorMessages.clear();
    events.clear();
  }
}
