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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.IOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates values from a non-weighted list of samples, applying an explicitly defined distribution.<br/><br/>
 * Created: 07.06.2006 19:04:08
 * @param <E> the type of the generated objects
 * @author Volker Bergmann
 * @since 0.1
 */
public class SampleGenerator<E> extends AbstractSampleGenerator<E> {

  /** Holds the Sample information. */
  private final List<E> samples;

  /** {@link Distribution} for choosing a List index of the sample list. */
  private final Distribution distribution;

  /** Requires the generator to use each index only one times if set to true. */
  private boolean unique;

  /** Generates an index based on the {@link #distribution}. */
  private NonNullGenerator<Integer> indexGenerator;

  private static final RandomProvider random = BeneratorFactory.getInstance().getRandomProvider();


  // constructors ----------------------------------------------------------------------------------------------------

  public SampleGenerator() {
    this(null);
  }

  public SampleGenerator(Class<E> generatedType) {
    this(generatedType, new ArrayList<>());
  }

  /** Initializes the generator with a sample list. */
  @SafeVarargs
  public SampleGenerator(Class<E> generatedType, E... values) {
    this(generatedType, SequenceManager.RANDOM_SEQUENCE, values);
    setValues(values);
  }

  /** Initializes the generator with a sample list. */
  @SafeVarargs
  public SampleGenerator(Class<E> generatedType, Distribution distribution, E... values) {
    this(generatedType, distribution, false, CollectionUtil.toList(values));
  }

  /** Initializes the generator with a sample list. */
  public SampleGenerator(Class<E> generatedType, Iterable<E> values) {
    this(generatedType, SequenceManager.RANDOM_SEQUENCE, false, values);
  }

  /** Initializes the generator with a sample list. */
  public SampleGenerator(Class<E> generatedType, Distribution distribution, boolean unique, Iterable<E> values) {
    super(generatedType);
    this.samples = new ArrayList<>();
    this.distribution = distribution;
    this.unique = unique;
    this.indexGenerator = null;
    setValues(values);
  }


  // properties ------------------------------------------------------------------------------------------------------

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  @Override
  public long getVariety() {
    return samples.size();
  }

  public boolean contains(E value) {
    return samples.contains(value);
  }


  // interface ---------------------------------------------------------------------------------------------------------

  @Override
  public void clear() {
    this.samples.clear();
  }

  @Override
  public <T extends E> void addValue(T value) {
    if (unique && this.contains(value)) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "Trying to add a duplicate value (" + value + ") to unique generator: " + this);
    }
    samples.add(value);
  }


  // Generator implementation ------------------------------------------------------------------------------------------

  /** Initializes all attributes */
  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    if (samples.isEmpty()) {
      throw new InvalidGeneratorSetupException("No samples defined in " + this);
    } else {
      if(distribution != null){
        indexGenerator = distribution.createNumberGenerator(Integer.class, 0, samples.size() - 1, 1, unique);
        indexGenerator.init(context);
      } else {
        indexGenerator = SequenceManager.RANDOM_SEQUENCE.createNumberGenerator(Integer.class, 0, samples.size() - 1, 1, unique);
        indexGenerator.init(context);
      }
    }
    super.init(context);
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    Integer index;
    if (!samples.isEmpty() && (index = indexGenerator.generate()) != null) {
      return wrapper.wrap(samples.get(index));
    } else {
      return null;
    }
  }

  @Override
  public void reset() {
    indexGenerator.reset();
    super.reset();
  }

  @Override
  public void close() {
    IOUtil.close(indexGenerator);
    super.close();
  }


  // static utility methods --------------------------------------------------------------------------------------------

  /** Convenience utility method that chooses one sample out of a list with uniform random distribution. */
  @SafeVarargs
  public static <T> T generate(T... samples) {
    return samples[random.randomInt(samples.length)];
  }

  /** Convenience utility method that chooses one sample out of a list with uniform random distribution. */
  public static <T> T generate(List<T> samples) {
    return samples.get(random.randomInt(samples.size()));
  }


  // java.lang.Object overrides ----------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
