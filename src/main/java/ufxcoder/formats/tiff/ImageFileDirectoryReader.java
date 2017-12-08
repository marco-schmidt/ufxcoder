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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.formats.xmp.XmpReader;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Read and parse {@link ImageFileDirectory} metadata.
 */
public class ImageFileDirectoryReader
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ImageFileDirectoryReader.class);
  private final TiffProcessor tiffProcessor;

  public ImageFileDirectoryReader(final TiffProcessor tiffProcessor)
  {
    this.tiffProcessor = tiffProcessor;
  }

  public void readAllMetadata(final BigInteger initialOffset)
  {
    try
    {
      ImageFileDirectory ifd;
      final TiffValidator validator = new TiffValidator(tiffProcessor);
      final TiffFileDescription desc = tiffProcessor.getTiffFileDescription();
      final ImageFileDirectoryReader reader = new ImageFileDirectoryReader(tiffProcessor);
      BigInteger imageFileDirectoryOffset = initialOffset;
      do
      {
        ifd = reader.readImageFileDirectory(tiffProcessor.getSource(), desc.isBig(), imageFileDirectoryOffset);
        desc.add(ifd);
        validator.validate(ifd);
        if (desc.getNumDirectories() == 1 && ifd.findByTag(FieldDescriptionFactory.DNG_VERSION) != null)
        {
          desc.setDng(true);
        }

        imageFileDirectoryOffset = handleContent(ifd, reader, desc);
      }
      while (imageFileDirectoryOffset != null && !imageFileDirectoryOffset.equals(BigInteger.ZERO));

      if (desc.getNumDirectories() == Constants.CR2_IMAGE_FILE_DIRECTORIES)
      {
        ifd = desc.getDirectory(Constants.CR2_IMAGE_FILE_DIRECTORIES - 1);
        final Field cr2Slice = ifd.findByTag(FieldDescriptionFactory.CR2_SLICE_INFORMATION);
        desc.setCr2(cr2Slice != null);
      }
    }
    catch (IOException e)
    {
      LOGGER.error("Unable to read TIFF image file directory.", e);
    }
  }

  private BigInteger handleContent(final ImageFileDirectory ifd, final ImageFileDirectoryReader reader,
      final TiffFileDescription desc)
  {
    List<ImageFileDirectory> ifdList = reader.readSubImageFileDirectories(ifd,
        FieldDescriptionFactory.SUB_IMAGE_FILE_DIRECTORIES);
    for (final ImageFileDirectory sub : ifdList)
    {
      ifd.addSubImageFileDirectory(sub);
    }
    ifdList = reader.readSubImageFileDirectories(ifd, FieldDescriptionFactory.GPS);
    if (!ifdList.isEmpty())
    {
      ifd.setGpsInfo(ifdList.get(0));
    }
    reader.parseXmp(desc, ifd.findByTag(FieldDescriptionFactory.XMP));

    final BigInteger imageFileDirectoryOffset = ifd.getNextImageFileDirectoryOffset();
    if (desc.contains(imageFileDirectoryOffset))
    {
      LOGGER.error(tiffProcessor.msg("tiff.error.image_file_directory_repeated", imageFileDirectoryOffset,
          desc.getAbsolutePath()));
    }
    else
    {
      desc.addOffset(imageFileDirectoryOffset);
    }

    return imageFileDirectoryOffset;
  }

  private void parseXmp(final TiffFileDescription desc, final Field xmp)
  {
    if (xmp != null)
    {
      final byte[] data = xmp.getData();
      final XmpReader reader = new XmpReader(tiffProcessor);
      if (!reader.parseXmp(data))
      {
        desc.addErrorMessage("todo");
      }
    }
  }

  public ImageFileDirectory readImageFileDirectory(final SeekableSource source, final boolean big,
      final BigInteger imageFileDirectoryOffset) throws IOException
  {
    source.seek(imageFileDirectoryOffset.longValue());

    LOGGER.debug(String.format("%d IFD #%d", imageFileDirectoryOffset,
        tiffProcessor.getTiffFileDescription().getNumDirectories() + 1));
    final int countSize = big ? 8 : 2;
    final Segment rawIfd = tiffProcessor.read(countSize);
    final long numTags = big ? rawIfd.int64() : rawIfd.int16();
    final ImageFileDirectory ifd = new ImageFileDirectory(imageFileDirectoryOffset);
    ifd.setNumTags(numTags);

    if (numTags < Constants.MIN_NUMBER_OF_ENTRIES_PER_IFD)
    {
      tiffProcessor.addErrorMessage(tiffProcessor.msg("tiff.error.too_few_tags", numTags));
    }
    else
    {
      parseImageFileDirectory(big, numTags, rawIfd, ifd);
    }
    return ifd;
  }

  private void parseImageFileDirectory(final boolean big, final long numTags, final Segment rawIfd,
      final ImageFileDirectory ifd) throws IOException
  {
    long tagDataSize;
    if (big)
    {
      tagDataSize = numTags * Constants.TAG_SIZE_BIG + Constants.OFFSET_SIZE_BIG;
    }
    else
    {
      tagDataSize = numTags * Constants.TAG_SIZE_REGULAR + Constants.OFFSET_SIZE_REGULAR;
    }
    tiffProcessor.append(rawIfd, (int) tagDataSize);

    final FieldReader reader = new FieldReader(tiffProcessor);
    final TiffValidator val = new TiffValidator(tiffProcessor);
    for (long i = 0; i < numTags; i++)
    {
      final Field field = reader.parseField(big, rawIfd);
      val.validate(field);
      ifd.add(field);
    }
    ifd.setNextImageFileDirectoryOffset(rawIfd.bigInt(big ? 8 : 4));
  }

  /**
   * If argument image file directory contains a sub image file directory field it is being loaded.
   *
   * @param ifd
   *          image file directory
   * @param fieldDesc
   *          description of field pointing to sub image file directory
   * @return list with image file directories, one per offset value in the field
   */
  public List<ImageFileDirectory> readSubImageFileDirectories(final ImageFileDirectory ifd,
      final FieldDescription fieldDesc)
  {
    final List<ImageFileDirectory> result = new ArrayList<ImageFileDirectory>();
    final Field field = ifd == null ? null : ifd.findByTag(fieldDesc == null ? 0 : fieldDesc.getTag());
    final SeekableSource input = tiffProcessor.getSource();
    if (field != null)
    {
      final long numValues = field.getNumValues();
      for (int i = 0; i < numValues; i++)
      {
        final Number offset = field.getAsNumber(i);
        if (offset != null)
        {
          final BigInteger numericOffset = BigInteger.valueOf(offset.longValue());
          if (tiffProcessor.isValidSourceOffset(numericOffset))
          {
            try
            {
              final ImageFileDirectory sub = readImageFileDirectory(input,
                  tiffProcessor.getTiffFileDescription().isBig(), numericOffset);
              result.add(sub);
            }
            catch (IOException e)
            {
              LOGGER.error(
                  String.format("Error reading image file directory pointed to at offset %d.", field.getAsNumber()), e);
            }
          }
        }
      }
    }
    return result;
  }
}
