/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.CollectionUtil;

import java.util.Set;
import java.util.TreeSet;

/**
 * Generates unique strings of fixed length.<br/>
 * <br/>
 * Created: 15.11.2007 14:07:49
 *
 * @author Volker Bergmann
 */
public class UniqueFixedLengthStringGenerator extends NonNullGeneratorWrapper<int[], String> {

  /**
   * The constant DEFAULT_CHAR_SET.
   */
  public static final Set<Character> DEFAULT_CHAR_SET
      = CollectionUtil.toSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
  private static final int DEFAULT_LENGTH = 4;
  private static final boolean DEFAULT_ORDERED = false;

  private final char[] digitSymbols;
  private final int length;
  private final boolean ordered;

  /**
   * Instantiates a new Unique fixed length string generator.
   */
  public UniqueFixedLengthStringGenerator() {
    this(DEFAULT_CHAR_SET, DEFAULT_LENGTH, DEFAULT_ORDERED);
  }

  /**
   * Instantiates a new Unique fixed length string generator.
   *
   * @param chars   the chars
   * @param length  the length
   * @param ordered the ordered
   */
  public UniqueFixedLengthStringGenerator(Set<Character> chars, int length, boolean ordered) {
    super(null);
    this.digitSymbols = CollectionUtil.toCharArray(new TreeSet<>(chars));
    this.length = length;
    this.ordered = ordered;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    assertNotInitialized();
    if (ordered) {
      setSource(new IncrementalIntsGenerator(digitSymbols.length, length));
    } else {
      setSource(new UniqueIntsGenerator(digitSymbols.length, length));
    }
    super.init(context);
  }

  @Override
  public String generate() {
    ProductWrapper<int[]> wrapper = generateFromSource();
    if (wrapper == null) {
      return null;
    }
    int[] ordinals = wrapper.unwrap();
    char[] buffer = new char[length];
    for (int i = 0; i < length; i++) {
      buffer[i] = digitSymbols[ordinals[i]];
    }
    return String.valueOf(buffer);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[length=" + length + ", charset=" +
        ArrayFormat.formatChars(",", digitSymbols) + ']';
  }

}
