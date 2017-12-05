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

import java.io.IOException;
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

    loadAdditionalData(field);
    parseFieldData(field, rawIfd.getByteOrder());
    log(field);

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
   * @throws IOException
   *           if loading data failed
   */
  private void loadAdditionalData(final Field field) throws IOException
  {
    final FieldType valueType = FieldType.findById(field.getType());
    if (valueType == null)
    {
      processor.addErrorMessage(processor.msg("tiff.error.unknown_tiff_field_type", field.getId()));
    }
    else
    {
      final long dataSize = field.getNumValues() * valueType.getSize();
      final TiffFileDescription desc = processor.getTiffFileDescription();
      final boolean big = desc.isBig();
      final boolean fits = dataSize <= (big ? Constants.OFFSET_SIZE_BIG : Constants.OFFSET_SIZE_REGULAR);
      final byte[] data = field.getData();
      if (!fits)
      {
        final ByteOrder byteOrder = desc.getByteOrder();
        final long offset = big ? Array.from64(data, 0, byteOrder) : Array.from32(data, 0, byteOrder);
        if (offset % 2 != 0)
        {
          final String msg = processor.getConfig().msg("tiff.error.validation.odd_offset", offset);
          processor.addErrorMessage(msg);
        }
        final SeekableSource source = processor.getSource();
        source.seek(offset);
        final byte[] buffer = new byte[(int) dataSize];
        source.readFully(buffer, 0, buffer.length);
        field.setData(buffer);
      }
    }
  }

  private void parseFieldData(final Field field, final ByteOrder byteOrder)
  {
    final FieldType type = FieldType.findById(field.getType());
    final byte[] data = field.getData();
    int offset = 0;
    for (long i = 0; i < field.getNumValues(); i++)
    {
      final Object value = parse(data, offset, type, byteOrder);
      field.add(value);
      offset += type.getSize();
    }
  }

  private Object parse(final byte[] data, final int offset, final FieldType type, final ByteOrder byteOrder)
  {
    Object result;
    switch (type)
    {
    case Byte:
    {
      result = Byte.valueOf(data[offset]);
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
      result = Integer.valueOf(Array.from32(data, offset, byteOrder));
      break;
    }
    case Rational:
    {
      final Integer numerator = Integer.valueOf(Array.from32(data, offset, byteOrder));
      final Integer denominator = Integer.valueOf(Array.from32(data, offset + FieldType.Long.getSize(), byteOrder));
      if (denominator.equals(0))
      {
        processor.addErrorMessage(processor.msg("tiff.error.validation.denominator_zero"));
        result = numerator;
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
