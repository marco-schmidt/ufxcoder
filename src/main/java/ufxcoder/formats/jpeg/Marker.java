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
package ufxcoder.formats.jpeg;

import ufxcoder.io.Segment;

/**
 * Data class for a JPEG marker and its data.
 */
public class Marker
{
  private int id;
  private int length;
  private int number;
  private Segment segment;

  public int getId()
  {
    return id;
  }

  public void setId(final int id)
  {
    this.id = id;
  }

  public int getLength()
  {
    return length;
  }

  public void setLength(final int length)
  {
    this.length = length;
  }

  public Segment getSegment()
  {
    return segment;
  }

  public void setSegment(final Segment segment)
  {
    this.segment = segment;
  }

  public int getNumber()
  {
    return number;
  }

  public void setNumber(final int number)
  {
    this.number = number;
  }
}
