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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.composite.ArrayElementTypeConverter;
import com.rapiddweller.benerator.composite.BlankArrayGenerator;
import com.rapiddweller.benerator.distribution.DistributingGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.util.FilteringGenerator;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.format.script.ScriptConverterForObjects;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.array.Entity2ArrayConverter;
import com.rapiddweller.platform.csv.CSVArraySourceProvider;
import com.rapiddweller.platform.xls.XLSArraySourceProvider;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.Expression;

/**
 * Creates array {@link Generator}s.<br/><br/>
 * Created: 29.04.2010 07:45:18
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class ArrayTypeGeneratorFactory extends TypeGeneratorFactory<ArrayTypeDescriptor> {

  @Override
  protected Generator<Object[]> createSourceGenerator(
      ArrayTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    // if no sourceObject is specified, there's nothing to do
    String sourceName = descriptor.getSource();
    if (sourceName == null) {
      return null;
    }
    // create sourceObject generator
    Generator<Object[]> generator = null;
    Object contextSourceObject = context.get(sourceName);
    if (contextSourceObject != null) {
      generator = createSourceGeneratorFromObject(descriptor, context, contextSourceObject);
    } else {
      if (DataFileUtil.isCsvDocument(sourceName)) {
        generator = createCSVSourceGenerator(descriptor, context, sourceName);
      } else if (DataFileUtil.isExcelDocument(sourceName)) {
        generator = createXLSSourceGenerator(descriptor, context, sourceName);
      } else {
        try {
          BeanSpec sourceBeanSpec = DatabeneScriptParser.resolveBeanSpec(sourceName, context);
          Object sourceObject = sourceBeanSpec.getBean();
          if (!sourceBeanSpec.isReference() && sourceObject instanceof ContextAware) {
            ((ContextAware) sourceObject).setContext(context);
          }
          generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
          if (sourceBeanSpec.isReference()) {
            generator = WrapperFactory.preventClosing(generator);
          }
          return generator;
        } catch (Exception e) {
          throw new UnsupportedOperationException("Unknown source type: " + sourceName);
        }
      }
    }
    if (descriptor.getFilter() != null) {
      Expression<Boolean> filter
          = new ScriptExpression<>(ScriptUtil.parseScriptText(descriptor.getFilter()));
      generator = new FilteringGenerator<>(generator, filter);
    }
    Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
    if (distribution != null) {
      generator = new DistributingGenerator<>(generator, distribution, uniqueness.isUnique());
    }
    return generator;
  }

  @Override
  protected Generator<?> createSpecificGenerator(ArrayTypeDescriptor descriptor, String instanceName,
                                                 boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
    return null;
  }

  @Override
  protected Generator<?> createHeuristicGenerator(
      ArrayTypeDescriptor descriptor, String instanceName,
      Uniqueness uniqueness, BeneratorContext context) {
    return new BlankArrayGenerator(descriptor.getElementCount());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected Generator<?> applyComponentBuilders(Generator<?> generator, boolean iterationMode, ArrayTypeDescriptor descriptor, String instanceName,
                                                Uniqueness uniqueness, BeneratorContext context) {
    Generator[] generators;
    // create synthetic element generators if necessary
    if (generator instanceof BlankArrayGenerator) {
      generators = createSyntheticElementGenerators(descriptor, uniqueness, context);
      generator = context.getGeneratorFactory().createCompositeArrayGenerator(
          Object.class, generators, uniqueness);
    }
    // ... and don't forget to support the parent class' functionality
    generator = super.applyComponentBuilders(generator, iterationMode, descriptor, instanceName, uniqueness, context);
    return generator;
  }

  @Override
  protected Class<?> getGeneratedType(ArrayTypeDescriptor descriptor) {
    return Object[].class;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private Generator<Object[]> createCSVSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
                                                       String sourceName) {
    logger.debug("createCSVSourceGenerator({})", arrayType);
    String encoding = arrayType.getEncoding();
    if (encoding == null) {
      encoding = context.getDefaultEncoding();
    }
    char separator = DescriptorUtil.getSeparator(arrayType, context);
    boolean rowBased = (arrayType.isRowBased() != null ? arrayType.isRowBased() : true);
    DataSourceProvider<Object[]> factory =
        new CSVArraySourceProvider(arrayType.getName(), new ScriptConverterForStrings(context), rowBased, separator, encoding);
    Generator<Object[]> generator =
        SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
    return WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
  }

  private Generator<Object[]> createXLSSourceGenerator(
      ArrayTypeDescriptor arrayType, BeneratorContext context, String sourceName) {
    logger.debug("createXLSSourceGenerator({})", arrayType);
    boolean rowBased = (arrayType.isRowBased() != null ? arrayType.isRowBased() : true);
    String emptyMarker = arrayType.getEmptyMarker();
    String nullMarker = arrayType.getNullMarker();
    boolean formatted = isFormatted(arrayType);
    DataSourceProvider<Object[]> factory = new XLSArraySourceProvider(formatted,
        new ScriptConverterForObjects(context), emptyMarker, nullMarker, rowBased);
    Generator<Object[]> generator =
        SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
    generator = WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
    return generator;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Generator<Object[]> createSourceGeneratorFromObject(ArrayTypeDescriptor descriptor,
                                                                     BeneratorContext context, Object sourceObject) {
    Generator<Object[]> generator;
    if (sourceObject instanceof StorageSystem) {
      StorageSystem storage = (StorageSystem) sourceObject;
      String selector = descriptor.getSelector();
      String subSelector = descriptor.getSubSelector();
      if (!StringUtil.isEmpty(subSelector)) {
        generator = WrapperFactory.applyHeadCycler(new DataSourceGenerator(storage.query(subSelector, false, context)));
      } else {
        generator = new DataSourceGenerator(storage.query(selector, false, context));
      }
    } else if (sourceObject instanceof EntitySource) {
      DataSourceGenerator<Entity> entityGenerator = new DataSourceGenerator<>((EntitySource) sourceObject);
      generator = WrapperFactory.applyConverter(entityGenerator, new Entity2ArrayConverter());
    } else if (sourceObject instanceof Generator) {
      generator = (Generator<Object[]>) sourceObject;
    } else {
      throw new ConfigurationError("Source type not supported: " + sourceObject.getClass());
    }
    return generator;
  }

  @SuppressWarnings("rawtypes")
  private Generator<?>[] createSyntheticElementGenerators(
      ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
    Generator[] result = new Generator[arrayType.getElementCount()];
    for (int i = 0; i < arrayType.getElementCount(); i++) {
      ArrayElementDescriptor element = getElementOfTypeOrParents(arrayType, i);
      if (element.getMode() != Mode.ignored) {
        Generator<?> generator = InstanceGeneratorFactory.createSingleInstanceGenerator(
            element, uniqueness, context);
        result[i] = generator;
      }
    }
    return result;
  }

  protected ArrayElementDescriptor getElementOfTypeOrParents(ArrayTypeDescriptor arrayType, int index) {
    ArrayTypeDescriptor tmp = arrayType;
    ArrayElementDescriptor result;
    while ((result = tmp.getElement(index)) == null && tmp.getParent() != null) {
      tmp = tmp.getParent();
    }
    return result;
  }

}
