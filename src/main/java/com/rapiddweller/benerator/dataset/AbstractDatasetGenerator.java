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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.util.RandomUtil;
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

  /**
   * The Supported datasets.
   */
  protected final Set<String> supportedDatasets;
  /**
   * The Fallback.
   */
  protected final boolean fallback;
  /**
   * The Nesting.
   */
  protected String nesting;
  /**
   * The Dataset name.
   */
  protected String datasetName;
  /**
   * The Total weight.
   */
  protected double totalWeight;

  // constructor -----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Abstract dataset generator.
   *
   * @param generatedType the generated type
   * @param nesting       the nesting
   * @param datasetName   the dataset name
   * @param fallback      the fallback
   */
  public AbstractDatasetGenerator(Class<E> generatedType, String nesting, String datasetName, boolean fallback) {
    super(generatedType);
    this.nesting = nesting;
    this.datasetName = datasetName;
    this.fallback = fallback;
    this.supportedDatasets = new HashSet<>();
    this.supportedDatasets.add(datasetName);
    this.totalWeight = 0;
  }

  /**
   * Supports dataset boolean.
   *
   * @param datasetName the dataset name
   * @return the boolean
   */
  public boolean supportsDataset(String datasetName) {
    return supportedDatasets.contains(datasetName);
  }

  // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

  @Override
  public String getNesting() {
    return nesting;
  }

  /**
   * Sets nesting.
   *
   * @param nesting the nesting
   */
  public void setNesting(String nesting) {
    this.nesting = nesting;
  }

  @Override
  public String getDataset() {
    return datasetName;
  }

  /**
   * Sets dataset.
   *
   * @param datasetName the dataset name
   */
  public void setDataset(String datasetName) {
    this.datasetName = datasetName;
    this.supportedDatasets.clear();
    this.supportedDatasets.add(datasetName);
  }

  /**
   * Gets weight.
   *
   * @return the weight
   */
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

  /**
   * Random dataset string.
   *
   * @return the string
   */
  public String randomDataset() {
    if (getSource() instanceof CompositeDatasetGenerator) {
      Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
      return RandomUtil.randomElement(dataset.getSubSets()).getName();
    } else {
      return datasetName;
    }
  }


  // helper methods --------------------------------------------------------------------------------------------------

  /**
   * Create dataset generator weighted dataset generator.
   *
   * @param dataset  the dataset
   * @param required the required
   * @param fallback the fallback
   * @return the weighted dataset generator
   */
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

  /**
   * Is atomic boolean.
   *
   * @param dataset the dataset
   * @return the boolean
   */
  protected boolean isAtomic(Dataset dataset) {
    return dataset.isAtomic();
  }

  /**
   * Create composite dataset generator composite dataset generator.
   *
   * @param dataset  the dataset
   * @param fallback the fallback
   * @return the composite dataset generator
   */
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

  /**
   * Create atomic dataset generator atomic dataset generator.
   *
   * @param dataset  the dataset
   * @param required the required
   * @return the atomic dataset generator
   */
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

  /**
   * Create generator for atomic dataset weighted generator.
   *
   * @param dataset the dataset
   * @return the weighted generator
   */
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
