/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;

/**
 * A generator proxy that forwards the output of another generator with a random number of repetitions.<br/><br/>
 * Created: 18.08.2007 17:08:10
 * @param <E> the type parameter
 * @author Volker Bergmann
 */
public class RepeatGeneratorProxy<E> extends CardinalGenerator<E, E> {

  private Integer repCount;
  private Integer totalReps;
  private E currentValue;

  public RepeatGeneratorProxy() {
    this(null, 0, 3);
  }

  public RepeatGeneratorProxy(Generator<E> source, int minRepetitions, int maxRepetitions) {
    this(source, minRepetitions, maxRepetitions, 1, SequenceManager.RANDOM_SEQUENCE);
  }

  public RepeatGeneratorProxy(Generator<E> source, int minRepetitions, int maxRepetitions,
                              int repetitionGranularity, Distribution repetitionDistribution) {
    super(source, true, minRepetitions, maxRepetitions, repetitionGranularity, repetitionDistribution);
  }

  @Override
  public Class<E> getGeneratedType() {
    return getSource().getGeneratedType();
  }

  @Override
  public void init(GeneratorContext context) {
    super.init(context);
    resetMembers();
  }

  private void resetMembers() {
    repCount = -1;
    totalReps = -1;
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    if (repCount == -1 || repCount >= totalReps) {
      wrapper = getSource().generate(wrapper);
      if (wrapper == null) {
        return null;
      } else {
        currentValue = wrapper.unwrap();
      }
      repCount = 0;
      totalReps = generateCardinal();
    } else {
      wrapper.wrap(currentValue);
      repCount++;
    }
    return wrapper.wrap(currentValue);
  }

  @Override
  public void reset() {
    super.reset();
    resetMembers();
  }

}
