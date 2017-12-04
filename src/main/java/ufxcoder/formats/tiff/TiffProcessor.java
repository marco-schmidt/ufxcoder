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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.FileDescription;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Processor for the Tagged Image File Format (TIFF).
 */
public class TiffProcessor extends AbstractFormatProcessor
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TiffProcessor.class);
  private long imageFileDirectoryOffset;

  private boolean extractByteOrder(final TiffFileDescription desc, final Segment globalHeader)
  {
    String msgKey = null;
    if (globalHeader.equalsAt(0, Constants.getSignatureIntel()))
    {
      desc.setByteOrder(ByteOrder.LittleEndian);
      msgKey = "tiff.byteorder.littleendian";
    }
    else
    {
      if (globalHeader.equalsAt(0, Constants.getSignatureMotorola()))
      {
        desc.setByteOrder(ByteOrder.BigEndian);
        msgKey = "tiff.byteorder.bigendian";
      }
      else
      {
        desc.addErrorMessage(msg("tiff.error.signature_missing"));
      }
    }
    if (msgKey != null)
    {
      LOGGER.debug(msg("tiff.byteorder") + "=" + msg(msgKey));
    }
    globalHeader.setIndex(2);
    globalHeader.setByteOrder(desc.getByteOrder());
    return isSuccess();
  }

  @Override
  public void process()
  {
    final TiffFileDescription desc = new TiffFileDescription();
    setFileDescription(desc);

    final Segment globalHeader = identify(desc);
    if (!isIdentify() && desc.isSuccess())
    {
      try
      {
        extractFirstOffset(desc, globalHeader);
      }
      catch (IOException e)
      {
        LOGGER.error("Unable to read TIFF offset.", e);
      }
      final ImageFileDirectoryReader reader = new ImageFileDirectoryReader(this);
      reader.readAllMetadata(imageFileDirectoryOffset);
    }

    final SeekableSource src = getSource();
    if (src != null)
    {
      try
      {
        src.close();
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
  }

  private Segment identify(final TiffFileDescription desc)
  {
    Segment globalHeader = null;
    try
    {
      globalHeader = read(4);
      extractByteOrder(desc, globalHeader);
      extractVersion(desc, globalHeader);
    }
    catch (IOException e)
    {
      desc.addErrorMessage(msg("tiff.error.cannot_read_global_header"));
    }
    return globalHeader;
  }

  private void extractFirstOffset(final TiffFileDescription desc, final Segment globalHeader) throws IOException
  {
    if (desc.isSuccess())
    {
      long offsetPosition = 0;
      if (desc.isBig())
      {
        append(globalHeader, 12);
        final int offsetByteSize = globalHeader.int16();
        if (offsetByteSize != Constants.OFFSET_SIZE_BIG)
        {
          desc.addErrorMessage("x");
        }
        final int zero = globalHeader.int16();
        if (zero != 0)
        {
          desc.addErrorMessage("x");
        }
        imageFileDirectoryOffset = globalHeader.int64();
      }
      else
      {
        offsetPosition = globalHeader.getOffset() + globalHeader.getLength();
        append(globalHeader, 4);
        imageFileDirectoryOffset = globalHeader.int32();
      }
      desc.addOffset(Long.valueOf(imageFileDirectoryOffset));
      if (!isValidSourceOffset(imageFileDirectoryOffset))
      {
        desc.addErrorMessage(msg("tiff.error.invalid_file_offset", imageFileDirectoryOffset, offsetPosition));
      }
    }
  }

  private void extractVersion(final TiffFileDescription desc, final Segment globalHeader)
  {
    final int version = globalHeader.int16();
    String styleKey = null;
    if (version == Constants.MAGIC_TIFF)
    {
      desc.setBig(false);
      styleKey = "tiff.style.regular";
      setFormatIdentified(true);
    }
    else
      if (version == Constants.MAGIC_BIG_TIFF)
      {
        desc.setBig(true);
        styleKey = "tiff.style.big";
        setFormatIdentified(true);
      }
      else
      {
        desc.addErrorMessage(msg("tiff.error.invalid_version"));
      }
    if (styleKey != null)
    {
      LOGGER.debug(String.format("%s=%s", msg("tiff.style"), msg(styleKey)));
    }
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
