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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of the {@link DatasetBasedGenerator} interface.
 * It is configured with 'nesting' and 'dataset'. Depending on the type of
 * the dataset (atomic or composite), it initializes a delegate instance
 * of a {@link DatasetBasedGenerator}, either a {@link CompositeDatasetGenerator}
 * or an {@link AtomicDatasetGenerator}. For the dfinition of custom
 * {@link DatasetBasedGenerator}s, inherit from this class and implement
 * the abstract method {@link #createAtomicDatasetGenerator(Dataset, boolean)}.
 * All dataset recognition and handling and data generation will be handled
 * automatically.<br/><br/>
 * Created: 10.03.2011 10:44:58
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.6
 */
public abstract class AbstractDatasetGenerator<E> extends GeneratorProxy<E> implements DatasetBasedGenerator<E> {

  protected final Set<String> supportedDatasets;
  protected final boolean fallback;
  protected String nesting;
  protected String datasetName;
  protected double totalWeight;
  protected RandomProvider random;

  // constructor -----------------------------------------------------------------------------------------------------

  public AbstractDatasetGenerator(Class<E> generatedType, String nesting, String datasetName, boolean fallback) {
    super(generatedType);
    this.nesting = nesting;
    this.datasetName = datasetName;
    this.fallback = fallback;
    this.supportedDatasets = new HashSet<>();
    this.supportedDatasets.add(datasetName);
    this.totalWeight = 0;
    this.random = BeneratorFactory.getInstance().getRandomProvider();
  }

  public boolean supportsDataset(String datasetName) {
    return supportedDatasets.contains(datasetName);
  }

  // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

  @Override
  public String getNesting() {
    return nesting;
  }

  public void setNesting(String nesting) {
    this.nesting = nesting;
  }

  @Override
  public String getDataset() {
    return datasetName;
  }

  public void setDataset(String datasetName) {
    this.datasetName = datasetName;
    this.supportedDatasets.clear();
    this.supportedDatasets.add(datasetName);
  }

  public double getWeight() {
    return totalWeight;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
    setSource(createDatasetGenerator(dataset, true, fallback));
    super.init(context);
  }

  @Override
  public E generateForDataset(String requestedDataset) {
    DatasetBasedGenerator<E> sourceGen = getSource();
    if (sourceGen instanceof CompositeDatasetGenerator) {
      return sourceGen.generateForDataset(requestedDataset);
    } else { // assume that either the dataset matches or an appropriate failover has been chosen
      ProductWrapper<E> wrapper = sourceGen.generate(getResultWrapper());
      return (wrapper != null ? wrapper.unwrap() : null);
    }
  }

  public String randomDataset() {
    if (getSource() instanceof CompositeDatasetGenerator) {
      Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
      return random.randomElement(dataset.getSubSets()).getName();
    } else {
      return datasetName;
    }
  }


  // helper methods --------------------------------------------------------------------------------------------------

  protected WeightedDatasetGenerator<E> createDatasetGenerator(Dataset dataset, boolean required, boolean fallback) {
    WeightedDatasetGenerator<E> generator = null;
    if (!isAtomic(dataset)) {
      generator = createCompositeDatasetGenerator(dataset, fallback);
    }
    if (isAtomic(dataset) || (generator == null && required)) {
      generator = createAtomicDatasetGenerator(dataset, required);
    }
    if (generator != null) {
      supportedDatasets.add(dataset.getName());
    }
    return generator;
  }

  protected boolean isAtomic(Dataset dataset) {
    return dataset.isAtomic();
  }

  protected CompositeDatasetGenerator<E> createCompositeDatasetGenerator(Dataset dataset, boolean fallback) {
    CompositeDatasetGenerator<E> generator = new CompositeDatasetGenerator<>(nesting, dataset.getName(), fallback);
    for (Dataset subSet : dataset.getSubSets()) {
      WeightedDatasetGenerator<E> subGenerator = createDatasetGenerator(subSet, false, fallback);
      if (subGenerator != null) {
        generator.addSubDataset(subGenerator, subGenerator.getWeight());
      }
    }
    if (generator.getSource().getSources().size() > 0) {
      return generator;
    } else {
      return null;
    }
  }

  protected AtomicDatasetGenerator<E> createAtomicDatasetGenerator(Dataset dataset, boolean required) {
    WeightedGenerator<E> generator = createGeneratorForAtomicDataset(dataset);
    if (generator != null) {
      totalWeight += generator.getWeight();
      return new AtomicDatasetGenerator<>(generator, nesting, dataset.getName());
    }
    if (required) {
      throw new InvalidGeneratorSetupException("Unable to create generator for atomic dataset: " + dataset.getName());
    } else {
      return null;
    }
  }

  protected abstract WeightedGenerator<E> createGeneratorForAtomicDataset(Dataset dataset);

  @Override
  public DatasetBasedGenerator<E> getSource() {
    return (DatasetBasedGenerator<E>) super.getSource();
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + nesting + ':' + datasetName + ']';
  }

}
