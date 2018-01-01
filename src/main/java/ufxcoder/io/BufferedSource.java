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

import java.io.IOException;

/**
 * Wrapper around {@link SeekableSource} to provide buffered input.
 */
public class BufferedSource
{
  private final SeekableSource input;
  private final byte[] buffer;
  private int index;
  private int length;

  public BufferedSource(final SeekableSource in, final int bufferSize)
  {
    input = in;
    buffer = new byte[bufferSize];
    index = buffer.length;
    length = index;
  }

  public int next() throws IOException
  {
    int result;
    if (index == length)
    {
      length = input.read(buffer, 0, buffer.length);
      if (length < 1)
      {
        result = -1;
      }
      else
      {
        result = buffer[0] & 0xff;
        index = 1;
      }
    }
    else
    {
      result = buffer[index++] & 0xff;
    }
    return result;
  }

  /**
   * Position underlying {@link SeekableSource} so that it's at the offset a number of bytes before the current
   * position.
   *
   * @param numBytes
   *          position that many bytes before current position
   * @throws IOException
   *           if underlying input throws that exception when reading
   */
  public void seekBack(final int numBytes) throws IOException
  {
    // figure out current position in input
    long position = input.getPosition(); // actual position after last read
    position -= length; // position before last read
    position += index; // that many bytes were already consumed in buffer
    position -= numBytes; // seek back argument number of bytes
    input.seek(position);
  }
}
