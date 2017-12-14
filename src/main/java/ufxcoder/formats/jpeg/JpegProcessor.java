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

import java.io.IOException;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.FileDescription;
import ufxcoder.formats.tiff.Msg;
import ufxcoder.io.Segment;

/**
 * Process JPEG image files.
 */
public class JpegProcessor extends AbstractFormatProcessor
{
  @Override
  public FileDescription createDescription()
  {
    return new JpegFileDescription();
  }

  @Override
  public void process()
  {
    final JpegFileDescription desc = new JpegFileDescription();
    setFileDescription(desc);

    Segment header = null;
    try
    {
      header = read(2);
      header.setByteOrder(ByteOrder.BigEndian);
      if (header.int16() == Constants.MAGIC)
      {
        setFormatIdentified(true);
      }
    }
    catch (IOException e)
    {
      error(Msg.CANNOT_READ_GLOBAL_HEADER, e);
    }

    closeSource();
  }

  @Override
  public String[] getTypicalFileExtensions()
  {
    return new String[]
    {
        "jpg", "jpeg"
    };
  }

  @Override
  public String getShortName()
  {
    return "JPEG";
  }

  @Override
  public String getLongName()
  {
    return "Joint Photographic Experts Group (JPEG)";
  }
}
