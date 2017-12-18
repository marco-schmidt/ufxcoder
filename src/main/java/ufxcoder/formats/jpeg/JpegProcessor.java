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

  private long identify(final long initialOffset, final Marker marker)
  {
    long offset = initialOffset;
    try
    {
      offset = readMarker(offset, marker);
      setFormatIdentified(marker.getId() == Constants.MARKER_START_OF_IMAGE);
    }
    catch (IOException e)
    {
      error(Msg.CANNOT_READ_HEADER, e);
    }
    return offset;
  }

  @Override
  public void process()
  {
    final JpegFileDescription desc = new JpegFileDescription();
    setFileDescription(desc);

    final Marker startOfImage = new Marker();
    startOfImage.setNumber(1);
    final long offset = identify(0, startOfImage);

    if (isFormatIdentified() && !isIdentify())
    {
      processInput(offset);
    }

    closeSource();
  }

  /**
   * Read a two-byte marker and possibly a two-byte length value.
   *
   * @param initialOffset
   *          file offset from which to start reading
   * @param marker
   *          data structure to add read integer values to
   * @return offset after reading marker information
   * @throws IOException
   *           if there are problems seeking or reading
   */
  private long readMarker(final long initialOffset, final Marker marker) throws IOException
  {
    long offset = initialOffset;
    Segment segm;
    segm = read(2);
    segm.setByteOrder(ByteOrder.BigEndian);
    marker.setSegment(segm);
    final int markerId = segm.int16();
    if ((markerId & Constants.MARKER_MASK) == Constants.MARKER_MASK)
    {
      marker.setId(markerId);
      offset += 2;
      if (hasLength(markerId))
      {
        append(segm, 2);
        final int length = segm.int16();
        marker.setLength(length);
        offset += 2;
      }
    }
    else
    {
      error(Msg.INVALID_MARKER, markerId, offset);
    }
    return offset;
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

  private void processInput(final long initialOffset)
  {
    final JpegFileDescription desc = getJpegFileDescription();
    long offset = initialOffset;

    // read markers until there is an I/O exception or an error
    int number = 2;
    try
    {
      Marker marker;
      do
      {
        marker = new Marker();
        marker.setNumber(number++);
        offset = readMarker(offset, marker);
        if (isSuccess())
        {
          offset = handleMarker(offset, marker);
        }
        desc.add(marker);
      }
      while (isSuccess() && marker.getId() != Constants.MARKER_END_OF_IMAGE);
      checkForExtraneousData(offset);
    }
    catch (IOException e)
    {
      error(e.getMessage());
    }
  }

  private long handleMarker(final long initialOffset, final Marker marker) throws IOException
  {
    int length = marker.getLength();
    if (hasLength(marker.getId()))
    {
      length -= 2;
    }
    final Segment segment = marker.getSegment();
    append(segment, length);
    LOGGER
        .debug(marker.getSegment().getOffset() + " " + Integer.toHexString(marker.getId()) + " " + marker.getLength());
    return initialOffset + length;
  }

  private void checkForExtraneousData(final long offset) throws IOException
  {
    if (!getJpegFileDescription().isEmbedded() && isSuccess())
    {
      final SeekableSource source = getSource();
      final long length = source.getLength();
      if (offset != length)
      {
        error(Msg.EXTRANEOUS_DATA_AFTER_END_OF_STREAM, offset, length);
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
