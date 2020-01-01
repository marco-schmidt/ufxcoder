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
package ufxcoder.formats.tiff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ufxcoder.formats.FileDescription;

/**
 * {@link FileDescription} extension with TIFF-specific properties.
 *
 * https://wwwimages.adobe.com/content/dam/Adobe/en/products/photoshop/pdfs/dng_spec_1.4.0.0.pdf
 *
 */
public class TiffFileDescription extends FileDescription
{
  /**
   * Is this file in the 64-bit extension format
   * <a href="http://www.awaresystems.be/imaging/tiff/bigtiff.html">BigTIFF</a> (true) or a regular old-style TIFF
   * (false)?
   */
  private boolean big;
  private boolean dng;
  private boolean cr2;
  private final List<ImageFileDirectory> directories = new ArrayList<ImageFileDirectory>();
  private final Set<BigInteger> offsets = new HashSet<BigInteger>();

  public boolean isBig()
  {
    return big;
  }

  public void setBig(final boolean big)
  {
    this.big = big;
  }

  public void add(final ImageFileDirectory ifd)
  {
    directories.add(ifd);
  }

  public int getNumDirectories()
  {
    return directories.size();
  }

  public void addOffset(final BigInteger offset)
  {
    offsets.add(offset);
  }

  public boolean contains(final BigInteger offset)
  {
    return offsets.contains(offset);
  }

  public boolean isDng()
  {
    return dng;
  }

  public void setDng(final boolean dng)
  {
    this.dng = dng;
  }

  public boolean isCr2()
  {
    return cr2;
  }

  public void setCr2(final boolean cr2)
  {
    this.cr2 = cr2;
  }

  public ImageFileDirectory getDirectory(final int index)
  {
    return index >= 0 && index < directories.size() ? directories.get(index) : null;
  }

  @Override
  public void reset()
  {
    super.reset();
    setBig(false);
    setCr2(false);
    setDng(false);
    directories.clear();
    offsets.clear();
  }
}
