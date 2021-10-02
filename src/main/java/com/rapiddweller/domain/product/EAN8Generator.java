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

package com.rapiddweller.domain.product;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.model.data.Uniqueness;

import java.util.Locale;

/**
 * Generates 8-digit EAN codes.<br/>
 * <br/>
 * Created: 30.07.2007 21:47:30
 *
 * @author Volker Bergmann
 */
public class EAN8Generator extends NonNullGeneratorWrapper<String, String> {

  private boolean unique;
  private boolean ordered;

  /**
   * Instantiates a new Ean 8 generator.
   */
  public EAN8Generator() {
    this(false);
  }

  /**
   * Instantiates a new Ean 8 generator.
   *
   * @param unique the unique
   */
  public EAN8Generator(boolean unique) {
    super(null);
    setUnique(unique);
  }

  /**
   * Instantiates a new Ean 8 generator.
   *
   * @param unique  the unique
   * @param ordered the ordered
   */
  public EAN8Generator(boolean unique, boolean ordered) {
    super(null);
    setUnique(unique);
    setOrdered(ordered);
  }

  /**
   * Is unique boolean.
   *
   * @return the boolean
   */
  public boolean isUnique() {
    return unique;
  }

  private void setUnique(boolean unique) {
    this.unique = unique;
  }

  /**
   * Is ordered boolean.
   *
   * @return the boolean
   */
  public boolean isOrdered() {
    return ordered;
  }

  /**
   * Sets ordered.
   *
   * @param ordered the ordered
   */
  public void setOrdered(boolean ordered) {
    this.ordered = ordered;
  }


  // Generator interface --------------------------------------------------------------------


  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    Uniqueness uniqueness = Uniqueness.instance(unique, ordered);
    setSource(context.getGeneratorFactory()
        .createRegexStringGenerator("[0-9]{7}", Locale.ENGLISH, 7, 7, uniqueness));
    super.init(context);
  }

  @Override
  public String generate() {
    assertInitialized();
    char[] chars = new char[8];
    generateFromNotNullSource().getChars(0, 7, chars, 0);
    chars[7] = chars[6];
    chars[6] = '0';
    int sum = 0;
    for (int i = 0; i < 8; i++) {
      sum += (chars[i] - '0') * (1 + (i % 2) * 2);
    }
    if (sum % 10 == 0) {
      chars[6] = '0';
    } else {
      chars[6] = (char) ('0' + 10 - (sum % 10));
    }
    return String.valueOf(chars);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + (unique ? "[unique]" : "");
  }

}
