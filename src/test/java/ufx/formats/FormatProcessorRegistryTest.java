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
package ufx.formats;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import ufxcoder.formats.FormatProcessorRegistry;
import ufxcoder.formats.jpeg.JpegProcessor;
import ufxcoder.formats.tiff.TiffProcessor;

public class FormatProcessorRegistryTest
{
  @Test
  public void testExtensions()
  {
    FormatProcessorRegistry.register(TiffProcessor.class);
    FormatProcessorRegistry.register(JpegProcessor.class);
    Set<String> set = FormatProcessorRegistry.createKnownExtensionsSet(false);
    Assert.assertNotNull("Extension set must be non-null.", set);
    Assert.assertFalse("Extension set not empty.", set.isEmpty());
    set = FormatProcessorRegistry.createKnownExtensionsSet(true);
    Assert.assertNotNull("Extension set must be non-null.", set);
    Assert.assertFalse("Extension set not empty.", set.isEmpty());
  }
}
