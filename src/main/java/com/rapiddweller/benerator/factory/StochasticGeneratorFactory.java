/*
 * (c) Copyright 2006-2022 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorProvider;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.primitive.BooleanGenerator;
import com.rapiddweller.benerator.primitive.IncrementalStringGenerator;
import com.rapiddweller.benerator.primitive.UniqueScrambledStringGenerator;
import com.rapiddweller.benerator.sample.AttachedWeightSampleGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.sample.OneShotGenerator;
import com.rapiddweller.benerator.sample.SampleGenerator;
import com.rapiddweller.benerator.wrapper.AlternativeGenerator;
import com.rapiddweller.benerator.wrapper.CompositeStringGenerator;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.GeneratorChain;
import com.rapiddweller.benerator.wrapper.SimpleMultiSourceArrayGenerator;
import com.rapiddweller.benerator.wrapper.UniqueMultiSourceArrayGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.format.array.ArrayDataSource;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedSample;

import java.util.Collection;
import java.util.Set;

/**
 * {@link GeneratorFactory} implementation that generates docile data in order to avoid functional failures
 * and combines them randomly and repetitively for generating large data volumes. Its primary purpose is
 * data generation for performance tests.<br/><br/>
 * Created: 04.07.2011 09:34:34
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class StochasticGeneratorFactory extends GeneratorFactory {

  public StochasticGeneratorFactory() {
    super(new GentleDefaultsProvider());
  }

  @Override
  public <T> Generator<T> createAlternativeGenerator(Class<T> targetType, Generator<T>[] sources,
                                                     Uniqueness uniqueness) {
    if (uniqueness == Uniqueness.ORDERED) {
      return new GeneratorChain<>(targetType, uniqueness.isUnique(), sources);
    } else {
      return new AlternativeGenerator<>(targetType, sources);
    }
  }

  @Override
  public <T> Generator<T[]> createCompositeArrayGenerator(
      Class<T> componentType, Generator<T>[] sources, Uniqueness uniqueness) {
    if (uniqueness.isUnique()) {
      return new UniqueMultiSourceArrayGenerator<>(componentType, sources);
    } else {
      return new SimpleMultiSourceArrayGenerator<>(componentType, sources);
    }
  }

  @Override
  public <T> Generator<T> createSampleGenerator(Collection<T> values,
                                                Class<T> generatedType, boolean unique) {
    SampleGenerator<T> generator = new SampleGenerator<>(generatedType, values);
    generator.setUnique(unique);
    return generator;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
                                                        Distribution distribution, boolean unique) {
    WeightedSample<T>[] samples = (WeightedSample<T>[]) DatabeneScriptParser.parseWeightedLiteralList(valueSpec);
    if (distribution == null && !unique && weightsUsed(samples)) {
      AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<>(targetType);
      for (WeightedSample<T> sample : samples) {
        if (sample.getValue() == null) {
          throw BeneratorExceptionFactory.getInstance().configurationError(
              "null is not supported in values='...', drop it from the list and use a nullQuota instead");
        }
        generator.addSample(sample.getValue(), sample.getWeight());
      }
      return generator;
    } else {
      String[] values = new String[samples.length];
      for (int i = 0; i < samples.length; i++) {
        T rawValue = samples[i].getValue();
        String value = ToStringConverter.convert(rawValue, null);
        values[i] = value;
      }
      DataSourceGenerator<String> source = new DataSourceGenerator<>(new ArrayDataSource<>(values, String.class));
      if (distribution == null) {
        distribution = SequenceManager.RANDOM_SEQUENCE;
      }
      Converter<String, T> converter = ConverterManager.getInstance().createConverter(String.class, targetType);
      Generator<T> gen = WrapperFactory.applyConverter(source, converter);
      return distribution.applyTo(gen, unique);
    }
  }

  @Override
  public Generator<Boolean> createBooleanGenerator(Double trueQuota) {
    return new BooleanGenerator(trueQuota != null ? trueQuota : 0.5);
  }

  private static boolean weightsUsed(WeightedSample<?>[] samples) {
    for (WeightedSample<?> sample : samples) {
      if (sample.getWeight() != 1) {
        return true;
      }
    }
    return false;
  }

  @Override
  public NonNullGenerator<String> createStringGenerator(Set<Character> chars,
                                                        Integer minLength, Integer maxLength, int lengthGranularity, Distribution lengthDistribution,
                                                        Uniqueness uniqueness) {
    if (uniqueness == Uniqueness.ORDERED) {
      return new IncrementalStringGenerator(chars, minLength, maxLength, lengthGranularity);
    } else if (uniqueness.isUnique()) {
      return new UniqueScrambledStringGenerator(chars, minLength, maxLength);
    } else {
      return BeneratorFactory.getInstance().createVarLengthStringGenerator(
          chars, minLength, maxLength, lengthGranularity, lengthDistribution);
    }
  }

  @Override
  public Generator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota) {
    if (nullQuota == null) {
      if (nullable == null) {
        nullable = defaultsProvider.defaultNullable();
      }
      if (nullable) {
        nullQuota = (defaultsProvider.defaultNullQuota() != 1 ? defaultsProvider.defaultNullQuota() : 0);
      } else {
        nullQuota = 0.;
      }
    }
    return WrapperFactory.injectNulls(source, nullQuota);
  }

  @Override
  public <T> Generator<T> createSingleValueGenerator(T value, boolean unique) {
    if (unique) {
      return new OneShotGenerator<>(value);
    } else {
      return new ConstantGenerator<>(value);
    }
  }

  @Override
  protected double defaultTrueQuota() {
    return 0.5;
  }

  @Override
  protected Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required) {
    switch (uniqueness) {
      case ORDERED:
        return SequenceManager.STEP_SEQUENCE;
      case SIMPLE:
        return SequenceManager.EXPAND_SEQUENCE;
      default:
        if (required) {
          return SequenceManager.RANDOM_SEQUENCE;
        } else {
          return null;
        }
    }
  }

  @Override
  public Distribution defaultDistribution(Uniqueness uniqueness) {
    if (uniqueness == null) {
      return SequenceManager.STEP_SEQUENCE;
    }
    switch (uniqueness) {
      case ORDERED:
        return SequenceManager.STEP_SEQUENCE;
      case SIMPLE:
        return SequenceManager.EXPAND_SEQUENCE;
      default:
        return SequenceManager.RANDOM_SEQUENCE;
    }
  }

  @Override
  protected boolean defaultUnique() {
    return false;
  }

  @Override
  public <T> Generator<T> createNullGenerator(Class<T> generatedType) {
    return new ConstantGenerator<>(null, generatedType);
  }

  /** The resulting generator is an {@link AlternativeGenerator} with one alternative for each part count. */
  @SuppressWarnings("unchecked")
  @Override
  public NonNullGenerator<String> createCompositeStringGenerator(
      GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, Uniqueness uniqueness) {
    AlternativeGenerator<String> result = new AlternativeGenerator<>(String.class);
    for (int partCount = minParts; partCount <= maxParts; partCount++) {
      if (partCount == 0) {
        // for partCount == 0 an empty string is returned
        result.addSource(new ConstantGenerator<>(""));
      } else {
        // for partCount >= 1 the partGeneratorProvider is called
        Generator<String>[] sources = new Generator[partCount];
        for (int i = 0; i < partCount; i++) {
          sources[i] = WrapperFactory.asStringGenerator(partGeneratorProvider.create());
        }
        result.addSource(new CompositeStringGenerator(uniqueness.isUnique(), sources));
      }
    }
    return WrapperFactory.asNonNullGenerator(result);
  }

}
