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
import java.util.List;
import ufxcoder.conversion.Array;

/**
 * A single entry of an image file directory, describing one property of one image in a TIFF file.
 */
public class Field
{
  private static final int MAX_PRINT_ELEMENTS = 48;
  private int id;
  /**
   * Number representing one of the predefined types.
   *
   * @see FieldType
   */
  private int type;
  private long numValues;
  /**
   * File offset from which this value was read.
   */
  private long offset;
  private BigInteger additionalOffset;
  private byte[] data;
  private final List<Object> values = new ArrayList<Object>();

  public int getId()
  {
    return id;
  }

  public void setId(final int id)
  {
    this.id = id;
  }

  public int getType()
  {
    return type;
  }

  public void setType(final int type)
  {
    this.type = type;
  }

  public long getNumValues()
  {
    return numValues;
  }

  public void setNumValues(final long numValues)
  {
    this.numValues = numValues;
  }

  public long getOffset()
  {
    return offset;
  }

  public void setOffset(final long offset)
  {
    this.offset = offset;
  }

  public byte[] getData()
  {
    return Array.clone(data);
  }

  public void setData(final byte[] data)
  {
    this.data = Array.clone(data);
  }

  public void add(final Object obj)
  {
    values.add(obj);
  }

  public Number getAsNumber()
  {
    return getAsNumber(0);
  }

  public Number getAsNumber(final int index)
  {
    Number result = null;
    if (index >= 0 && index < values.size())
    {
      final Object object = values.get(index);
      if (object instanceof Number)
      {
        result = (Number) object;
      }
    }
    return result;
  }

  public BigInteger getAsBigInteger()
  {
    return getAsBigInteger(0);
  }

  public BigInteger getAsBigInteger(final int index)
  {
    BigInteger result = null;
    if (index >= 0 && index < values.size())
    {
      final Object object = values.get(index);
      if (object instanceof BigInteger)
      {
        result = (BigInteger) object;
      }
      else
      {
        if (object instanceof Number)
        {
          final Number number = (Number) object;
          result = BigInteger.valueOf(number.longValue());
        }
      }
    }
    return result;
  }

  public int getAsInt()
  {
    return getAsInt(0);
  }

  public int getAsInt(final int index)
  {
    final Number result = getAsNumber(index);
    return result == null ? 0 : result.intValue();
  }

  public long getAsLong()
  {
    return getAsLong(0);
  }

  public long getAsLong(final int index)
  {
    final Number result = getAsNumber(index);
    return result == null ? 0 : result.longValue();
  }

  public String getAsString()
  {
    final StringBuilder result = new StringBuilder();
    final boolean isChar = type == FieldType.Char.getId();
    int counter = 0;
    for (final Object obj : values)
    {
      if (!isChar && result.length() > 0)
      {
        result.append(' ');
      }
      if (obj == null)
      {
        result.append('?');
      }
      else
      {
        result.append(obj.toString());
      }
      counter++;
      if (counter == MAX_PRINT_ELEMENTS)
      {
        result.append(" ...");
        break;
      }
    }
    return result.toString();
  }

  public BigInteger getAdditionalOffset()
  {
    return additionalOffset;
  }

  public void setAdditionalOffset(final BigInteger additionalOffset)
  {
    this.additionalOffset = additionalOffset;
  }
}
