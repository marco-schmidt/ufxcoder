/*
 * Copyright 2017, 2018, 2019, 2020 the original author or authors.
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

/**
 * Validate various properties of a TIFF file related to color, samples, pixels.
 */
public class TiffColorValidator
{
  private final TiffProcessor proc;

  public TiffColorValidator(final TiffProcessor proc)
  {
    this.proc = proc;
  }

  public void checkSamples(final ImageFileDirectory ifd)
  {
    // determine image data samples per pixel from photometric interpretation
    final Field photoInterp = ifd.findByTag(FieldDescriptionFactory.PHOTOMETRIC_INTERPRETATION);
    int imageSamples = 0;
    int photomInterpValue = 0;
    if (photoInterp != null)
    {
      photomInterpValue = (int) photoInterp.getAsLong();
      imageSamples = Constants.mapPhotometricInterpretationToNumberOfSamples(photomInterpValue);
    }

    // retrieve number of additional samples
    long additionalSamples = 0;
    final Field extraSamples = ifd.findByTag(FieldDescriptionFactory.EXTRA_SAMPLES);
    if (extraSamples != null)
    {
      additionalSamples = extraSamples.getNumValues();
    }

    // retrieve total number of samples per pixel
    long totalSamples = 0;
    final Field samplesPerPixel = ifd.findByTag(FieldDescriptionFactory.SAMPLES_PER_PIXEL);
    if (samplesPerPixel != null)
    {
      totalSamples = samplesPerPixel.getAsLong();
    }

    // compare expected to actual samples
    if (imageSamples + additionalSamples != totalSamples)
    {
      proc.error("tiff.error.validation.unexpected_number_of_samples", photomInterpValue, imageSamples,
          additionalSamples, totalSamples);
    }
  }
}
