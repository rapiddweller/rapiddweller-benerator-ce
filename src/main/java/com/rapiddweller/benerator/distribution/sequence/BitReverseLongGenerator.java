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
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Generates integers reversing the bits of a continuously rising number.<br/><br/>
 * Created: 13.11.2007 15:42:27
 * @author Volker Bergmann
 */
public class BitReverseLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  public static final long MAX_INDEX_RANGE = (1L << 30) - 1;

  private BitReverseNaturalNumberGenerator indexGenerator;

  public BitReverseLongGenerator() {
    this(0, MAX_INDEX_RANGE);
  }

  public BitReverseLongGenerator(long min, long max) {
    this(min, max, 1);
  }

  public BitReverseLongGenerator(long min, long max, long granularity) {
    super(Long.class, min, max, granularity);
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    indexGenerator = new BitReverseNaturalNumberGenerator((max - min - 1 + granularity) / granularity);
    indexGenerator.init(context);
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    Long index = indexGenerator.generate();
    if (index == null) {
      return null;
    }
    return min + index * granularity;
  }

  @Override
  public void reset() {
    assertInitialized();
    super.reset();
    indexGenerator.reset();
  }

  @Override
  public void close() {
    assertInitialized();
    super.close();
    indexGenerator.close();
  }

}
