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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Long Generator that implements a 'shuffle' Long Sequence:
 * It starts with min and produced numbers by continuously incrementing the cursor
 * by a fix <code>increment</code> value; when <code>max</code> is reached, it
 * repeats the procedure starting by min+granularity, later min+2*granularity and so on.
 * The generated numbers are unique as long as the generator is not reset.<br/><br/>
 * Created: 18.06.2006 14:40:29
 * @author Volker Bergmann
 * @since 0.1
 */
public class ShuffleLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  private long increment;
  private Long next;

  public ShuffleLongGenerator() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public ShuffleLongGenerator(long min, long max) {
    this(min, max, 2, 1);
  }

  public ShuffleLongGenerator(long min, long max, long granularity, long increment) {
    super(Long.class, min, max, granularity);
    this.increment = increment;
    reset();
  }

  // config properties -----------------------------------------------------------------------------------------------

  public long getIncrement() {
    return increment;
  }

  public void setIncrement(long increment) {
    this.increment = increment;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    if (granularity <= 0) {
      throw new InvalidGeneratorSetupException("Granularity must be greater than zero, but is " + granularity);
    }
    if (min < max && increment <= 0) {
      throw new InvalidGeneratorSetupException("Unsupported increment value: " + increment);
    }
    next = min;
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    if (next == null) {
      return null;
    }
    long result = next;
    if (next + increment <= max) {
      next += increment;
    } else {
      long newOffset = (next - min + granularity) % increment;
      next = (newOffset > 0 ? min + newOffset : null);
    }
    return result;
  }

  @Override
  public synchronized void reset() {
    this.next = min;
  }

}
