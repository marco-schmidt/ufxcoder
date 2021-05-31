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
package ufxcoder.app;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import ufxcoder.conversion.StrUtil;
import ufxcoder.formats.AbstractFormatProcessor;

/**
 * Configuration for {@link UniversalFileTranscoder} application.
 */
public class AppConfig
{
  /**
   * Logging Mapped Diagnostic Context identifier for the machine.
   */
  public static final String MDC_MACHINE = "machine";

  /**
   * Default logging pattern.
   */
  public static final String DEFAULT_LOGGING_PATTERN = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZ}\t%X{" + MDC_MACHINE
      + "}\t%thread\t%level\t%message%n";

  /**
   * If no argument is specified, use this many threads per CPU as returned by {@link Runtime#availableProcessors()}.
   */
  public static final int DEFAULT_NUMBER_OF_THREADS_PER_CPU = 4;
  private boolean showEnvironment;
  private ResourceBundle bundle;
  private final List<String> fileNames;
  private final List<String> directoryNames;
  private ProcessMode mode;
  private List<AbstractFormatProcessor> processors;
  private SystemInfo systemInfo;
  private Integer numberOfThreads;
  private Locale locale;
  private boolean quiet;
  private boolean knownFileExtensionsOnly;
  private boolean tiffBaseline;

  public AppConfig()
  {
    fileNames = new ArrayList<String>();
    directoryNames = new ArrayList<String>();
  }

  public ResourceBundle getBundle()
  {
    return bundle;
  }

  public final void setBundle(final ResourceBundle bundle)
  {
    this.bundle = bundle;
  }

  /**
   * Look up message in resource bundle.
   *
   * @param key
   *          message key
   * @return looked-up message
   */
  public String msg(final String key)
  {
    return bundle != null && bundle.containsKey(key) ? bundle.getString(key) : "";
  }

  /**
   * Look up message in resource bundle and format using arguments.
   *
   * @param key
   *          message key
   * @param args
   *          arguments used for formatting
   * @return looked-up message
   */
  public String msg(final String key, final Object... args)
  {
    String result;
    final String pattern = msg(key);
    if (pattern.isEmpty())
    {
      result = "";
    }
    else
    {
      final MessageFormat formatter = new MessageFormat(pattern, getLocale());
      result = formatter.format(args);
      result = StrUtil.escapeControl(result);
    }
    return result;
  }

  public boolean hasMsg(final String key)
  {
    return bundle != null && bundle.containsKey(key);
  }

  public List<String> getFileNames()
  {
    return fileNames;
  }

  /**
   * Add file name to internal list of files to be processed.
   *
   * @param fileName
   *          name of file to be added
   */
  public void addFileName(final String fileName)
  {
    if (fileName != null)
    {
      fileNames.add(fileName);
    }
  }

  public ProcessMode getMode()
  {
    return mode;
  }

  public void setMode(final ProcessMode mode)
  {
    this.mode = mode;
  }

  public List<AbstractFormatProcessor> getProcessors()
  {
    return processors;
  }

  public void setProcessors(final List<AbstractFormatProcessor> processors)
  {
    this.processors = processors;
  }

  public boolean isShowEnvironment()
  {
    return showEnvironment;
  }

  public void setShowEnvironment(final boolean showEnvironment)
  {
    this.showEnvironment = showEnvironment;
  }

  public SystemInfo getSystemInfo()
  {
    return systemInfo;
  }

  public void setSystemInfo(final SystemInfo systemInfo)
  {
    this.systemInfo = systemInfo;
  }

  public void addDirectory(final String nextArg)
  {
    directoryNames.add(nextArg);
  }

  public List<String> getDirectoryNames()
  {
    return directoryNames;
  }

  public Integer getNumberOfThreads()
  {
    return numberOfThreads;
  }

  public void setNumberOfThreads(final Integer numberOfThreads)
  {
    this.numberOfThreads = numberOfThreads;
  }

  public Locale getLocale()
  {
    return locale;
  }

  public void setLocale(final Locale locale)
  {
    this.locale = locale;
  }

  public boolean isQuiet()
  {
    return quiet;
  }

  public void setQuiet(final boolean quiet)
  {
    this.quiet = quiet;
  }

  public boolean isKnownFileExtensionsOnly()
  {
    return knownFileExtensionsOnly;
  }

  public void setKnownFileExtensionsOnly(final boolean knownFileExtensionsOnly)
  {
    this.knownFileExtensionsOnly = knownFileExtensionsOnly;
  }

  public boolean isTiffBaseline()
  {
    return tiffBaseline;
  }

  public void setTiffBaseline(final boolean tiffBaseline)
  {
    this.tiffBaseline = tiffBaseline;
  }
}
