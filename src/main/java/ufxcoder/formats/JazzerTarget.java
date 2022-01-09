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

import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ufxcoder.app.AppConfig;
import ufxcoder.formats.jpeg.JpegProcessor;

/**
 * Target class to be used with fuzzing tool jazzer.
 *
 * Implements method signature required by jazzer 0.9.1.
 *
 * <pre>
 * bazel --version
 * ./gradlew install
 * jazzer --cp=build/install/ufxcoder/lib/ufxcoder-0.0.3-SNAPSHOT.jar:\
    build/install/ufxcoder/lib/logback-classic-1.2.3.jar:\
    build/install/ufxcoder/lib/logback-core-1.2.3.jar:\
    build/install/ufxcoder/lib/slf4j-api-1.7.30.jar --target_class=ufxcoder.formats.JazzerTarget
 * </pre>
 *
 * @see https://github.com/CodeIntelligenceTesting/jazzer
 * @author Marco Schmidt
 */
public final class JazzerTarget
{
  private static AbstractFormatProcessor proc;

  private JazzerTarget()
  {
  }

  private static void initialize()
  {
    final AppConfig config = new AppConfig();
    config.setBundle(ResourceBundle.getBundle("Messages", Locale.ENGLISH));
    config.setLocale(Locale.ENGLISH);
    proc = new JpegProcessor();
    proc.setConfig(config);
    // turn off logging
    final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(Logger.ROOT_LOGGER_NAME);
    if (rootLogger != null)
    {
      rootLogger.setLevel(Level.OFF);
    }
  }

  public static void fuzzerTestOneInput(final byte[] input)
  {
    if (proc == null)
    {
      initialize();
    }
    proc.open(input);
    proc.process();
  }
}
