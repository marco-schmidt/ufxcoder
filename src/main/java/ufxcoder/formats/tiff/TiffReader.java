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
import ufxcoder.conversion.ByteOrder;
import ufxcoder.io.Segment;

/**
 * Read information from a TIFF file.
 */
public class TiffReader
{
  private final TiffProcessor proc;

  public TiffReader(final TiffProcessor proc)
  {
    this.proc = proc;
  }

  /**
   * Try to identify underlying stream as TIFF.
   */
  public Segment identify(final TiffFileDescription desc)
  {
    Segment globalHeader = null;
    try
    {
      globalHeader = proc.read(4);
      if (extractByteOrder(desc, globalHeader))
      {
        extractVersion(desc, globalHeader);
      }
    }
    catch (IOException e)
    {
      proc.error(Msg.CANNOT_READ_GLOBAL_HEADER, e);
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
        proc.error(Msg.INVALID_BYTE_ORDER);
      }
    }
    globalHeader.setIndex(2);
    globalHeader.setByteOrder(desc.getByteOrder());
    return proc.isSuccess();
  }

  public BigInteger extractFirstOffset(final TiffFileDescription desc, final Segment globalHeader) throws IOException
  {
    BigInteger imageFileDirectoryOffset = null;
    if (desc.isSuccess())
    {
      long offsetPosition = 0;
      int offsetByteSize;
      if (desc.isBig())
      {
        proc.append(globalHeader, 12);
        offsetByteSize = globalHeader.int16();
        if (offsetByteSize != Constants.OFFSET_SIZE_BIG)
        {
          proc.error(Msg.INVALID_BIG_TIFF_OFFSET_SIZE, Constants.OFFSET_SIZE_BIG, offsetByteSize);
        }
        final int zero = globalHeader.int16();
        if (zero != 0)
        {
          proc.error(Msg.INVALID_BIG_TIFF_OFFSET_ZERO, zero);
        }
      }
      else
      {
        offsetPosition = globalHeader.getOffset() + globalHeader.getLength();
        offsetByteSize = 4;
        proc.append(globalHeader, offsetByteSize);
      }
      if (proc.isSuccess())
      {
        imageFileDirectoryOffset = globalHeader.bigInt(offsetByteSize);
        desc.addOffset(imageFileDirectoryOffset);
        if (!proc.isValidSourceOffset(imageFileDirectoryOffset))
        {
          proc.error("tiff.error.invalid_file_offset", imageFileDirectoryOffset, offsetPosition);
        }
      }
    }
    return imageFileDirectoryOffset;
  }

  public void extractVersion(final TiffFileDescription desc, final Segment globalHeader)
  {
    final int version = globalHeader.int16();
    if (version == Constants.MAGIC_TIFF)
    {
      desc.setBig(false);
      proc.setFormatIdentified(true);
    }
    else
    {
      if (version == Constants.MAGIC_BIG_TIFF)
      {
        desc.setBig(true);
        proc.setFormatIdentified(true);
      }
      else
      {
        proc.error(Msg.INVALID_VERSION, version);
      }
    }
  }
}
