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
import ufxcoder.io.Segment;

/**
 * Read information from a TIFF file.
 */
public class TiffReader
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TiffReader.class);
  private final TiffProcessor proc;

  public TiffReader(final TiffProcessor proc)
  {
    this.proc = proc;
  }

  public Segment identify(final TiffFileDescription desc)
  {
    Segment globalHeader = null;
    try
    {
      globalHeader = proc.read(4);
      extractByteOrder(desc, globalHeader);
      extractVersion(desc, globalHeader);
    }
    catch (IOException e)
    {
      desc.addErrorMessage(proc.msg("tiff.error.cannot_read_global_header"));
    }
    return globalHeader;
  }

  public boolean extractByteOrder(final TiffFileDescription desc, final Segment globalHeader)
  {
    if (globalHeader.equalsAt(0, Constants.getSignatureIntel()))
    {
      desc.setByteOrder(ByteOrder.LittleEndian);
    }
    else
    {
      if (globalHeader.equalsAt(0, Constants.getSignatureMotorola()))
      {
        desc.setByteOrder(ByteOrder.BigEndian);
      }
      else
      {
        proc.error("tiff.error.signature_missing");
      }
    }
    globalHeader.setIndex(2);
    globalHeader.setByteOrder(desc.getByteOrder());
    return proc.isSuccess();
  }

  public void extractFirstOffset(final TiffFileDescription desc, final Segment globalHeader) throws IOException
  {
    if (desc.isSuccess())
    {
      long imageFileDirectoryOffset;
      long offsetPosition = 0;
      if (desc.isBig())
      {
        proc.append(globalHeader, 12);
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
        proc.append(globalHeader, 4);
        imageFileDirectoryOffset = globalHeader.int32();
      }
      desc.addOffset(Long.valueOf(imageFileDirectoryOffset));
      if (!proc.isValidSourceOffset(imageFileDirectoryOffset))
      {
        desc.addErrorMessage(proc.msg("tiff.error.invalid_file_offset", imageFileDirectoryOffset, offsetPosition));
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
      proc.setFormatIdentified(true);
    }
    else
      if (version == Constants.MAGIC_BIG_TIFF)
      {
        desc.setBig(true);
        styleKey = "tiff.style.big";
        proc.setFormatIdentified(true);
      }
      else
      {
        desc.addErrorMessage(proc.msg("tiff.error.invalid_version"));
      }
    if (styleKey != null)
    {
      LOGGER.debug(String.format("%s=%s", proc.msg("tiff.style"), proc.msg(styleKey)));
    }
  }

}
