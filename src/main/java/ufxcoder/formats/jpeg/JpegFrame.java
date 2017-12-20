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
 * Data class describing a JPEG frame.
 */
public class JpegFrame
{
  private boolean baseline;
  private boolean extended;
  private boolean lossless;
  private boolean progressive;
  private int samplePrecision;

  public int getSamplePrecision()
  {
    return samplePrecision;
  }

  public void setSamplePrecision(final int samplePrecision)
  {
    this.samplePrecision = samplePrecision;
  }

  public boolean isBaseline()
  {
    return baseline;
  }

  public void setBaseline(final boolean baseline)
  {
    this.baseline = baseline;
  }

  public boolean isExtended()
  {
    return extended;
  }

  public void setExtended(final boolean extended)
  {
    this.extended = extended;
  }

  public boolean isLossless()
  {
    return lossless;
  }

  public void setLossless(final boolean lossless)
  {
    this.lossless = lossless;
  }

  public boolean isProgressive()
  {
    return progressive;
  }

  public void setProgressive(final boolean progressive)
  {
    this.progressive = progressive;
  }
}
