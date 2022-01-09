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
package ufxcoder.formats.jpeg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class describing a Jpeg scan.
 */
public class JpegScan
{
  private int numComponents;
  private int startSpectral;
  private int endSpectral;
  private int approxHigh;
  private int approxLow;
  private final Map<Integer, JpegScanComponent> components = new HashMap<>();

  public int getNumComponents()
  {
    return numComponents;
  }

  public void setNumComponents(final int numComponents)
  {
    this.numComponents = numComponents;
  }

  public void add(final JpegScanComponent comp)
  {
    components.put(comp.getId(), comp);
  }

  public JpegScanComponent findById(final int id)
  {
    return components.get(id);
  }

  public JpegScanComponent getFirstScan()
  {
    final Collection<JpegScanComponent> coll = components.values();
    return coll.isEmpty() ? null : coll.iterator().next();
  }

  public int getStartSpectral()
  {
    return startSpectral;
  }

  public void setStartSpectral(final int startSpectral)
  {
    this.startSpectral = startSpectral;
  }

  public int getEndSpectral()
  {
    return endSpectral;
  }

  public void setEndSpectral(final int endSpectral)
  {
    this.endSpectral = endSpectral;
  }

  public int getApproxHigh()
  {
    return approxHigh;
  }

  public void setApproxHigh(final int approxHigh)
  {
    this.approxHigh = approxHigh;
  }

  public int getApproxLow()
  {
    return approxLow;
  }

  public void setApproxLow(final int approxLow)
  {
    this.approxLow = approxLow;
  }
}
