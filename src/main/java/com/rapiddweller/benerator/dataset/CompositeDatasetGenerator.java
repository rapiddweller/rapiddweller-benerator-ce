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

package com.rapiddweller.benerator.dataset;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.wrapper.GeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.wrapper.WeightedGeneratorGenerator;

/**
 * {@link DatasetBasedGenerator} implementation which bases on a composite dataset.<br/><br/>
 * Created: 09.03.2011 11:01:04
 * @param <E> the type of generated data
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class CompositeDatasetGenerator<E> extends GeneratorWrapper<Generator<E>, E> implements WeightedDatasetGenerator<E> {

  private final String nesting;
  private final String datasetName;
  private final boolean performFallback;
  private DatasetBasedGenerator<E> fallbackGenerator;

  public CompositeDatasetGenerator(String nesting, String datasetName, boolean fallback) {
    super(new WeightedGeneratorGenerator<>());
    this.nesting = nesting;
    this.datasetName = datasetName;
    this.performFallback = fallback;
  }

  // properties ------------------------------------------------------------------------------------------------------

  @Override
  public WeightedGeneratorGenerator<E> getSource() {
    return (WeightedGeneratorGenerator<E>) super.getSource();
  }

  public void addSubDataset(DatasetBasedGenerator<E> generator, double weight) {
    getSource().addSource(generator, weight);
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public Class<E> getGeneratedType() {
    WeightedGeneratorGenerator<E> generatorGenerator = getSource();
    if (generatorGenerator.getSources().size() > 0) {
      return (Class<E>) generatorGenerator.getSource(0).getGeneratedType();
    }
    return (Class<E>) Object.class;
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    DatasetBasedGenerator<E> generator = randomAtomicGenerator();
    if (generator == null) {
      return null;
    }
    ProductWrapper<E> generation = generator.generate(getResultWrapper());
    if (generation == null) {
      return null;
    }
    return wrapper.wrap(generation.unwrap()).setTag(nesting, generator.getDataset());
  }

  // DatasetRelatedGenerator interface implementation ----------------------------------------------------------------

  @Override
  public String getNesting() {
    return nesting;
  }

  @Override
  public String getDataset() {
    return datasetName;
  }

  @Override
  public double getWeight() {
    return getSource().getWeight();
  }

  @Override
  public E generateForDataset(String dataset) {
    ProductWrapper<E> wrapper = getGeneratorForDataset(dataset, true).generate(getResultWrapper());
    if (wrapper == null) {
      return null;
    }
    return wrapper.unwrap();
  }

  // helper methods --------------------------------------------------------------------------------------------------

  private DatasetBasedGenerator<E> randomGenerator() {
    return (DatasetBasedGenerator<E>) getSource().generate(new ProductWrapper<>()).unwrap();
  }

  private DatasetBasedGenerator<E> randomAtomicGenerator() {
    DatasetBasedGenerator<E> generator = this;
    while (generator instanceof CompositeDatasetGenerator) {
      generator = ((CompositeDatasetGenerator<E>) generator).randomGenerator();
    }
    return generator;
  }

  @SuppressWarnings("unchecked")
  private DatasetBasedGenerator<E> getGeneratorForDataset(String requestedDataset, boolean required) {
    if (datasetName.equals(requestedDataset)) {
      return this;
    }
    for (Generator<? extends E> generator : getSource().getSources()) {
      DatasetBasedGenerator<E> dbGenerator = (DatasetBasedGenerator<E>) generator;
      if (dbGenerator.getDataset().equals(requestedDataset)) {
        return dbGenerator;
      }
      if (generator instanceof CompositeDatasetGenerator) {
        DatasetBasedGenerator<E> tmp = ((CompositeDatasetGenerator<E>) generator).getGeneratorForDataset(requestedDataset, false);
        if (tmp != null) {
          return tmp;
        }
      }
    }
    if (required) {
      if (performFallback) {
        if (fallbackGenerator == null) { // create fallback generator lazily
          logger.warn("requested dataset not found: {}", requestedDataset);
          Dataset datasetInstance = DatasetUtil.getDataset(DatasetUtil.REGION_NESTING, requestedDataset);
          // try each atomic data subset (this makes the first atomic subset the main one)
          createFallbackGeneratorFor(datasetInstance);
          if (fallbackGenerator == null) {
              throw new IllegalArgumentException("Unable to find sub generator for data subset " +
                  requestedDataset + " in " + this);
          }
        }
        return fallbackGenerator;
      } else {
        throw new IllegalArgumentException(getClass() + " did not find a sub generator for dataset '" +
            requestedDataset + "'");
      }
    } else {
      return null;
    }
  }

  private void createFallbackGeneratorFor(Dataset failedSet) {
    Dataset rootSet = DatasetUtil.getDataset(nesting, datasetName);
    if (!rootSet.contains(failedSet.getName())) {
      createFallbackGeneratorForFirstAtomicSubsetOf(rootSet);
      return;
    }
    for (Dataset parent : failedSet.getParents()) {
      for (Dataset sibling : parent.getSubSets()) {
        if (sibling.equals(failedSet)) {
          continue;
        }
        createFallbackGeneratorForFirstAtomicSubsetOf(sibling);
        if (fallbackGenerator != null) {
          break;
        }
      }
    }
  }

  private void createFallbackGeneratorForFirstAtomicSubsetOf(Dataset set) {
    if (set.isAtomic()) {
      fallbackGenerator = getGeneratorForDataset(set.getName(), false);
    } else {
      for (Dataset subset : set.getSubSets()) {
        createFallbackGeneratorForFirstAtomicSubsetOf(subset);
        if (fallbackGenerator != null) {
          return;
        }
      }
    }
  }

}
