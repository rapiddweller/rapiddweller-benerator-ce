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
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * {@link DatasetBasedGenerator} implementation which bases on an atomic dataset.<br/><br/>
 * Created: 09.03.2011 10:54:28
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class AtomicDatasetGenerator<E> extends GeneratorProxy<E> implements WeightedDatasetGenerator<E> {

  private final String nesting;
  private final String dataset;
  private final double weight;

  /**
   * Instantiates a new Atomic dataset generator.
   *
   * @param source  the source
   * @param nesting the nesting
   * @param dataset the dataset
   */
  public AtomicDatasetGenerator(WeightedGenerator<E> source, String nesting, String dataset) {
    this(source, nesting, dataset, source.getWeight());
  }

  /**
   * Instantiates a new Atomic dataset generator.
   *
   * @param source  the source
   * @param nesting the nesting
   * @param dataset the dataset
   * @param weight  the weight
   */
  public AtomicDatasetGenerator(Generator<E> source, String nesting, String dataset, double weight) {
    super(source);
    this.nesting = nesting;
    this.dataset = dataset;
    this.weight = weight;
  }

  @Override
  public String getNesting() {
    return nesting;
  }

  @Override
  public String getDataset() {
    return dataset;
  }

  @Override
  public double getWeight() {
    return weight;
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    return super.generate(wrapper).setTag(nesting, dataset);
  }

  @Override
  public E generateForDataset(String requestedDataset) {
    if (!dataset.equals(requestedDataset)) {
      throw new IllegalArgumentException("Requested dataset " + requestedDataset + ", but supporting only dataset " + this.dataset);
    }
    ProductWrapper<E> wrapper = generate(getResultWrapper());
    if (wrapper == null) {
      return null;
    }
    return wrapper.unwrap();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + nesting + ":" + dataset + "]";
  }

}
