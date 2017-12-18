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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import ufxcoder.formats.FileDescription;

/**
 * Data class describing the details of a JPEG file.
 */
public class JpegFileDescription extends FileDescription
{
  private BigInteger initialOffset = BigInteger.ZERO;
  private boolean embedded;
  private final List<Marker> markers = new ArrayList<>();

  public BigInteger getInitialOffset()
  {
    return initialOffset;
  }

  public void setInitialOffset(final BigInteger initialOffset)
  {
    this.initialOffset = initialOffset;
  }

  public boolean isEmbedded()
  {
    return embedded;
  }

  public void setEmbedded(final boolean embedded)
  {
    this.embedded = embedded;
  }

  public void add(final Marker marker)
  {
    markers.add(marker);
  }

  public List<Marker> getMarkers()
  {
    return markers;
  }
}
