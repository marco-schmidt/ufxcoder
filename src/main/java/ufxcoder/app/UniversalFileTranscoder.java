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
package ufxcoder.app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ufxcoder.formats.FormatProcessorRegistry;
import ufxcoder.formats.jpeg.JpegProcessor;
import ufxcoder.formats.tiff.TiffProcessor;
import ufxcoder.io.CollectAllFilesVisitor;

/**
 * Command line application to offer access to library features.
 */
public class UniversalFileTranscoder
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UniversalFileTranscoder.class);

  private boolean initialize(final AppConfig config, final String... args)
  {
    boolean success = true;
    final ResourceBundle bundle = ResourceBundle.getBundle("Messages", config.getLocale());
    config.setBundle(bundle);
    config.setProcessors(FormatProcessorRegistry.createProcessorInstances());
    final ArgumentParser parser = new ArgumentParser();
    if (parser.parse(config, args))
    {
      for (final String dirName : config.getDirectoryNames())
      {
        try
        {
          LOGGER.debug(config.msg("args.debug.scanning_directory", dirName));
          Files.walkFileTree(Paths.get(dirName), new CollectAllFilesVisitor(config));
        }
        catch (IOException e)
        {
          LOGGER.error(config.msg("args.error.scanning_directory", dirName), e);
        }
      }
      if (config.isKnownFileExtensionsOnly())
      {
        parser.removeFilesWithUnknownExtensions(config.getFileNames(),
            FormatProcessorRegistry.createKnownExtensionsSet(true));
      }

      setDefaults(config, args);
      final SystemInfo info = new SystemInfo();
      config.setSystemInfo(info);
      if (config.isShowEnvironment())
      {
        info.initialize(config, args);
        info.print(config);
      }
    }
    else
    {
      success = false;
    }
    return success;
  }

  private void setDefaults(final AppConfig config, final String... args)
  {
    if (config.getMode() == null)
    {
      config.setMode(args.length == 0 ? ProcessMode.ShowHelp : ProcessMode.Check);
    }
  }

  /**
   * Run the command line application, do initialization, then process files.
   *
   * @param args
   *          command line arguments given to the application
   */
  public static final void main(final String[] args)
  {
    final UniversalFileTranscoder transcoder = new UniversalFileTranscoder();
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    initLogger();
    final AppConfig config = new AppConfig();
    config.setLocale(Locale.ENGLISH);
    FormatProcessorRegistry.register(TiffProcessor.class);
    FormatProcessorRegistry.register(JpegProcessor.class);
    if (transcoder.initialize(config, args))
    {
      transcoder.process(config);
    }
  }

  private static LoggerContext initLoggerContext(final LoggerContext loggerContext)
  {
    loggerContext.reset();
    return loggerContext;
  }

  private static void initLogger()
  {
    final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(Logger.ROOT_LOGGER_NAME);
    final LoggerContext loggerContext = initLoggerContext(rootLogger.getLoggerContext());

    try
    {
      MDC.put(AppConfig.MDC_MACHINE, InetAddress.getLocalHost().getHostName());
    }
    catch (IllegalArgumentException | UnknownHostException e)
    {
      LOGGER.error("Unable to retrieve local host.", e);
    }

    final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern("%message%n");
    encoder.start();

    final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
    appender.setContext(loggerContext);
    appender.setEncoder(encoder);
    appender.start();

    rootLogger.addAppender(appender);
    rootLogger.setLevel(Level.INFO);
  }

  private void process(final AppConfig config)
  {
    switch (config.getMode())
    {
    case ShowHelp:
    {
      printHelp(config);
      break;
    }
    case ShowVersion:
    {
      printVersion(config);
      break;
    }
    default:
    {
      if (!config.getFileNames().isEmpty())
      {
        processFiles(config);
      }
      break;
    }
    }
  }

  private void processFiles(final AppConfig config)
  {
    long millis = System.currentTimeMillis();

    // create a job object for each file name
    final List<String> fileNames = config.getFileNames();
    final int numFileNames = fileNames.size();
    final BlockingQueue<ProcessorJob> queue = new ArrayBlockingQueue<ProcessorJob>(numFileNames);
    for (final String fileName : fileNames)
    {
      final ProcessorJob job = new ProcessorJob();
      job.setFileName(fileName);
      queue.add(job);
    }

    // determine number of threads to be used
    // 1) prefer argument 2) otherwise number of CPUs times 8 3) limit if there are fewer files than threads
    final Integer numberOfThreadsConfig = config.getNumberOfThreads();
    final int numThreads = Math.min(numFileNames,
        numberOfThreadsConfig == null
            ? Runtime.getRuntime().availableProcessors() * AppConfig.DEFAULT_NUMBER_OF_THREADS_PER_CPU
            : numberOfThreadsConfig.intValue());

    // create and start threads
    final List<Thread> threads = new ArrayList<Thread>(numThreads);
    for (int i = 1; i <= numThreads; i++)
    {
      final Thread thread = new Thread(new ProcessorThread(config, queue), "T" + String.format("%03d", i));
      threads.add(thread);
      thread.start();
    }

    // wait and check once in a while if there are any active threads left
    do
    {
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        LOGGER.error(e.getMessage());
      }
      final Iterator<Thread> iter = threads.iterator();
      while (iter.hasNext())
      {
        final Thread thread = iter.next();
        if (!thread.isAlive())
        {
          iter.remove();
        }
      }
    }
    while (!threads.isEmpty());
    millis = System.currentTimeMillis() - millis;
    LOGGER.info(config.msg("processor.files_time", config.getFileNames().size(), millis / 1000L));
  }

  private void printVersion(final AppConfig config)
  {
    LOGGER.info(String.format("%s %s", SystemInfo.APP_NAME, config.getSystemInfo().getApplicationVersion()));
  }

  private void printHelp(final AppConfig config)
  {
    for (final AbstractParameter param : ArgumentParser.getParameters())
    {
      LOGGER.info(ArgumentParser.format(config, param));
    }
  }
}
