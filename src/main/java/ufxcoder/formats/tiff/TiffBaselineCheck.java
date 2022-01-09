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
package ufxcoder.formats.tiff;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ufxcoder.conversion.Array;

/**
 * Checks conformance of a file with TIFF Baseline.
 */
public class TiffBaselineCheck
{
  private static final Set<Integer> ALLOWED_COMPRESSION = Array.toSet(Constants.COMPRESSION_NONE,
      Constants.COMPRESSION_MODIFIED_HUFFMAN_RLE, Constants.COMPRESSION_PACKBITS);
  private final Map<Integer, FieldDescription> tagToDesc = new HashMap<>();
  private boolean baseline;
  private final TiffProcessor processor;

  public TiffBaselineCheck(final TiffProcessor proc)
  {
    for (final FieldDescription desc : FieldDescriptionFactory.getBaseline())
    {
      tagToDesc.put(desc.getTag(), desc);
    }
    baseline = false;
    processor = proc;
  }

  public void check(final TiffFileDescription desc)
  {
    final int numDirectories = desc.getNumDirectories();
    if (processor.getConfig().isTiffBaseline() && numDirectories > 0)
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
      final int id = field.getId();
      if (tagToDesc.get(id) == null)
      {
        String fieldName = processor.msg(Msg.PREFIX_FIELD_NAME + id);
        if ("".equals(fieldName))
        {
          fieldName = "?";
        }
        processor.error(Msg.NON_BASELINE_FIELD, id, fieldName);
        baseline = false;
        break;
      }
    }
    checkCompression(ifd);
    checkPhotometricInterpretation(ifd);
  }

  private void checkPhotometricInterpretation(final ImageFileDirectory ifd)
  {
    final Field field = ifd.findByTag(FieldDescriptionFactory.PHOTOMETRIC_INTERPRETATION);
    if (field != null)
    {
      final int pi = field.getAsInt();
      switch (pi)
      {
      case Constants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO:
      case Constants.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO:
      {
        checkGrayscale(ifd, pi);
        break;
      }
      case Constants.PHOTOMETRIC_INTERPRETATION_PALETTED:
      {
        checkPaletted(ifd);
        break;
      }
      case Constants.PHOTOMETRIC_INTERPRETATION_RGB:
      {
        checkRgb(ifd);
        break;
      }
      default:
      {
        processor.error(Msg.NON_BASELINE_PHOTOMETRIC_INTERPRETATON, pi);
        break;
      }
      }
    }
  }

  private void checkRgb(final ImageFileDirectory ifd)
  {
    // TIFF6.pdf, p. 24: "Each component is 8 bits deep in a Baseline TIFF RGB image."
    checkBitsPerSample(ifd, Constants.PHOTOMETRIC_INTERPRETATION_RGB, 3, Array.toSet(new int[]
    {
        8
    }));
  }

  private void checkPaletted(final ImageFileDirectory ifd)
  {
    checkBitsPerSample(ifd, Constants.PHOTOMETRIC_INTERPRETATION_PALETTED, 1, Array.toSet(new int[]
    {
        // TIFF6.pdf, p. 23
        4, 8
    }));
  }

  private void checkGrayscale(final ImageFileDirectory ifd, final int photomInterp)
  {
    checkBitsPerSample(ifd, photomInterp, 1, Array.toSet(new int[]
    {
        1, 4, 8
    }));
  }

  private void checkBitsPerSample(final ImageFileDirectory ifd, final int photomInterp, final int numComponents,
      final Set<Integer> allowedBitsPerSample)
  {
    final Field bps = ifd.findByTag(FieldDescriptionFactory.BITS_PER_SAMPLE);
    if (bps == null)
    {
      processor.error(Msg.BASELINE_BITS_PER_SAMPLE_MISSING);
    }
    else
    {
      final long numValues = bps.getNumValues();
      if (numValues < numComponents)
      {
        processor.error(Msg.BASELINE_NUM_COMPONENTS, photomInterp, numComponents, numValues);
      }
      else
      {
        for (int index = 0; index < numComponents; index++)
        {
          final int compBits = bps.getAsInt(index);
          if (!allowedBitsPerSample.contains(compBits))
          {
            processor.error(Msg.BASELINE_INVALID_BITS_PER_SAMPLE, photomInterp, index, compBits);
          }
        }
      }
    }
  }

  private void checkCompression(final ImageFileDirectory ifd)
  {
    final Field compr = ifd.findByTag(FieldDescriptionFactory.COMPRESSION);
    boolean baselineCompression;
    String name = null;
    int value = 0;
    if (compr == null)
    {
      baselineCompression = false;
    }
    else
    {
      value = compr.getAsInt();
      baselineCompression = ALLOWED_COMPRESSION.contains(value);
      name = processor.msg(Msg.PREFIX_FIELD_NAME + FieldDescriptionFactory.COMPRESSION.getTag() + "." + value);
    }
    if (!baselineCompression)
    {
      if (name == null)
      {
        name = "?";
      }
      processor.error(Msg.NON_BASELINE_COMPRESSION, value, name);
      baseline = false;
    }
  }

  public boolean isBaseline()
  {
    return baseline;
  }
}
