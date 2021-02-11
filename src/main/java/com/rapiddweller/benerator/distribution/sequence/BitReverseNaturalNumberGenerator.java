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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.common.NumberUtil;

/**
 * Long Generator that implements a 'bitreverse' Long Sequence.<br/>
 * <br/>
 * Created: 13.11.2007 14:39:29
 *
 * @author Volker Bergmann
 */
public class BitReverseNaturalNumberGenerator extends ThreadSafeNonNullGenerator<Long> {

  private long max;
  private long cursor;
  private int bitsUsed;
  private long maxCursor;

  /**
   * Instantiates a new Bit reverse natural number generator.
   */
  public BitReverseNaturalNumberGenerator() {
    this(Long.MAX_VALUE);
  }

  /**
   * Instantiates a new Bit reverse natural number generator.
   *
   * @param max the max
   */
  public BitReverseNaturalNumberGenerator(long max) {
    this.max = max;
  }

  // config properties -----------------------------------------------------------------------------------------------

  @Override
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

  /**
   * Sets max.
   *
   * @param max the max
   */
  public void setMax(Long max) {
    if (max < 0) {
      throw new IllegalArgumentException("No negative min supported, was: " + max);
    }
    this.max = max;
  }

  // generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    initMembers();
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    long result;
    do {
      result = cursorReversed();
      cursor++;
    } while (result > max && cursor < maxCursor);
    if (cursor >= maxCursor) {
      return null;
    }
    return result;
  }

  @Override
  public synchronized void reset() {
    initMembers();
    super.reset();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + renderState() + ']';
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void initMembers() {
    cursor = 0;
    bitsUsed = NumberUtil.bitsUsed(max);
    this.maxCursor = (1 << bitsUsed) + 1;
  }

  private long cursorReversed() {
    long result = 0;
    for (int i = 0; i <= bitsUsed; i++) {
      result |= ((cursor >> i) & 1) << (bitsUsed - i - 1);
    }
    return result;
  }

  private String renderState() {
    return "max=" + max + ", cursor=" + cursor;
  }

}
