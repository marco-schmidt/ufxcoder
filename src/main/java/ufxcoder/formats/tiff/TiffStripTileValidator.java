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
import ufxcoder.app.AppConfig;
import ufxcoder.io.SeekableSource;

/**
 * Validator for Tiff strips and tiles.
 */
public class TiffStripTileValidator
{
  private final AppConfig config;
  private final TiffProcessor proc;

  public TiffStripTileValidator(final AppConfig config, final TiffProcessor proc)
  {
    this.config = config;
    this.proc = proc;
  }

  /**
   * Count the number of null arguments.
   *
   * @param fields
   *          sequence of Field objects, each possibly null
   * @return number of null values
   */
  private int countNull(final Field... fields)
  {
    int result = 0;
    for (final Field field : fields)
    {
      if (field != null)
      {
        result++;
      }
    }
    return result;
  }

  /**
   * Check strip and tile declarations.
   *
   * @param ifd
   *          {@link ImageFileDirectory} for which strips and tiles are to be checked
   */
  public void checkStripsAndTiles(final ImageFileDirectory ifd)
  {
    final Field stripOffsets = ifd.findByTag(FieldDescriptionFactory.STRIP_OFFSETS);
    final Field stripByteCounts = ifd.findByTag(FieldDescriptionFactory.STRIP_BYTE_COUNTS);
    Field rowsPerStrip = ifd.findByTag(FieldDescriptionFactory.ROWS_PER_STRIP);
    if (rowsPerStrip == null && stripOffsets != null && stripOffsets.getNumValues() == 1)
    {
      final Field height = ifd.findByTag(FieldDescriptionFactory.IMAGE_LENGTH);
      if (height != null)
      {
        rowsPerStrip = new Field();
        rowsPerStrip.setNumValues(1);
        rowsPerStrip.add(height.getAsNumber());
      }
    }
    final Field[] stripFields = new Field[]
    {
        stripOffsets, stripByteCounts, rowsPerStrip
    };
    final int numStripFields = countNull(stripFields);

    final Field tileWidth = ifd.findByTag(FieldDescriptionFactory.TILE_WIDTH);
    final Field tileHeight = ifd.findByTag(FieldDescriptionFactory.TILE_LENGTH);
    final Field tileOffsets = ifd.findByTag(FieldDescriptionFactory.TILE_OFFSETS);
    final Field tileByteCounts = ifd.findByTag(FieldDescriptionFactory.TILE_BYTE_COUNTS);
    final Field[] tileFields = new Field[]
    {
        tileWidth, tileHeight, tileOffsets, tileByteCounts
    };
    final int numTileFields = countNull(tileFields);

    if (numStripFields == 0)
    {
      if (numTileFields == tileFields.length)
      {
        checkTiles(ifd, tileWidth, tileHeight, tileOffsets, tileByteCounts);
      }
      else
      {
        final String msg = config.msg("tiff.error.validation.some_tile_fields_missing", Long.toString(ifd.getOffset()));
        proc.getFileDescription().addErrorMessage(msg);
      }
    }
    else
    {
      if (numStripFields == stripFields.length)
      {
        if (numTileFields == 0)
        {
          checkStrips(ifd, rowsPerStrip, stripOffsets, stripByteCounts);
        }
        else
        {
          final String msg = config.msg("tiff.error.validation.all_strip_and_some_tile_fields",
              Long.toString(ifd.getOffset()));
          proc.getFileDescription().addErrorMessage(msg);
        }
      }
      else
      {
        final String msg = config.msg("tiff.error.validation.only_some_strip_fields", Long.toString(ifd.getOffset()));
        proc.getFileDescription().addErrorMessage(msg);
      }
    }
  }

  private long toLong(final Field field)
  {
    return field == null ? 0 : field.getAsNumber().longValue();
  }

  private void checkStrips(final ImageFileDirectory ifd, final Field rowsPerStrip, final Field stripOffsets,
      final Field stripByteCounts)
  {
    final long offsets = stripOffsets.getNumValues();
    final long counts = stripByteCounts.getNumValues();
    if (offsets != counts)
    {
      final String msg = config.msg("tiff.error.validation.number_of_strip_offsets_and_byte_counts_differ",
          Long.toString(ifd.getOffset()), offsets, counts);
      proc.getFileDescription().addErrorMessage(msg);
    }

    final long rows = toLong(rowsPerStrip);
    if (rows > 0)
    {
      final long height = toLong(ifd.findByTag(FieldDescriptionFactory.IMAGE_LENGTH.getTag()));
      final long requiredStrips = (height + rows - 1) / rows;
      if (requiredStrips < offsets)
      {
        final String msg = config.msg("tiff.error.validation.too_few_strips", Long.toString(ifd.getOffset()),
            requiredStrips, offsets, height, rows);
        proc.getFileDescription().addErrorMessage(msg);
      }
    }

    checkImageDataSegments(ifd, stripOffsets, stripByteCounts);
  }

  private void checkTiles(final ImageFileDirectory ifd, final Field tileWidth, final Field tileHeight,
      final Field tileOffsets, final Field tileByteCounts)
  {
    final long imageHeight = toLong(ifd.findByTag(FieldDescriptionFactory.IMAGE_LENGTH));
    final long imageWidth = toLong(ifd.findByTag(FieldDescriptionFactory.IMAGE_WIDTH));

    // TIFF6.pdf p. 67
    final long width = toLong(tileWidth);
    if (width % 16 != 0)
    {
      final String msg = config.msg("tiff.error.validation.tile_width_not_multiple_of_16",
          Long.toString(ifd.getOffset()), width);
      proc.getFileDescription().addErrorMessage(msg);
    }

    final long height = toLong(tileHeight);
    if (height % 16 != 0)
    {
      final String msg = config.msg("tiff.error.validation.tile_length_not_multiple_of_16",
          Long.toString(ifd.getOffset()), height);
      proc.getFileDescription().addErrorMessage(msg);
    }

    final long offsets = toLong(tileOffsets);
    final long counts = toLong(tileByteCounts);
    if (offsets != counts)
    {
      final String msg = config.msg("tiff.error.validation.number_of_tile_offsets_and_byte_counts_differ",
          Long.toString(ifd.getOffset()), offsets, counts);
      proc.getFileDescription().addErrorMessage(msg);
    }

    final long horiz = (imageWidth + width - 1) / width;
    final long vert = (imageHeight + height - 1) / height;
    final long expectedTiles = horiz * vert;
    if (offsets != expectedTiles)
    {
      final String msg = config.msg("tiff.error.validation.number_of_expected_and_actual_tiles_differ",
          Long.toString(ifd.getOffset()), expectedTiles, offsets);
      proc.getFileDescription().addErrorMessage(msg);
    }

    checkImageDataSegments(ifd, tileOffsets, tileByteCounts);
  }

  /**
   * Checks validity of offsets and byte counts, can be used for both strips and tiles.
   *
   * @param ifd
   *          image file directory to be checked
   * @param offsets
   *          integer file offsets for all strips/tiles
   * @param byteCounts
   *          integer number of bytes for all strips/tiles
   */
  public void checkImageDataSegments(final ImageFileDirectory ifd, final Field offsets, final Field byteCounts)
  {
    final SeekableSource source = proc.getSource();
    try
    {
      final long fileSize = source.getLength();
      final long numValues = offsets.getNumValues();
      for (int index = 0; index < numValues; index++)
      {
        final Number offsetNumber = offsets.getAsNumber(index);
        final long offset = offsetNumber.longValue();
        final Number countNumber = byteCounts.getAsNumber(index);
        final long count = countNumber.longValue();
        if (offset >= fileSize)
        {
          final String msg = config.msg("tiff.error.validation.offset_outside_file", Long.toString(ifd.getOffset()),
              offset, fileSize);
          proc.getFileDescription().addErrorMessage(msg);
        }
        else
        {
          final long lastByteOffset = offset + count;
          if (lastByteOffset > fileSize)
          {
            final String msg = config.msg("tiff.error.validation.end_of_strip_or_tile_outside_file",
                Long.toString(ifd.getOffset()), lastByteOffset, fileSize);
            proc.getFileDescription().addErrorMessage(msg);
          }
        }
      }
    }
    catch (IOException e)
    {
      final String msg = config.msg("tiff.error.validation.unable_to_determine_source_size", source.getName(),
          e.getMessage());
      proc.getFileDescription().addErrorMessage(msg);
    }
  }
}
