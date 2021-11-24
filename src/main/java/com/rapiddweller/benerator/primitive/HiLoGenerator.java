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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.util.AbstractNonNullGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Combines the output of a 'slow' generator (e.g. a remote hiGenerator)
 * with quickly generated numbers in a range: value = hi * maxLo + local.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class HiLoGenerator extends AbstractNonNullGenerator<Long> {

  private static final Logger logger = LoggerFactory.getLogger(HiLoGenerator.class);

  protected static final int DEFAULT_MAX_LO = 100;

  protected int maxLo;

  private int lo;
  private Long hi;

  protected NonNullGenerator<Long> hiGenerator;

  // constructors ----------------------------------------------------------------------------------------------------

  public HiLoGenerator() {
    this(new IncrementGenerator(), DEFAULT_MAX_LO);
  }

  public HiLoGenerator(int maxLo) {
    this(new IncrementGenerator(), DEFAULT_MAX_LO);
  }

  public HiLoGenerator(NonNullGenerator<Long> hiGenerator, int maxLo) {
    this.hiGenerator = hiGenerator;
    setMaxLo(maxLo);
    resetMembers();
  }

  // properties ------------------------------------------------------------------------------------

  public void setHiGenerator(NonNullGenerator<Long> hiGenerator) {
    this.hiGenerator = hiGenerator;
  }

  public int getMaxLo() {
    return maxLo;
  }

  public void setMaxLo(int maxLo) {
    if (maxLo <= 0) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("maxLo must be greater than 0, was: " + maxLo);
    }
    this.maxLo = maxLo;
  }

  // Generator interface -------------------------------------------------------------------

  @Override
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    assertNotInitialized();
    if (hiGenerator == null) {
      throw new InvalidGeneratorSetupException("hiGenerator", "is null");
    }
    hiGenerator.init(context);
    resetMembers();
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    if (hi == -1 || lo >= maxLo) {
      hi = hiGenerator.generate();
      if (hi == null) {
        return null;
      }
      logger.debug("fetched new hi value: {}", hi);
      lo = 0;
    } else {
      lo++;
    }
    return hi * (maxLo + 1) + lo;
  }

  @Override
  public void reset() {
    hiGenerator.reset();
    resetMembers();
    super.reset();
  }

  @Override
  public void close() {
    hiGenerator.close();
    super.close();
  }

  @Override
  public boolean isThreadSafe() {
    return hiGenerator.isThreadSafe();
  }

  @Override
  public boolean isParallelizable() {
    return hiGenerator.isParallelizable();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + maxLo + ',' + hiGenerator + ']';
  }

  private void resetMembers() {
    this.lo = -1;
    this.hi = -1L;
  }

}
