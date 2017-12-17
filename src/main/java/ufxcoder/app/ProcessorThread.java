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
package ufxcoder.app;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.EventSeverity;
import ufxcoder.formats.FileDescription;
import ufxcoder.formats.FormatProcessorRegistry;
import ufxcoder.io.SeekableSource;

/**
 * Thread processing jobs from a queue.
 */
public class ProcessorThread implements Runnable
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorThread.class);
  private boolean initialized;
  private List<AbstractFormatProcessor> processors;
  private final BlockingQueue<ProcessorJob> queue;
  private final AppConfig config;

  public ProcessorThread(final AppConfig appConfig, final BlockingQueue<ProcessorJob> queue)
  {
    this.config = appConfig;
    this.queue = queue;
  }

  private void initialize()
  {
    processors = FormatProcessorRegistry.createProcessorInstances();
    initialized = true;
  }

  private void process(final String fileName)
  {
    boolean identified = false;
    for (final AbstractFormatProcessor proc : processors)
    {
      proc.setConfig(config);
      proc.process(fileName);
      handleResult(proc, proc.getSource(), proc.getFileDescription());
      if (proc.isFormatIdentified())
      {
        identified = true;
        proc.reset();
        break;
      }
      else
      {
        proc.reset();
      }
    }
    if (!identified)
    {
      LOGGER.info(fileName + "\t" + "?" + "\t");
    }
  }

  private void handleResult(final AbstractFormatProcessor proc, final SeekableSource source, final FileDescription desc)
  {
    if (proc.isFormatIdentified())
    {
      final String eventText = desc.formatEvents();
      String key;
      final EventSeverity highestSeverity = desc.findHighestSeverity();
      boolean ok = false;
      if (highestSeverity == EventSeverity.Error)
      {
        key = "processor.result.error";
      }
      else
      {
        if (highestSeverity == EventSeverity.Warning)
        {
          key = "processor.result.warn";
        }
        else
        {
          key = "processor.result.ok";
          ok = true;
        }
      }
      if (!(ok && config.isQuiet()))
      {
        LOGGER.info(source.getName() + "\t" + proc.getShortName() + "\t" + proc.msg(key) + "\t" + eventText);
      }
    }
  }

  @Override
  public void run()
  {
    while (!queue.isEmpty())
    {
      if (!initialized)
      {
        initialize();
      }
      try
      {
        final ProcessorJob job = queue.take();
        process(job.getFileName());
      }
      catch (InterruptedException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
  }
}
