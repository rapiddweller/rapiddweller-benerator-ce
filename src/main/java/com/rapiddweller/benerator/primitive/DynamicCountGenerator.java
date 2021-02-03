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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.script.Expression;

/**
 * Behaves similar to the {@link DynamicLongGenerator},
 * but generates <code>maxFallback</code> values, if <code>max</code> is set to <code>null</code>.<br/><br/>
 * Created: 28.03.2010 08:48:11
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DynamicCountGenerator extends DynamicLongGenerator {

  private boolean resetToMin;

  /**
   * Instantiates a new Dynamic count generator.
   */
  public DynamicCountGenerator() {
    super();
  }

  /**
   * Instantiates a new Dynamic count generator.
   *
   * @param min          the min
   * @param max          the max
   * @param granularity  the granularity
   * @param distribution the distribution
   * @param unique       the unique
   * @param resetToMin   the reset to min
   */
  public DynamicCountGenerator(Expression<Long> min, Expression<Long> max, Expression<Long> granularity,
                               Expression<? extends Distribution> distribution, Expression<Boolean> unique, boolean resetToMin) {
    super(min, max, granularity, distribution, unique);
    this.resetToMin = resetToMin;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected void resetMembers(Long minValue, Long maxValue) {
    if (maxValue != null) {
      super.resetMembers(minValue, maxValue);
    } else {
      // if it is not required to reset to min (<generate> or <iterate>), make it unlimited (returning null),
      // otherwise reset to the min value (component generation)
      Long constant = (resetToMin ? minValue : null);
      Generator<Long> source = new ConstantGenerator(constant);
      source.init(context);
      setSource(source);
    }
  }

}
