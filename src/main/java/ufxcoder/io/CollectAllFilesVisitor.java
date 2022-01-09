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
package ufxcoder.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import ufxcoder.app.AppConfig;

/**
 * Add all files encountered to the configuration file list using {@link AppConfig#addFileName(String)}.
 */
public class CollectAllFilesVisitor extends SimpleFileVisitor<Path>
{
  private final AppConfig config;

  public CollectAllFilesVisitor(final AppConfig config)
  {
    super();
    this.config = config;
  }

  @Override
  public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
  {
    config.addFileName(file == null ? null : file.toString());
    return FileVisitResult.CONTINUE;
  }
}
