/*
 * Copyright 2017, 2018 the original author or authors.
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
package ufxcoder.formats.xmp;

import ufxcoder.conversion.Array;
import ufxcoder.formats.AbstractFormatProcessor;

/**
 * Parse metadata in XMP (eXtensible metadata platform) format.
 */
public class XmpReader
{
  /**
   * A sequence of bytes marking the beginning of an xpacket declaration (<code>&lt;?xpacket begin="</code>).
   */
  private static final byte[] XPACKET =
  {
      60, 63, 120, 112, 97, 99, 107, 101, 116, 32, 98, 101, 103, 105, 110, 61
  };
  private final AbstractFormatProcessor processor;

  public XmpReader(final AbstractFormatProcessor proc)
  {
    processor = proc;
  }

  public static int findXpacket(final byte[] data, final int initialIndex)
  {
    return Array.indexOf(data, initialIndex, XPACKET);
  }

  public boolean parseXmp(final byte[] data)
  {
    boolean result = true;
    final Xpacket xpacket = new Xpacket();
    final int index = parseXpacket(data, 0, xpacket);
    if (index < 0)
    {
      processor.error("xmp.error.unable_to_find_xpacket");
      result = false;
    }
    /*
     * int index = parseXpacket(data, 0, xpacket);
     *
     * final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
     * DocumentBuilder builder; try { builder = factory.newDocumentBuilder(); } catch (ParserConfigurationException e1)
     * { builder = null; result = false; } if (builder != null) { Document doc; try { doc = builder.parse(new
     * ByteArrayInputStream(data)); if (doc == null) { result = false; } else { result = true; } } catch (SAXException |
     * IOException e) { processor.getFileDescription().addErrorMessage(processor.msg("tiff.error.xmp_parsing_failed",
     * e.getMessage())); result = false; } }
     */
    return result;
  }

  private int parseXpacket(final byte[] data, final int initialIndex, final Xpacket xpacket)
  {
    final int index = findXpacket(data, initialIndex);
    xpacket.setId("xy");
    return index;
  }
}
