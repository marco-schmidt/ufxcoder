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

import java.io.IOException;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.FileDescription;
import ufxcoder.io.Segment;

/**
 * Processor for the Tagged Image File Format (TIFF).
 */
public class TiffProcessor extends AbstractFormatProcessor
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TiffProcessor.class);
  private BigInteger imageFileDirectoryOffset;

  @Override
  public void process()
  {
    final TiffFileDescription desc = new TiffFileDescription();
    setFileDescription(desc);
    final TiffReader reader = new TiffReader(this);
    final Segment globalHeader = reader.identify(desc);
    if (!isIdentify() && desc.isSuccess())
    {
      try
      {
        imageFileDirectoryOffset = reader.extractFirstOffset(desc, globalHeader);
      }
      catch (IOException e)
      {
        LOGGER.error("Unable to read TIFF offset.", e);
      }
      final ImageFileDirectoryReader ifdReader = new ImageFileDirectoryReader(this);
      ifdReader.readAllMetadata(imageFileDirectoryOffset);
    }

    closeSource();
  }

  public TiffFileDescription getTiffFileDescription()
  {
    TiffFileDescription result = null;
    final FileDescription desc = getFileDescription();
    if (desc instanceof TiffFileDescription)
    {
      result = (TiffFileDescription) desc;
    }
    return result;
  }

  @Override
  public FileDescription createDescription()
  {
    return new TiffFileDescription();
  }

  @Override
  public String[] getTypicalFileExtensions()
  {
    return new String[]
    {
        "tif", "tiff", "dng", "cr2"
    };
  }

  @Override
  public String getShortName()
  {
    final TiffFileDescription desc = getTiffFileDescription();
    String result = "TIFF";
    if (desc != null)
    {
      if (desc.isCr2())
      {
        result = "CR2";
      }
      if (desc.isDng())
      {
        result = "DNG";
      }
    }
    return result;
  }

  @Override
  public String getLongName()
  {
    final TiffFileDescription desc = getTiffFileDescription();
    String result = "Tagged Image File Format";
    if (desc != null)
    {
      if (desc.isCr2())
      {
        result = "Canon Raw";
      }
      if (desc.isDng())
      {
        result = "Digital Negative";
      }
    }
    return result;
  }
}
