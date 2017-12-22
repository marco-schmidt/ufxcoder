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

/**
 * Data class describing a single component in a JPEG scan.
 */
public class JpegScanComponent
{
  private int id;
  private int ac;
  private int dc;

  public int getId()
  {
    return id;
  }

  public void setId(final int id)
  {
    this.id = id;
  }

  public int getAc()
  {
    return ac;
  }

  public void setAc(final int ac)
  {
    this.ac = ac;
  }

  public int getDc()
  {
    return dc;
  }

  public void setDc(final int dc)
  {
    this.dc = dc;
  }
}
