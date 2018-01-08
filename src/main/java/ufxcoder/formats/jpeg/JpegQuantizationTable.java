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
package ufxcoder.formats.jpeg;

import ufxcoder.conversion.Array;

/**
 * A single quantization table.
 */
public class JpegQuantizationTable
{
  private int id;
  private int precision;
  private int[] data;

  public int getId()
  {
    return id;
  }

  public void setId(final int id)
  {
    this.id = id;
  }

  public int getPrecision()
  {
    return precision;
  }

  public void setPrecision(final int precision)
  {
    this.precision = precision;
  }

  public int[] getData()
  {
    return Array.clone(data);
  }

  public void setData(final int... array)
  {
    data = Array.clone(array);
  }
}