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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.sample.AttachedWeightSampleGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Weighted;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Generator} that wraps several other 'source generators' and assigns a weight to each one.
 * Calls to {@link Generator#generate(ProductWrapper)} are forwarded to a random source generator, with a probability
 * proportional to its assigned weight. If a source generator becomes unavailable, its weight is
 * ignored.<br/><br/>
 * Created: 09.03.2011 07:59:04
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class WeightedGeneratorGenerator<E> extends MultiGeneratorWrapper<E, Generator<E>> implements Weighted {

  private final List<Double> sourceWeights;
  private AttachedWeightSampleGenerator<Integer> indexGenerator;
  private double totalWeight;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public WeightedGeneratorGenerator() {
    super((Class) Generator.class);
    this.sourceWeights = new ArrayList<>();
    this.totalWeight = 0;
  }

  @Override
  public double getWeight() {
    return totalWeight;
  }

  public List<Double> getSourceWeights() {
    return sourceWeights;
  }

  @Override
  public void addSource(Generator<? extends E> source) {
    addSource(source, 1.);
  }

  public void addSource(Generator<? extends E> source, Double weight) {
    if (weight == null) {
      weight = 1.;
    }
    this.sourceWeights.add(weight);
    super.addSource(source);
    this.totalWeight += weight;
  }

  private void createAndInitIndexGenerator() {
    indexGenerator = new AttachedWeightSampleGenerator<>(Integer.class);
    for (int i = 0; i < sourceWeights.size(); i++) {
      indexGenerator.addSample(i, sourceWeights.get(i));
    }
    indexGenerator.init(context);
  }

  @Override
  public void init(GeneratorContext context) {
    super.init(context);
    createAndInitIndexGenerator();
  }

  @Override
  @SuppressWarnings("unchecked")
  public ProductWrapper<Generator<E>> generate(ProductWrapper<Generator<E>> wrapper) {
    assertInitialized();
    if (availableSourceCount() == 0) {
      return null;
    }
    int sourceIndex = GeneratorUtil.generateNonNull(indexGenerator);
    Generator<E> result = (Generator<E>) getAvailableSource(sourceIndex);
    return wrapper.wrap(result);
  }

  @Override
  public void reset() {
    super.reset();
    createAndInitIndexGenerator();
  }

  @Override
  public void close() {
    super.close();
    IOUtil.close(indexGenerator);
  }

}
