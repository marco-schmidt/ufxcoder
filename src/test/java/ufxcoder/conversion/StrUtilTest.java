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
package ufxcoder.conversion;

import org.junit.Assert;
import org.junit.Test;

public class StrUtilTest
{
  @Test
  public void testEscapeControlNull()
  {
    Assert.assertNull("Null input leads to null output.", StrUtil.escapeControl(null));
  }

  @Test
  public void testEscapeControlEmpty()
  {
    Assert.assertEquals("Empty input leads to empty output.", StrUtil.escapeControl(""), "");
  }

  @Test
  public void testEscapeControlRegular()
  {
    final String test = "abc";
    Assert.assertEquals("Input without control characters leads to identical output.", StrUtil.escapeControl(test),
        test);
  }

  @Test
  public void testEscapeControlLinefeed()
  {
    final String input = "abc\nabc";
    final String expected = "abc abc";
    Assert.assertEquals("Input with linefeed leads to output with space at the same place.", expected,
        StrUtil.escapeControl(input));
  }
}
