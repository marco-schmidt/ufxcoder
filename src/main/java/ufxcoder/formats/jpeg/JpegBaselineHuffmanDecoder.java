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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decode baseline Huffman input.
 */
public class JpegBaselineHuffmanDecoder
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JpegBaselineHuffmanDecoder.class);
  private final JpegFileDescription desc;
  private final JpegScanReader reader;

  public JpegBaselineHuffmanDecoder(final JpegProcessor proc, final JpegScanReader reader)
  {
    desc = proc.getJpegFileDescription();
    this.reader = reader;
  }

  public void decode()
  {
    final JpegFrame frame = desc.getFrame();
    if (frame != null && frame.isBaseline())
    {
      final JpegScan scan = frame.getLastScan();
      if (scan != null && scan.getNumComponents() == 1)
      {
        final JpegScanComponent scanComp = scan.getFirstScan();
        final int dc = scanComp.getDc();
        final int ac = scanComp.getAc();
        final JpegHuffmanTable dcTable = desc.findHuffmanTable(dc, Constants.TABLE_CLASS_DC);
        final JpegHuffmanDecoder dcDec = new JpegHuffmanDecoder(dcTable, reader);
        final JpegHuffmanTable acTable = desc.findHuffmanTable(ac, Constants.TABLE_CLASS_AC);
        final JpegHuffmanDecoder acDec = new JpegHuffmanDecoder(acTable, reader);
        final int hBlocks = (frame.getWidth() + 7) / 8;
        final int vBlocks = (frame.getHeight() + 7) / 8;
        int totalBlocks = hBlocks * vBlocks;
        LOGGER.debug("H=" + hBlocks + " V=" + vBlocks + " TOTAL=" + totalBlocks);
        final int[] zz = new int[64];
        while (totalBlocks > 0)
        {
          zz[0] = dcDec.decodeDc();
          acDec.decodeAc(zz);
          totalBlocks--;
        }
      }
    }
  }
}
