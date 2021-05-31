/*
 * Copyright 2017, 2018, 2019, 2020, 2021 the original author or authors.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A single entry of a TIFF image file directory.
 */
public class FieldDescription
{
  private static Map<Integer, FieldDescription> mapByTag;
  private final int tag;
  private final Set<FieldType> allowedTypes = new HashSet<FieldType>();
  private final Comparable<?> maximum;
  private final Comparable<?> minimum;
  private final Object defaultValue;
  private final Number maximumCount;
  private final Number minimumCount;
  private final boolean mandatory;

  public static FieldDescription findByTag(final int tag)
  {
    synchronized (FieldDescription.class)
    {
      if (mapByTag == null)
      {
        final List<FieldDescription> descriptions = FieldDescriptionFactory.getDescriptions();
        mapByTag = new HashMap<Integer, FieldDescription>(descriptions.size());
        for (final FieldDescription desc : descriptions)
        {
          mapByTag.put(desc.tag, desc);
        }
      }
      return mapByTag.get(tag);
    }
  }

  public FieldDescription(final int tag, final Set<FieldType> allowed, final Comparable<?> min, final Comparable<?> max,
      final int minCount, final int maxCount, final Object defaultValue, final boolean mandatory)
  {
    this.tag = tag;
    maximumCount = Integer.valueOf(maxCount);
    minimumCount = Integer.valueOf(minCount);
    this.defaultValue = defaultValue;
    this.mandatory = mandatory;
    maximum = max;
    minimum = min;
    if (allowed != null)
    {
      allowedTypes.addAll(allowed);
    }
  }

  public void addAllowedType(final FieldType type)
  {
    allowedTypes.add(type);
  }

  public boolean isAllowed(final FieldType type)
  {
    return allowedTypes.contains(type);
  }

  public int getTag()
  {
    return tag;
  }

  public Comparable<?> getMaximum()
  {
    return maximum;
  }

  public Comparable<?> getMinimum()
  {
    return minimum;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  public Number getMaximumCount()
  {
    return maximumCount;
  }

  public Number getMinimumCount()
  {
    return minimumCount;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }
}
