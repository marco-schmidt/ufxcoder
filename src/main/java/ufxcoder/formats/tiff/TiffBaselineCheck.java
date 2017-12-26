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
package ufxcoder.formats.tiff;

import java.util.HashMap;
import java.util.Map;

/**
 * Checks conformance of a file with TIFF Baseline.
 */
public class TiffBaselineCheck
{
  private final Map<Integer, FieldDescription> tagToDesc = new HashMap<>();
  private boolean baseline;

  public TiffBaselineCheck()
  {
    for (final FieldDescription desc : FieldDescriptionFactory.getBaseline())
    {
      tagToDesc.put(desc.getTag(), desc);
    }
    baseline = false;
  }

  public void check(final TiffFileDescription desc)
  {
    final int numDirectories = desc.getNumDirectories();
    if (numDirectories > 0)
    {
      final ImageFileDirectory directory = desc.getDirectory(0);
      check(directory);
    }
  }

  private void check(final ImageFileDirectory ifd)
  {
    baseline = true;
    for (final Field field : ifd.getFields())
    {
      if (tagToDesc.get(field.getId()) == null)
      {
        baseline = false;
        break;
      }
    }
    checkCompression(ifd);
  }

  private void checkCompression(final ImageFileDirectory ifd)
  {
    final Field compr = ifd.findByTag(FieldDescriptionFactory.COMPRESSION);
    if (compr == null)
    {
      baseline = false;
    }
    else
    {
      final int value = compr.getAsInt();
      baseline = baseline && (value == Constants.COMPRESSION_NONE || value == Constants.COMPRESSION_MODIFIED_HUFFMAN_RLE
          || value == Constants.COMPRESSION_PACKBITS);
    }
  }

  public boolean isBaseline()
  {
    return baseline;
  }
}
