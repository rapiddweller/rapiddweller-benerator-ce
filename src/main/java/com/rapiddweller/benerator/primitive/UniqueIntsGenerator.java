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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.sequence.BitReverseNaturalNumberGenerator;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;

/**
 * Creates unique pseudo-random int arrays.<br/><br/>
 * Created: 01.08.2011 17:00:57
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class UniqueIntsGenerator extends NonNullGeneratorProxy<int[]> {

  private int[] digits;
  private final int[] displayColumn;
  private final int[] digitOffsets;
  private int cycleCounter;

  /**
   * Instantiates a new Unique ints generator.
   *
   * @param radix  the radix
   * @param length the length
   */
  public UniqueIntsGenerator(int radix, int length) {
    super(new IncrementalIntsGenerator(radix, length));
    this.displayColumn = new int[length];
    this.digitOffsets = new int[length];
  }

  @Override
  public IncrementalIntsGenerator getSource() {
    return (IncrementalIntsGenerator) super.getSource();
  }

  /**
   * Gets radix.
   *
   * @return the radix
   */
  public int getRadix() {
    return getSource().getRadix();
  }

  /**
   * Gets length.
   *
   * @return the length
   */
  public int getLength() {
    return getSource().getLength();
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    assertNotInitialized();
    int length = getLength();
    int radix = getRadix();
    NonNullGenerator<Long> colGen = new BitReverseNaturalNumberGenerator(length - 1);
    colGen.init(context);
    for (int i = 0; i < length; i++) {
      this.displayColumn[i] = colGen.generate().intValue();
      this.digitOffsets[i] = (length - 1 - this.displayColumn[i]) % radix;
    }
    resetMembers();
    super.init(context);
  }

  @Override
  public int[] generate() {
    if (digits == null) {
      return null;
    }
    int length = getLength();
    int radix = getRadix();
    int[] buffer = new int[length];
    for (int i = 0; i < digits.length; i++) {
      buffer[displayColumn[i]] = (digits[i] + digitOffsets[i] + cycleCounter) % radix;
    }
    if (cycleCounter < radix - 1 && length > 0) {
      cycleCounter++;
    } else {
      digits = super.generate();
      if (radix == 1 || (digits != null && digits[0] > 0)) {
        // counter + cycle have run through all combinations
        digits = null;
      }
      cycleCounter = 0;
    }
    return buffer;
  }

  @Override
  public void reset() {
    super.reset();
    resetMembers();
  }

  private void resetMembers() {
    this.digits = super.generate().clone();
    this.cycleCounter = 0;
  }

}
