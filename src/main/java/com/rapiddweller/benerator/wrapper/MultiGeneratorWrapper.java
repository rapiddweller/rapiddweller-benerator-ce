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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.AbstractGenerator;
import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ProgrammerError;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for wrapping several other generators (in a <i>sources</i> property)
 * and refining a composite state from them.<br/>
 * <br/>
 * Created: 19.12.2006 07:05:29
 *
 * @param <S> the type parameter
 * @param <P> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public abstract class MultiGeneratorWrapper<S, P> extends AbstractGenerator<P> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiGeneratorWrapper.class);

  /**
   * The Generated type.
   */
  protected final Class<P> generatedType;
  /**
   * The Sources.
   */
  protected final List<Generator<? extends S>> sources;
  private final List<Generator<? extends S>> availableSources;
  private final WrapperProvider<S> sourceWrapperProvider;

  /**
   * Instantiates a new Multi generator wrapper.
   *
   * @param generatedType the generated type
   * @param sources       the sources
   */
  @SafeVarargs
  public MultiGeneratorWrapper(Class<P> generatedType, Generator<? extends S>... sources) {
    this(generatedType, CollectionUtil.toList(sources));
  }

  /**
   * Instantiates a new Multi generator wrapper.
   *
   * @param generatedType the generated type
   * @param sources       the sources
   */
  public MultiGeneratorWrapper(Class<P> generatedType, List<Generator<? extends S>> sources) {
    this.generatedType = generatedType;
    this.sources = new ArrayList<>();
    this.availableSources = new ArrayList<>();
    this.sourceWrapperProvider = new WrapperProvider<>();
    setSources(sources);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets sources.
   *
   * @return the sources
   */
  public List<Generator<? extends S>> getSources() {
    return sources;
  }

  /**
   * Sets sources.
   *
   * @param sources the sources
   */
  public synchronized void setSources(List<Generator<? extends S>> sources) {
    this.sources.clear();
    for (Generator<? extends S> source : sources) {
      addSource(source);
    }
  }

  /**
   * Gets source.
   *
   * @param index the index
   * @return the source
   */
  public Generator<? extends S> getSource(int index) {
    return sources.get(index);
  }

  /**
   * Add source.
   *
   * @param source the source
   */
  public synchronized void addSource(Generator<? extends S> source) {
    sources.add(source);
  }

  /**
   * Available source count int.
   *
   * @return the int
   */
  protected int availableSourceCount() {
    return availableSources.size();
  }

  /**
   * Gets available source.
   *
   * @param index the index
   * @return the available source
   */
  protected Generator<? extends S> getAvailableSource(int index) {
    return availableSources.get(index);
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<P> getGeneratedType() {
    return generatedType;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    assertNotInitialized();
    if (sources.size() == 0) {
      throw new InvalidGeneratorSetupException("sources", "is empty");
    }
    makeAllGeneratorsAvailable();
    for (Generator<? extends S> source : sources) {
      if (source != null) { // some elements may be Mode.ignored
        source.init(context);
      }
    }
    super.init(context);
  }

  @Override
  public synchronized void reset() {
    for (Generator<? extends S> source : sources) {
      source.reset();
    }
    makeAllGeneratorsAvailable();
    super.reset();
  }

  @Override
  public synchronized void close() {
    for (Generator<? extends S> source : sources) {
      source.close();
    }
    this.availableSources.clear();
    super.close();
  }

  @Override
  public boolean isThreadSafe() {
    for (Generator<? extends S> source : sources) {
      if (!source.isThreadSafe()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isParallelizable() {
    for (Generator<? extends S> source : sources) {
      if (!source.isParallelizable()) {
        return false;
      }
    }
    return true;
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  /**
   * Gets source wrapper.
   *
   * @return the source wrapper
   */
  protected ProductWrapper<S> getSourceWrapper() {
    return sourceWrapperProvider.get();
  }

  /**
   * Generate from random source product wrapper.
   *
   * @param wrapper the wrapper
   * @return the product wrapper
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected synchronized ProductWrapper<S> generateFromRandomSource(ProductWrapper<S> wrapper) {
    assertInitialized();
    if (availableSources.size() == 0) {
      return null;
    }
    ProductWrapper test;
    do {
      int sourceIndex = RandomUtil.randomIndex(availableSources);
      test = availableSources.get(sourceIndex).generate((ProductWrapper) wrapper);
      if (test == null) {
        availableSources.remove(sourceIndex);
      }
    } while (test == null && availableSources.size() > 0);
    LOGGER.debug("generateFromRandomSource(): {}", test);
    return test;
  }

  /**
   * Generate from source product wrapper.
   *
   * @param index   the index
   * @param wrapper the wrapper
   * @return the product wrapper
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected ProductWrapper<String> generateFromSource(int index, ProductWrapper<S> wrapper) {
    assertInitialized();
    if (index < 0 || index > sources.size()) {
      throw new ProgrammerError("illegal generator index: " + index + " in " + this);
    }
    Generator<? extends S> source = sources.get(index);
    ProductWrapper test = source.generate((ProductWrapper) wrapper);
    if (test == null) {
      sources.remove(source);
    }
    return test;
  }

  /**
   * Generate from available source product wrapper.
   *
   * @param wrapper the wrapper
   * @return the product wrapper
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected ProductWrapper<S> generateFromAvailableSource(ProductWrapper<S> wrapper) {
    assertInitialized();
    if (0 >= availableSources.size()) {
      return null;
    }
    ProductWrapper test;
    do {
      test = availableSources.get(0).generate((ProductWrapper) wrapper);
      if (test == null) {
        availableSources.remove(0);
      }
    } while (test == null && 0 < availableSources.size());
    return test;
  }

  /**
   * Generate from all sources s [ ].
   *
   * @param componentType the component type
   * @return the s [ ]
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected synchronized S[] generateFromAllSources(Class<S> componentType) {
    assertInitialized();
    if (availableSources.size() < sources.size()) {
      return null;
    }
    S[] result = (S[]) Array.newInstance(componentType, sources.size());
    ProductWrapper elementWrapper = getSourceWrapper();
    for (int i = 0; i < sources.size(); i++) {
      elementWrapper = sources.get(i).generate(elementWrapper);
      if (elementWrapper == null) {
        return null;
      }
      S product = (S) elementWrapper.unwrap();
      result[i] = product;
    }
    return result;
  }

  private void makeAllGeneratorsAvailable() {
    this.availableSources.clear();
    availableSources.addAll(sources);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public synchronized String toString() {
    return getClass().getSimpleName() + sources;
  }

}
