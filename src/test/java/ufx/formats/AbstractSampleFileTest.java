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
package ufx.formats;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import ufxcoder.formats.AbstractFormatProcessor;
import ufxcoder.formats.EventSeverity;
import ufxcoder.formats.FileDescription;

public abstract class AbstractSampleFileTest
{
  private static final String[] EMPTY =
  {};

  /**
   * Open a resource input stream in the implementing class, which may be in another package.
   *
   * @param name
   *          file name
   * @return open input stream on success
   * @throws IOException
   *           if opening failed
   */
  public abstract InputStream open(String name) throws IOException;

  /**
   * Load resource data from argument file to byte array.
   *
   * @param name
   *          file name
   * @return byte array with data
   * @throws IOException
   *           on read errors
   */
  public byte[] read(final String name) throws IOException
  {
    final InputStream in = open(name);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte[] buffer = new byte[1024];
    int numRead;
    while (true)
    {
      numRead = in.read(buffer);
      if (numRead >= 0)
      {
        baos.write(buffer, 0, numRead);
      }
      else
      {
        break;
      }
    }
    in.close();
    return baos.toByteArray();
  }

  public List<String> loadLines(final String name) throws IOException
  {
    final List<String> result = new ArrayList<>();
    final InputStream input = open(name);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    String line;
    try
    {
      do
      {
        line = reader.readLine();
        if (line != null)
        {
          result.add(line);
        }
      }
      while (line != null);
    }
    catch (final IOException ioe)
    {
      throw ioe;
    }
    finally
    {
      reader.close();
    }
    return result;
  }

  public abstract AbstractFormatProcessor create(byte[] data);

  private TestResult testStream(final String fileName) throws IOException
  {
    final byte[] data = read(fileName);
    final AbstractFormatProcessor proc = create(data);
    proc.process();
    final FileDescription desc = proc.getFileDescription();
    String result;
    if (proc.isFormatIdentified())
    {
      if (proc.isSuccess())
      {
        result = "ok";
      }
      else
      {
        final EventSeverity severity = desc.findHighestSeverity();
        result = severity.name().toLowerCase(Locale.ENGLISH);
      }
    }
    else
    {
      result = "unknown";
    }
    return new TestResult(result, desc.getSortedErrorWarningKeys());
  }

  @Test
  public void testStreams() throws IOException
  {
    final List<String> lines = loadLines("test-cases.tsv");
    for (final String rawLine : lines)
    {
      final String line = rawLine == null ? "" : rawLine.trim();
      final String[] comps = line.split("\t");
      final String fileName = comps[0];
      final String expectedResult = comps[1];
      final String expectedKeys = comps.length > 2 ? comps[2] : "";
      String[] expectedKeyArray = expectedKeys.split(",");
      if (expectedKeyArray.length == 1 && expectedKeyArray[0].equals(""))
      {
        expectedKeyArray = EMPTY;
      }
      final TestResult testResult = testStream(fileName);
      Assert.assertEquals(String.format("Differing results for %s.", fileName), expectedResult, testResult.getResult());
      final Object[] actualKeys = testResult.getKeys().toArray();
      Assert.assertArrayEquals(String.format("Differing keys for %s.", fileName), expectedKeyArray, actualKeys);
    }
  }
}
