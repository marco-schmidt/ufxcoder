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
package ufxcoder.formats;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.app.AppConfig;
import ufxcoder.app.ProcessMode;
import ufxcoder.conversion.Array;
import ufxcoder.io.FileSource;
import ufxcoder.io.SeekableSource;
import ufxcoder.io.Segment;

/**
 * Base class for all processors examining and transcoding specific file formats.
 */
public abstract class AbstractFormatProcessor
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFormatProcessor.class);
  private SeekableSource source;
  private FileDescription fileDescription;
  private AppConfig config;
  private boolean formatIdentified;

  public void addEvent(final EventSeverity severity, final String messageKey, final Object... arguments)
  {
    fileDescription.addEvent(severity, messageKey, config.msg(messageKey, arguments));
  }

  public void addErrorMessage(final String msg)
  {
    if (msg != null && fileDescription != null)
    {
      fileDescription.addErrorMessage(msg);
    }
  }

  public void error(final String messageKey, final Object... arguments)
  {
    addEvent(EventSeverity.Error, messageKey, arguments);
  }

  public void reset()
  {
    setFileDescription(null);
    setFormatIdentified(false);
    setSource(null);
  }

  public FileDescription getFileDescription()
  {
    return fileDescription;
  }

  public void setFileDescription(final FileDescription fileDescription)
  {
    this.fileDescription = fileDescription;
  }

  /**
   * Create a new instance of FileDescription or its most appropriate child class.
   *
   * @return {@link FileDescription} object appropriate for the implementing processor
   */
  public abstract FileDescription createDescription();

  /**
   * Open argument file for reading.
   *
   * @param file
   *          file to be opened
   * @throws IOException
   *           on error
   */
  public void open(final File file) throws IOException
  {
    setSource(new FileSource(file));
  }

  /**
   * Process input following the current processing mode ({@link AppConfig#getMode()}).
   */
  public abstract void process();

  public void process(final String fileName)
  {
    try
    {
      open(new File(fileName));
      process();
    }
    catch (IOException e)
    {
      LOGGER.error(String.format("Could not open file '%s'.", fileName), e);
    }
  }

  /**
   * List of lower-case file name extensions typical for the format supported by this processor.
   *
   * @return possibly empty array of file name extensions
   */
  public abstract String[] getTypicalFileExtensions();

  /**
   * Short name of the format supported by this processor, possibly an abbreviation.
   *
   * @return short format name
   */
  public abstract String getShortName();

  /**
   * Full name of the format supported by this processor.
   *
   * @return full format name
   */
  public abstract String getLongName();

  public SeekableSource getSource()
  {
    return source;
  }

  public void setSource(final SeekableSource source)
  {
    this.source = source;
  }

  public void closeSource()
  {
    final SeekableSource src = getSource();
    if (src != null)
    {
      try
      {
        src.close();
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
  }

  /**
   * Read data from source and append to existing {@link Segment}.
   *
   * @param segment
   *          append data to this segment
   * @param numBytes
   *          number of bytes to read
   * @throws IOException
   *           when there were problems
   */
  public void append(final Segment segment, final int numBytes) throws IOException
  {
    final int offset = segment.getLength();
    final byte[] data = Array.clone(segment.getData(), numBytes);
    source.readFully(data, offset, numBytes);
    segment.setData(data);
    segment.setLength(data.length);
  }

  public boolean isValidSourceOffset(final BigInteger offset)
  {
    boolean valid;
    try
    {
      valid = offset.signum() != -1 && offset.compareTo(BigInteger.valueOf(source.getLength())) <= 0;
    }
    catch (IOException e)
    {
      valid = false;
    }
    return valid;
  }

  public Segment read(final int numBytes) throws IOException
  {
    final Segment result = new Segment();
    assignByteOrder(getFileDescription(), result);
    result.setOffset(source.getPosition());
    final byte[] data = new byte[numBytes];
    source.readFully(data, 0, numBytes);
    result.setData(data);
    result.setLength(data.length);
    return result;
  }

  private void assignByteOrder(final FileDescription desc, final Segment result)
  {
    result.setByteOrder(desc.getByteOrder());
  }

  public boolean isIdentify()
  {
    return config.getMode() == ProcessMode.Identify;
  }

  public boolean isSuccess()
  {
    return fileDescription != null && fileDescription.isSuccess();
  }

  public AppConfig getConfig()
  {
    return config;
  }

  public void setConfig(final AppConfig config)
  {
    this.config = config;
  }

  public String msg(final String key)
  {
    return config.msg(key);
  }

  public String msg(final String key, final Object... args)
  {
    return config.msg(key, args);
  }

  public boolean hasMsg(final String key)
  {
    return config.hasMsg(key);
  }

  public boolean isFormatIdentified()
  {
    return formatIdentified;
  }

  public void setFormatIdentified(final boolean formatIdentified)
  {
    this.formatIdentified = formatIdentified;
  }
}
