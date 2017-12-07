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
import java.util.List;
import ufxcoder.app.AppConfig;
import ufxcoder.formats.FileDescription;

/**
 * Validate various parts of a TIFF file.
 */
public class TiffValidator
{
  private final TiffProcessor proc;
  private final AppConfig config;

  public TiffValidator(final TiffProcessor processor)
  {
    proc = processor;
    config = proc.getConfig();
  }

  public void validate(final Field field)
  {
    final FieldDescription desc = FieldDescription.findByTag(field.getId());
    if (desc != null)
    {
      validateType(field, desc);
      validateMinMax(field, desc);
      validateNumber(field, desc);
    }
    if (field.getType() == FieldType.Char.getId())
    {
      validateCharacters(field);
    }
  }

  private void validateCharacters(final Field field)
  {
    final byte[] data = field.getData();
    if (data != null && data.length > 0)
    {
      // count non-ASCII characters in all bytes except for the last one
      int numNonAscii = 0;
      for (int i = 0; i < data.length - 1; i++)
      {
        final int value = data[i] & 0xff;
        if (value > 127)
        {
          numNonAscii++;
        }
      }
      if (numNonAscii > 0)
      {
        proc.addErrorMessage(config.msg("tiff.error.validation.non_ascii_characters", numNonAscii));
      }

      // last byte must be zero
      if (data[data.length - 1] != 0)
      {
        proc.addErrorMessage(config.msg("tiff.error.validation.characters_not_zero_terminated"));
      }
    }
  }

  private void validateNumber(final Field field, final FieldDescription desc)
  {
    final long numValues = field.getNumValues();
    final Number minimumCount = desc.getMinimumCount();
    if (minimumCount != null && numValues < minimumCount.longValue())
    {
      proc.addErrorMessage(
          proc.msg("tiff.error.field_has_too_few_values", field.getId(), numValues, minimumCount.longValue()));
    }
    final Number maximumCount = desc.getMaximumCount();
    if (maximumCount != null && numValues > maximumCount.longValue())
    {
      proc.addErrorMessage(
          proc.msg("tiff.error.field_has_too_many_values", field.getId(), numValues, maximumCount.longValue()));
    }
  }

  private void validateMinMax(final Field field, final FieldDescription desc)
  {
    final Double min = toDouble(desc.getMinimum(), Double.NEGATIVE_INFINITY);
    final Double max = toDouble(desc.getMaximum(), Double.POSITIVE_INFINITY);

    for (int index = 0; index < field.getNumValues(); index++)
    {
      final Number value = field.getAsNumber(index);
      if (value != null)
      {
        final Double doubleValue = Double.valueOf(value.doubleValue());
        if (min.compareTo(doubleValue) > 0)
        {
          proc.addErrorMessage(proc.msg("tiff.error.value_smaller_than_minimum", field.getId(), value, min));
        }
        if (max.compareTo(doubleValue) < 0)
        {
          proc.addErrorMessage(proc.msg("tiff.error.value_larger_than_maximum", field.getId(), value, max));
        }
      }
    }
  }

  private Double toDouble(final Comparable<?> value, final Double defaultValue)
  {
    double doubl = defaultValue;
    if (value instanceof Number)
    {
      doubl = ((Number) value).doubleValue();
    }
    return Double.valueOf(doubl);
  }

  private void validateType(final Field field, final FieldDescription desc)
  {
    if (field != null && desc != null)
    {
      final int fieldTypeId = field.getType();
      final FieldType type = FieldType.findById(fieldTypeId);
      if (type == null)
      {
        proc.addErrorMessage(proc.msg("tiff.error.unknown_field_type", fieldTypeId));
      }
      else
      {
        if (!desc.isAllowed(type))
        {
          proc.addErrorMessage(proc.msg("tiff.error.incorrect_field_type", field.getId(), fieldTypeId));
        }
      }
    }
  }

  private void validateDateTime(final ImageFileDirectory ifd)
  {
    validateDateTime(ifd.findByTag(FieldDescriptionFactory.DATE_TIME));
    validateDateTime(ifd.findByTag(FieldDescriptionFactory.DATE_TIME_DIGITIZED));
    validateDateTime(ifd.findByTag(FieldDescriptionFactory.DATE_TIME_ORIGINAL));
  }

  private void validateDateTime(final Field dateTime)
  {
    if (dateTime != null)
    {
      final String str = dateTime.getAsString();
      final TiffDateTime tiffDateTime = new TiffDateTime();
      if (tiffDateTime.parseDateTimeString(str) == null)
      {
        proc.addErrorMessage(proc.msg("tiff.error.invalid_date_time", str));
      }
    }
  }

  /**
   * Make sure that the fields of an image file directory are stored in strict ascending order of their tags.
   *
   * @param ifd
   *          image file directory to check
   */
  public void checkFieldOrder(final ImageFileDirectory ifd)
  {
    final List<Field> fields = ifd.getFields();
    final FileDescription desc = proc.getFileDescription();
    for (int index = 0; index < fields.size() - 1; index++)
    {
      final Field field1 = fields.get(index);
      final Field field2 = fields.get(index + 1);
      final int tag1 = field1.getId();
      final int tag2 = field2.getId();
      if (tag1 >= tag2)
      {
        final String msg = config.msg("tiff.error.validation.image_file_directory_entries_order", ifd.getOffset(),
            Integer.toString(index + 1), Integer.toString(tag1), Integer.toString(index + 2), Integer.toString(tag2));
        desc.addErrorMessage(msg);
      }
    }
  }

  private void checkImageFileDirectoryOffset(final BigInteger imageFileDirectoryOffset)
  {
    if (imageFileDirectoryOffset.testBit(0))
    {
      final String msg = proc.getConfig().msg("tiff.error.validation.odd_image_file_directory_offset",
          imageFileDirectoryOffset);
      proc.addErrorMessage(msg);
    }
  }

  public boolean isThumbnailIfd(final ImageFileDirectory ifd)
  {
    boolean result = false;
    if (ifd.getNumTags() == 2)
    {
      final Field field1 = ifd.get(0);
      final Field field2 = ifd.get(1);
      result = field1.getId() == FieldDescriptionFactory.JPEG_INTERCHANGE_FORMAT.getTag()
          && field2.getId() == FieldDescriptionFactory.JPEG_INTERCHANGE_FORMAT_LENGTH.getTag();
    }
    return result;
  }

  public void validate(final ImageFileDirectory ifd)
  {
    checkImageFileDirectoryOffset(ifd.getOffset());
    checkFieldOrder(ifd);
    for (final Field field : ifd.getFields())
    {
      validate(field);
    }
    validateDateTime(ifd);
    final boolean thumbnail = isThumbnailIfd(ifd);
    if (!thumbnail)
    {
      final TiffStripTileValidator stVal = new TiffStripTileValidator(config, proc);
      stVal.checkStripsAndTiles(ifd);
    }
    final TiffColorValidator colorValidator = new TiffColorValidator(proc);
    colorValidator.checkSamples(ifd);
  }
}
