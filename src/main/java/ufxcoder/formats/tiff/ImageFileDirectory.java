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
package ufxcoder.formats.tiff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List of tags describing a single image in the file.
 */
public class ImageFileDirectory
{
  private long numTags;
  private final List<Field> fields = new ArrayList<Field>();
  private final Map<Integer, Field> mapTagToFields = new HashMap<Integer, Field>();
  private BigInteger nextImageFileDirectoryOffset;
  private final BigInteger offset;
  private final List<ImageFileDirectory> subs = new ArrayList<ImageFileDirectory>();
  private ImageFileDirectory gpsInfo;

  public ImageFileDirectory(final BigInteger offset)
  {
    this.offset = offset;
  }

  public long getNumTags()
  {
    return numTags;
  }

  public void setNumTags(final long numTags)
  {
    this.numTags = numTags;
  }

  public void add(final Field field)
  {
    fields.add(field);
    mapTagToFields.put(field.getId(), field);
  }

  public Field findByTag(final FieldDescription desc)
  {
    return findByTag(desc.getTag());
  }

  public Field findByTag(final int tag)
  {
    return mapTagToFields.get(tag);
  }

  public long findSingleNumberByTag(final FieldDescription desc)
  {
    return findSingleNumberByTag(desc.getTag());
  }

  public long findSingleNumberByTag(final int tag)
  {
    return findSingleNumberByTag(mapTagToFields.get(tag));
  }

  public long findSingleNumberByTag(final Field field)
  {
    long result = -1;
    if (field != null && field.getNumValues() == 1)
    {
      result = field.getAsLong();
    }
    return result;
  }

  public Field get(final int index)
  {
    Field result = null;
    if (index >= 0 && index < fields.size())
    {
      result = fields.get(index);
    }
    return result;
  }

  public BigInteger getNextImageFileDirectoryOffset()
  {
    return nextImageFileDirectoryOffset;
  }

  public void setNextImageFileDirectoryOffset(final BigInteger nextImageFileDirectoryOffset)
  {
    this.nextImageFileDirectoryOffset = nextImageFileDirectoryOffset;
  }

  public List<Field> getFields()
  {
    return fields;
  }

  public BigInteger getOffset()
  {
    return offset;
  }

  public void addSubImageFileDirectory(final ImageFileDirectory sub)
  {
    subs.add(sub);
  }

  public ImageFileDirectory getGpsInfo()
  {
    return gpsInfo;
  }

  public void setGpsInfo(final ImageFileDirectory gpsInfo)
  {
    this.gpsInfo = gpsInfo;
  }
}
