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
package ufxcoder.formats.tiff;

import java.io.IOException;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.conversion.Array;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Parse TIFF fields, the entries of an image file directory.
 */
public class FieldReader
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FieldReader.class);
  private final TiffProcessor processor;

  public FieldReader(final TiffProcessor proc)
  {
    processor = proc;
  }

  /**
   * Parse a single field from the current position within the argument segment.
   *
   * @param big
   *          is underlying Tiff regular (false) or big (true)
   * @param rawIfd
   *          segment to read from
   * @return new Field object
   * @throws IOException
   *           on errors accessing the segment
   */
  public Field parseField(final boolean big, final Segment rawIfd) throws IOException
  {
    final Field field = new Field();
    field.setOffset(rawIfd.getOffset() + rawIfd.getIndex());
    field.setId(rawIfd.int16());
    field.setType(rawIfd.int16());

    long count;
    byte[] data;
    if (big)
    {
      count = rawIfd.int64();
      data = rawIfd.getData(8);
    }
    else
    {
      count = rawIfd.int32();
      data = rawIfd.getData(4);
    }
    field.setNumValues(count);
    field.setData(data);

    // if data did not fit into IFD entry load it (unless in mode "identify")
    if (!processor.isIdentify())
    {
      if (loadAdditionalData(field))
      {
        parseFieldData(field, rawIfd.getByteOrder());
      }
      log(field);
    }

    return field;
  }

  private void log(final Field field)
  {
    if (LOGGER.isDebugEnabled())
    {
      final FieldType valueType = FieldType.findById(field.getType());
      final String tagKey = "tiff.field." + field.getId();
      final String tagName = processor.msg(tagKey);
      String value;
      if (valueType != null && valueType.isIntegerNumber() && field.getNumValues() == 1)
      {
        final String formattedValue = field.getAsString();
        final String valueKey = tagKey + "." + formattedValue;
        value = processor.hasMsg(valueKey) ? processor.msg(valueKey) : formattedValue;
      }
      else
      {
        value = field.getAsString();
      }
      LOGGER.debug(String.format("%d %d (%s) %d (%s) %d [%s]", field.getOffset(), field.getId(), tagName,
          field.getType(), valueType == null ? "" : valueType.getName(), field.getNumValues(), value));
    }
  }

  /**
   * Check if data fits into offset value and if it does not load data from another section of the file.
   *
   * @param field
   *          the TIFF field to check
   * @return whether loading was successful
   * @throws IOException
   *           if loading data failed
   */
  private boolean loadAdditionalData(final Field field) throws IOException
  {
    boolean result;
    final FieldType valueType = FieldType.findById(field.getType());
    if (valueType == null)
    {
      processor.error("tiff.error.unknown_tiff_field_type", field.getId());
      result = false;
    }
    else
    {
      final long dataSize = field.getNumValues() * valueType.getSize();
      final TiffFileDescription desc = processor.getTiffFileDescription();
      final boolean big = desc.isBig();
      final boolean fits = dataSize <= (big ? Constants.OFFSET_SIZE_BIG : Constants.OFFSET_SIZE_REGULAR);
      final byte[] data = field.getData();
      if (fits)
      {
        result = true;
      }
      else
      {
        final ByteOrder byteOrder = desc.getByteOrder();
        final BigInteger offset = Array.toBigInteger(data, byteOrder);
        processor.checkImageFileDirectoryOffset(offset);
        final SeekableSource source = processor.getSource();
        if (source.isValidSection(offset, BigInteger.valueOf(dataSize)))
        {
          field.setAdditionalOffset(offset);
          if (dataSize <= Constants.MAX_FIELD_DATA_ALLOCATION_SIZE)
          {
            source.seek(offset);
            final byte[] buffer = new byte[(int) dataSize];
            source.readFully(buffer, 0, buffer.length);
            field.setData(buffer);
            result = true;
          }
          else
          {
            field.setData(null);
            result = false;
          }
        }
        else
        {
          processor.error(Msg.INVALID_OFFSET_AND_SIZE, offset, dataSize);
          result = false;
        }
      }
    }
    return result;
  }

  private void parseFieldData(final Field field, final ByteOrder byteOrder)
  {
    final FieldType type = FieldType.findById(field.getType());
    if (type == null)
    {
      processor.error(Msg.INVALID_FIELD_TYPE, field.getType());
    }
    else
    {
      final byte[] data = field.getData();
      int offset = 0;
      for (long i = 0; i < field.getNumValues(); i++)
      {
        final Object value = parse(data, offset, type, byteOrder);
        field.add(value);
        offset += type.getSize();
      }
    }
  }

  private Object parse(final byte[] data, final int offset, final FieldType type, final ByteOrder byteOrder)
  {
    Object result;
    switch (type)
    {
    case Byte:
    {
      result = Integer.valueOf(data[offset] & 0xff);
      break;
    }
    case Char:
    {
      result = Character.valueOf((char) data[offset]);
      break;
    }
    case Short:
    {
      result = Integer.valueOf(Array.from16(data, offset, byteOrder));
      break;
    }
    case Long:
    {
      result = Array.toBigInteger(data, offset, 4, byteOrder);
      break;
    }
    case Rational:
    {
      final Integer numerator = Integer.valueOf(Array.from32(data, offset, byteOrder));
      final Integer denominator = Integer.valueOf(Array.from32(data, offset + FieldType.Long.getSize(), byteOrder));
      if (denominator.equals(0))
      {
        if (numerator.equals(0))
        {
          // 0/0 means "undefined"
          result = 0;
        }
        else
        {
          processor.error("tiff.error.validation.denominator_zero");
          result = numerator;
        }
      }
      else
      {
        result = numerator / denominator;
      }
      break;
    }
    default:
    {
      result = null;
      break;
    }
    }
    return result;
  }
}
