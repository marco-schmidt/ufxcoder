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
package ufxcoder.formats.tiff;

/**
 * The various types defined for values in a TIFF field.
 *
 * <ul>
 * <li>TIFF 6.0 specification pp. 15-16.</li>
 * <li><a href="http://www.awaresystems.be/imaging/tiff/bigtiff.html">BigTiff</a></li>
 * </ul>
 */
public enum FieldType
{
  /**
   * Unsigned byte, integer value from 0 to 255.
   */
  Byte("Byte", 1, 1, true),

  /**
   * Character, a single byte which only uses the lower seven bits to be interpreted as ASCII.
   */
  Char("Char", 2, 1, false),

  /**
   * Unsigned 16 bit integer.
   */
  Short("Short", 3, 2, true),

  /**
   * Unsigned 32 bit integer.
   */
  Long("Long", 4, 4, true),

  /**
   * Fraction from two unsigned 32 bit integer values.
   */
  Rational("Rational", 5, 8, false),

  /**
   * Signed 8 bit integer value.
   */
  SignedByte("Signed Byte", 6, 1, true),

  /**
   * 8 bit integer value without further description, may be used for any sort of data.
   */
  Undefined("Undefined", 7, 1, true),

  /**
   * Signed 16 bit integer value.
   */
  SignedShort("Signed Short", 8, 2, true),

  /**
   * Signed 32 bit integer value.
   */
  SignedLong("Signed Long", 9, 4, true),

  /**
   * Fraction from two signed 32 bit integer values.
   */
  SignedRational("Signed Rational", 10, 8, false),

  /**
   * 32 bit floating point value.
   */
  Float("Float", 11, 4, false),

  /**
   * 64 bit floating point value.
   */
  Double("Double", 12, 8, false),

  /**
   * 32 bit unsigned integer used for image file directory offsets.
   */
  IfdOffset("IFD Offset", 13, 4, true),

  /**
   * Unsigned 64 bit integer value.
   */
  Long8("Long8", 16, 8, true),

  /**
   * Signed 64 bit integer value.
   */
  SignedLong8("Signed Long8", 17, 8, true),

  /**
   * 64 bit offset value.
   */
  IfdOffset8("Ifd Offset8", 18, 8, true);

  private String name;
  private int id;
  private int size;
  private boolean integerNumber;

  FieldType(final String name, final int id, final int size, final boolean integerNumber)
  {
    this.name = name;
    this.id = id;
    this.size = size;
    this.integerNumber = integerNumber;
  }

  public int getId()
  {
    return id;
  }

  public int getSize()
  {
    return size;
  }

  public String getName()
  {
    return name;
  }

  public boolean isIntegerNumber()
  {
    return integerNumber;
  }

  public static FieldType findById(final int id)
  {
    FieldType result = null;
    for (final FieldType t : values())
    {
      if (id == t.getId())
      {
        result = t;
        break;
      }
    }
    return result;
  }
}
