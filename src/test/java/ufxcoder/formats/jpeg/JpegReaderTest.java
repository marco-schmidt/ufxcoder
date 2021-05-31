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
package ufxcoder.formats.jpeg;

import org.junit.Before;

public class JpegReaderTest
{
  private JpegProcessor proc;
  private JpegReader reader;
  private JpegFrame frame;

  @Before
  public void setup()
  {
    proc = (JpegProcessor) new JpegProcessorTest().create(new byte[]
    {
        (byte) 0xff, (byte) 0xc0,
    });
    reader = new JpegReader(proc);
    frame = new JpegFrame();
    reset();
    reader.getClass();
  }

  private void reset()
  {
    frame.setBaseline(false);
    frame.setExtended(false);
    frame.setLossless(false);
    frame.setProgressive(false);
    proc.reset();
    reader.getClass();
  }
  //
  // @Test
  // public void testCheckSamplePrecisionLosslessTooLow()
  // {
  // reset();
  // proc.setFileDescription(new JpegFileDescription());
  // frame.setLossless(true);
  // frame.setSamplePrecision(1);
  // reader.checkSamplePrecision(frame);
  // Assert.assertTrue("1 bit lossless invalid.",
  // proc.getFileDescription().containsEvent(Msg.INVALID_SAMPLE_PRECISION_LOSSLESS));
  // }
}
