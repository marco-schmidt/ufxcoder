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
package ufxcoder.io;

import java.io.IOException;
import ufxcoder.conversion.Array;

/**
 * A {@link SeekableSource} based on data kept in memory.
 */
public class MemorySource extends AbstractSeekableSource
{
  private final byte[] data;
  private int index;
  private boolean closed;

  public MemorySource(final byte[] buffer)
  {
    super();
    if (buffer == null)
    {
      throw new IllegalArgumentException("Must have non-null buffer argument.");
    }
    data = Array.clone(buffer);
    index = 0;
  }

  @Override
  public void close() throws IOException
  {
    closed = true;
  }

  @Override
  public int read(final byte[] buffer, final int offset, final int length) throws IOException
  {
    if (closed)
    {
      throw new IOException("Cannot read from closed input.");
    }
    final int num = Math.min(length, data.length - index);
    System.arraycopy(data, index, buffer, offset, num);
    index += num;
    return num;
  }

  @Override
  public void seek(final long pos) throws IOException
  {
    if (closed)
    {
      throw new IOException("Cannot seek in closed input.");
    }
    if (pos >= 0 && pos <= data.length)
    {
      index = (int) pos;
    }
    else
    {
      throw new IOException(String.format("Invalid seek position %d, must be from 0 to %d.", pos, data.length));
    }
  }

  @Override
  public long getPosition() throws IOException
  {
    return index;
  }

  @Override
  public void readFully(final byte[] buffer, final int offset, final int length) throws IOException
  {
    if (index + length > data.length || length < 0)
    {
      throw new IOException("Cannot read required number of bytes.");
    }
    System.arraycopy(data, index, buffer, offset, length);
    index += length;
  }

  @Override
  public long getLength() throws IOException
  {
    return data.length;
  }

  @Override
  public String getName()
  {
    return "?";
  }

  @Override
  public int read() throws IOException
  {
    int result;
    if (index >= data.length)
    {
      result = -1;
    }
    else
    {
      result = data[index++];
    }
    return result;
  }
}
