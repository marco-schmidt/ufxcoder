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
package ufxcoder.formats.xmp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
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
      60, 63, 120, 112, 97, 99, 107, 101, 116,
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
    final Xpacket xpacketBegin = new Xpacket();
    final int index = parseXpacket(data, 0, xpacketBegin);
    if (index < 0)
    {
      processor.error("xmp.error.unable_to_find_xpacket");
      result = false;
    }
    final Xpacket xpacketEnd = new Xpacket();
    int indexEnd = 0;
    if (processor.isSuccess())
    {
      indexEnd = parseXpacket(data, index, xpacketEnd);
    }
    if (indexEnd <= index)
    {
      processor.error("xmp.error.unable_to_find_xpacket");
      result = false;
    }

    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder;
    try
    {
      builder = factory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e1)
    {
      builder = null;
      result = false;
    }
    if (builder != null)
    {
      Document doc;
      try
      {
        doc = builder.parse(new ByteArrayInputStream(data, index, xpacketEnd.getStartOffset() - index));
        if (doc == null)
        {
          result = false;
        }
        else
        {
          result = true;
        }
      }
      catch (SAXException | IOException | ArrayIndexOutOfBoundsException e)
      {
        processor.error("tiff.error.xmp_parsing_failed", e.getMessage());
        result = false;
      }
    }

    return result;
  }

  private int parseXpacket(final byte[] data, final int initialIndex, final Xpacket xpacket)
  {
    int index = findXpacket(data, initialIndex);
    if (index >= 0)
    {
      xpacket.setStartOffset(index);
      final List<XmpPacketAttribute> attributes = xpacket.getAttributes();
      index = consumeAttributes(data, index + XPACKET.length, attributes);
    }
    return index;
  }

  private int consumeAttributes(final byte[] data, final int initialIndex, final List<XmpPacketAttribute> attributes)
  {
    int index = initialIndex;

    // read all attributes
    int nextIndex;
    do
    {
      if (index >= data.length || data[index] != 32)
      {
        break;
      }

      nextIndex = consumeAttribute(data, index, attributes);
      if (index == nextIndex)
      {
        break;
      }
      index = nextIndex;
    }
    while (true);

    // search for ?>
    index = findEndOfPacket(data, index);
    return index;
  }

  private int findEndOfPacket(final byte[] data, final int initialIndex)
  {
    int index = initialIndex;
    if (processor.isSuccess() && data != null)
    {
      while (index + 1 < data.length && (data[index] != '?' || data[index + 1] != '>'))
      {
        index++;
      }
    }
    return index + 2;
  }

  /**
   * Read single attribute from input. Format: space name equals quote/apostrophe value quote/apostrophe
   *
   * @param data
   *          array of bytes
   * @param initialIndex
   *          index into data to start
   * @param attributes
   *          new attribute will be added to this
   * @return index after reading attribute successfully, or initialIndex if there were problems
   */
  private int consumeAttribute(final byte[] data, final int initialIndex, final List<XmpPacketAttribute> attributes)
  {
    int index = initialIndex;

    // single space
    if (index >= data.length || data[index] != 32)
    {
      processor.error("xmp.error.attribute_leading_space");
    }
    index++;

    // name
    StringBuffer sb = new StringBuffer();
    index = readName(data, index, sb);
    final String name = sb.toString();

    // equals
    if (processor.isSuccess())
    {
      if (index >= data.length || data[index] != 61)
      {
        processor.error("xmp.error.attribute_no_equals");
      }
      index++;
    }

    // quote or apostrophe
    int quote = 0;
    if (processor.isSuccess())
    {
      if (index >= data.length)
      {
        processor.error("xmp.error.attribute_quote");
      }
      else
      {
        quote = data[index];
        index++;
        if (quote != 34 && quote != 39)
        {
          processor.error("xmp.error.attribute_quote");
        }
      }
    }

    // value
    sb = new StringBuffer();
    index = readLiteral(data, index, sb, quote);
    final String value = sb.toString();

    // result
    if (processor.isSuccess())
    {
      attributes.add(new XmpPacketAttribute(name, value));
    }
    else
    {
      index = initialIndex;
    }
    return index;
  }

  private int readLiteral(final byte[] data, final int initialIndex, final StringBuffer sb, final int quote)
  {
    int index = initialIndex;
    if (processor.isSuccess())
    {
      boolean foundQuote = false;
      while (index < data.length)
      {
        final char ch = (char) data[index];
        index++;
        if (ch == quote)
        {
          foundQuote = true;
          break;
        }
        else
        {
          sb.append(ch);
        }
      }
      if (!foundQuote)
      {
        processor.error("xmp.error.attribute_missing_closing_quote");
      }
    }
    return index;
  }

  private int readName(final byte[] data, final int initialIndex, final StringBuffer sb)
  {
    int index = initialIndex;
    if (processor.isSuccess())
    {
      while (index < data.length && data[index] >= 97 && data[index] <= 122)
      {
        sb.append((char) data[index++]);
      }
      if (sb.length() == 0)
      {
        processor.error("xmp.error.attribute_empty_name");
      }
    }
    return index;
  }
}
