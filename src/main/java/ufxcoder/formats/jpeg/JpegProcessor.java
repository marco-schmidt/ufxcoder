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
package ufxcoder.formats.jpeg;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.conversion.ByteOrder;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.FileDescription;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Process JPEG image files.
 */
public class JpegProcessor extends AbstractFormatProcessor
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JpegProcessor.class);

  @Override
  public FileDescription createDescription()
  {
    return new JpegFileDescription();
  }

  public JpegFileDescription getJpegFileDescription()
  {
    JpegFileDescription result = null;
    final FileDescription desc = getFileDescription();
    if (desc instanceof JpegFileDescription)
    {
      result = (JpegFileDescription) desc;
    }
    return result;
  }

  private void identify(final Marker marker)
  {
    try
    {
      readMarker(marker);
    }
    catch (IOException e)
    {
      error(Msg.CANNOT_READ_HEADER, e);
    }
  }

  @Override
  public void process()
  {
    final JpegFileDescription desc = new JpegFileDescription();
    setFileDescription(desc);

    final Marker startOfImage = new Marker();
    startOfImage.setNumber(1);
    identify(startOfImage);

    if (isFormatIdentified() && !isIdentify())
    {
      processInput();
    }

    closeSource();
  }

  /**
   * Read a two-byte marker and possibly a two-byte length value.
   *
   * @param marker
   *          data structure to add read integer values to
   * @throws IOException
   *           if there are problems seeking or reading
   */
  private void readMarker(final Marker marker) throws IOException
  {
    Segment segm;
    segm = read(2);
    segm.setByteOrder(ByteOrder.BigEndian);
    marker.setSegment(segm);
    int markerId = segm.int16();
    if ((markerId & Constants.MARKER_MASK) == Constants.MARKER_MASK)
    {
      // skip additional 0xff bytes
      while (markerId == 0xffff)
      {
        append(segm, 1);
        markerId = Constants.MARKER_MASK | segm.int8();
      }
      marker.setId(markerId);
      if (marker.getId() == Constants.MARKER_START_OF_IMAGE)
      {
        if (marker.getNumber() == 1)
        {
          setFormatIdentified(true);
        }
        else
        {
          error(Msg.SOI_FIRST_MARKER_ONLY);
        }
      }
      else
      {
        if (marker.getNumber() == 1)
        {
          error(Msg.FIRST_MARKER_NOT_SOI);
        }
      }
      if (isSuccess() && hasLength(markerId))
      {
        append(segm, 2);
        final int length = segm.int16();
        marker.setLength(length);
      }
    }
    else
    {
      error(Msg.INVALID_MARKER, markerId, getSource().getPosition() - 2);
    }
    // return offset;
  }

  /**
   * Determine if the argument marker has a length value associated with it.
   *
   * @param marker
   *          the marker to be examined
   * @return true if there is a length value, false otherwise
   */
  private boolean hasLength(final int marker)
  {
    return marker != Constants.MARKER_START_OF_IMAGE && marker != Constants.MARKER_END_OF_IMAGE;
  }

  private void processInput()
  {
    final JpegFileDescription desc = getJpegFileDescription();

    // read markers until there is an I/O exception or an error
    int number = 2;
    try
    {
      Marker marker;
      do
      {
        marker = new Marker();
        marker.setNumber(number++);
        readMarker(marker);
        if (isSuccess())
        {
          handleMarker(marker);
        }
        desc.add(marker);
      }
      while (isSuccess() && marker.getId() != Constants.MARKER_END_OF_IMAGE);
      checkForExtraneousData();
    }
    catch (IOException e)
    {
      error(Msg.IO_ERROR, e.getMessage());
    }
  }

  private void handleMarker(final Marker marker) throws IOException
  {
    int length = marker.getLength();
    if (hasLength(marker.getId()))
    {
      length -= 2;
    }
    if (length < 0)
    {
      error(Msg.INVALID_MARKER_LENGTH, length, Integer.toHexString(marker.getId()));
    }
    else
    {
      final Segment segment = marker.getSegment();
      append(segment, length);
      LOGGER.debug(
          marker.getSegment().getOffset() + " " + Integer.toHexString(marker.getId()) + " " + marker.getLength());
      final JpegReader reader = new JpegReader(this);
      reader.parseMarker(marker);
    }
  }

  private void checkForExtraneousData() throws IOException
  {
    if (!getJpegFileDescription().isEmbedded() && isSuccess())
    {
      final SeekableSource source = getSource();
      final long position = source.getPosition();
      final long length = source.getLength();
      if (position != length)
      {
        warn(Msg.EXTRANEOUS_DATA_AFTER_END_OF_STREAM, position, length);
      }
    }
  }

  @Override
  public String[] getTypicalFileExtensions()
  {
    return new String[]
    {
        "jpg", "jpeg"
    };
  }

  @Override
  public String getShortName()
  {
    return "JPEG";
  }

  @Override
  public String getLongName()
  {
    return "Joint Photographic Experts Group (JPEG)";
  }
}
