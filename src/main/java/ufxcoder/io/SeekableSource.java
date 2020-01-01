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

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;

/**
 * <p>
 * Interface for a seekable input source, allowing to read chunks of data.
 * </p>
 * <p>
 * There is a pointer with each opened source that will determine from which position within the source any read
 * operations will work. That pointer changes automatically with read operations, it will be at the position directly
 * after the byte last rad. The pointer can be set with {@link #seek(long)}. Its current position can be queried with
 * {@link #getPosition()}. The source's size and therefore the maximum position of the pointer is determined with
 * {@link #getLength()}.
 * </p>
 */
public interface SeekableSource extends Closeable
{
  /**
   * Close this source, preventing any subsequent operations to succeed, and release all resources associated with this
   * source.
   */
  @Override
  void close() throws IOException;

  /**
   * Determine the size of this source in bytes.
   *
   * @return size of this source in bytes
   * @throws IOException
   *           if access caused an error
   */
  long getLength() throws IOException;

  String getName();

  /**
   * Determine the position of the pointer within this source, somewhere between 0 and {@link #getLength()}.
   *
   * @return current position as a non-negative integer
   * @throws IOException
   *           if access caused an error
   */
  long getPosition() throws IOException;

  /**
   * Read a single unsigned byte from input.
   *
   * @return byte read as value from 0 to 255 or -1 if end-of-input was reached
   * @throws IOException
   *           on read errors
   */
  int read() throws IOException;

  int read(byte[] buffer, int offset, int length) throws IOException;

  void readFully(byte[] buffer, int offset, int length) throws IOException;

  void seek(long pos) throws IOException;

  void seek(BigInteger offset) throws IOException;

  /**
   * Does the underlying source hold a number of bytes at a certain offset?
   *
   * @param offset
   *          position in the source
   * @param numBytes
   *          number of bytes
   * @return if offset + numBytes &lt;= {@link #getLength()}
   * @throws IOException
   *           if querying current file size fails
   */
  boolean isValidSection(BigInteger offset, BigInteger numBytes) throws IOException;
}
