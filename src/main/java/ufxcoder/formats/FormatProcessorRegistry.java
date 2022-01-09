/*
 * Copyright 2017, 2018, 2019, 2020, 2021, 2022 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to register all classes extending {@link AbstractFormatProcessor}.
 */
public final class FormatProcessorRegistry
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FormatProcessorRegistry.class);
  private static List<Class<?>> classes = new ArrayList<Class<?>>();

  private FormatProcessorRegistry()
  {
  }

  public static void register(final Class<? extends AbstractFormatProcessor> processorClass)
  {
    synchronized (FormatProcessorRegistry.class)
    {
      classes.add(processorClass);
    }
  }

  public static List<AbstractFormatProcessor> createProcessorInstances()
  {
    final List<AbstractFormatProcessor> result = new ArrayList<AbstractFormatProcessor>(classes.size());
    for (final Class<?> clas : classes)
    {
      try
      {
        final Object obj = clas.getDeclaredConstructor().newInstance();
        if (obj instanceof AbstractFormatProcessor)
        {
          final AbstractFormatProcessor afp = (AbstractFormatProcessor) obj;
          result.add(afp);
        }
      }
      catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException
          | InvocationTargetException | SecurityException e)
      {
        LOGGER.error(String.format("Unable to instantiate object of class %s.", clas.getName()), e);
      }
    }
    return result;
  }

  public static Set<String> createKnownExtensionsSet(final boolean lower)
  {
    final List<AbstractFormatProcessor> processors = createProcessorInstances();
    final Set<String> result = new HashSet<>();
    for (final AbstractFormatProcessor proc : processors)
    {
      final String[] extensions = proc.getTypicalFileExtensions();
      for (final String ext : extensions)
      {
        if (ext != null && !ext.isEmpty())
        {
          String extToAdd;
          if (lower)
          {
            extToAdd = ext.toLowerCase(Locale.ENGLISH);
          }
          else
          {
            extToAdd = ext;
          }
          result.add(extToAdd);
        }
      }
    }
    return result;
  }
}
