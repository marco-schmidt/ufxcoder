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
package ufxcoder.io;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Abstract partial implementation of {@link SeekableSource}.
 */
public abstract class AbstractSeekableSource implements SeekableSource
{
  private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  @Override
  public void seek(final BigInteger offset) throws IOException
  {
    if (offset == null)
    {
      throw new IllegalArgumentException("Argument offset must not be null.");
    }
    if (offset.compareTo(MAX_LONG) > 0)
    {
      throw new IOException(String.format("Unable to seek to position %s, it is larger than the maximum long %d.",
          offset.toString(), Long.MAX_VALUE));
    }
    seek(offset.longValue());
  }

  @Override
  public boolean isValidSection(final BigInteger offset, final BigInteger numBytes)
  {
    boolean result;
    if (offset == null || offset.signum() < 0 || numBytes == null || numBytes.signum() < 0)
    {
      result = false;
    }
    else
    {
      final BigInteger lastByteOffset = offset.add(numBytes);
      try
      {
        final BigInteger sourceSize = BigInteger.valueOf(getLength());
        result = sourceSize.compareTo(lastByteOffset) >= 0;
      }
      catch (IOException ioe)
      {
        result = false;
      }
    }
    return result;
  }
}
