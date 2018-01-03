/*
 * Copyright 2017, 2018 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A {@link SeekableSource} based on a {@link RandomAccessFile}.
 */
public class FileSource extends AbstractSeekableSource
{
  private RandomAccessFile input;
  private String name;

  public FileSource(final File file) throws IOException
  {
    this(file.getPath());
  }

  public FileSource(final String name) throws IOException
  {
    this(new RandomAccessFile(name, "r"), name);
  }

  public FileSource(final RandomAccessFile file, final String name)
  {
    super();
    input = file;
    this.name = name;
  }

  @Override
  public void close() throws IOException
  {
    if (input != null)
    {
      try
      {
        input.close();
      }
      finally
      {
        input = null;
      }
    }
  }

  @Override
  public int read(final byte[] buffer, final int offset, final int length) throws IOException
  {
    return input.read(buffer, offset, length);
  }

  @Override
  public void readFully(final byte[] buffer, final int offset, final int length) throws IOException
  {
    int numLeft = length;
    int index = offset;
    while (numLeft > 0)
    {
      final int numRead = read(buffer, index, numLeft);
      if (numRead > 0)
      {
        index += numRead;
        numLeft -= numRead;
      }
      else
      {
        throw new IOException("Unable to complete read operation.");
      }
    }
  }

  @Override
  public void seek(final long pos) throws IOException
  {
    input.seek(pos);
  }

  @Override
  public long getPosition() throws IOException
  {
    return input.getFilePointer();
  }

  @Override
  public long getLength() throws IOException
  {
    return input.length();
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public int read() throws IOException
  {
    return input.read();
  }
}
