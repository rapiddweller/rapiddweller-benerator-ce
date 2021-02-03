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

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.util.Arrays;

/**
 * Generates int arrays in the same manner in which decimal numbers are used.
 * It can be used to generate numbers or strings of arbitrary numerical radix.<br/><br/>
 * Created: 01.08.2011 15:15:39
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class IncrementalIntsGenerator extends ThreadSafeNonNullGenerator<int[]> {

  private final int radix;
  private final int[] digits;
  private boolean overrun;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Incremental ints generator.
   *
   * @param radix  the radix
   * @param length the length
   */
  public IncrementalIntsGenerator(int radix, int length) {
    this.radix = radix;
    this.digits = new int[length];
    Arrays.fill(digits, 0);
    this.overrun = false;
  }

  // interface -------------------------------------------------------------------------------------------------------

  /**
   * Gets radix.
   *
   * @return the radix
   */
  public int getRadix() {
    return radix;
  }

  /**
   * Gets length.
   *
   * @return the length
   */
  public int getLength() {
    return digits.length;
  }

  @Override
  public Class<int[]> getGeneratedType() {
    return int[].class;
  }

  @Override
  public int[] generate() {
    if (overrun) {
      return null;
    }
    int[] result = digits.clone();
    incrementDigit(digits.length - 1);
    return result;
  }

  @Override
  public void reset() {
    super.reset();
    Arrays.fill(digits, 0);
    this.overrun = false;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void incrementDigit(int i) {
    if (i < 0) {
      overrun = true;
      return;
    }
    if (digits[i] < radix - 1) {
      digits[i]++;
    } else {
      digits[i] = 0;
      if (i > 0) {
        incrementDigit(i - 1);
      } else {
        overrun = true;
      }
    }
  }

}