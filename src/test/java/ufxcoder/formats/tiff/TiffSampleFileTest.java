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
package ufxcoder.formats.tiff;

import java.io.IOException;
import java.io.InputStream;
import ufx.formats.AbstractSampleFileTest;
import ufxcoder.formats.AbstractFormatProcessor;

/**
 * Load various small files from test/resources, run them through {@link TiffProcessor} and compare actual with expected
 * result from text file.
 */
public final class TiffSampleFileTest extends AbstractSampleFileTest
{
  @Override
  public AbstractFormatProcessor create(final byte[] data)
  {
    final TiffProcessor proc = (TiffProcessor) new TiffProcessorTest().create(data);
    proc.getConfig().setTiffBaseline(true);
    return proc;
  }

  @Override
  public InputStream open(final String name) throws IOException
  {
    return getClass().getResourceAsStream(name);
  }
}
