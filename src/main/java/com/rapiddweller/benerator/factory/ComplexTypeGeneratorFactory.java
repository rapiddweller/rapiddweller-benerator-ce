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
import com.rapiddweller.benerator.TypedEntitySource;
import com.rapiddweller.benerator.composite.BlankEntityGenerator;
import com.rapiddweller.benerator.composite.ComponentTypeConverter;
import com.rapiddweller.benerator.composite.GenerationStep;
import com.rapiddweller.benerator.composite.SimpleTypeEntityGenerator;
import com.rapiddweller.benerator.composite.CompositeEntityGenerator;
import com.rapiddweller.benerator.distribution.DistributingGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.TypedEntitySourceAdapter;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.EntityPartSource;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.fixedwidth.FixedWidthColumnDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthRowTypeDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthUtil;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.benerator.FileFormat;
import com.rapiddweller.benerator.FileFormats;
import com.rapiddweller.platform.csv.CSVEntitySourceProvider;
import com.rapiddweller.platform.dbunit.DbUnitEntitySource;
import com.rapiddweller.platform.fixedwidth.FixedWidthEntitySource;
import com.rapiddweller.platform.xls.XLSEntitySourceProvider;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates entity generators from entity metadata.<br/><br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class ComplexTypeGeneratorFactory extends TypeGeneratorFactory<ComplexTypeDescriptor> {

  private static final ComplexTypeGeneratorFactory INSTANCE = new ComplexTypeGeneratorFactory();

  public static ComplexTypeGeneratorFactory getInstance() {
    return INSTANCE;
  }

  protected ComplexTypeGeneratorFactory() {
  }

  @Override
  protected Generator<?> applyComponentBuilders(Generator<?> generator, ComplexTypeDescriptor descriptor,
                                                String instanceName, Uniqueness uniqueness, BeneratorContext context) {
    generator = createMutatingEntityGenerator(instanceName, descriptor, uniqueness, context, generator);
    return super.applyComponentBuilders(generator, descriptor, instanceName, uniqueness, context);
  }

  @Override
  protected Generator<Entity> createSourceGenerator(
      ComplexTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    // if no sourceObject is specified, there's nothing to do
    String sourceSpec = descriptor.getSource();
    if (sourceSpec == null) {
      return null;
    }
    Object sourceObject = null;
    if (ScriptUtil.isScript(sourceSpec)) {
      Object tmp = ScriptUtil.evaluate(sourceSpec, context); // TODO v0.8 When to resolve scripts?
      if (tmp instanceof String) {
        sourceSpec = (String) tmp;
        sourceObject = context.get(sourceSpec);
      } else {
        sourceObject = tmp;
      }
    } else if (context.hasProductNameInScope(sourceSpec)) {
      String partName = StringUtil.lastToken(descriptor.getName(), '.');
      sourceObject = new EntityPartSource(sourceSpec, partName, context);
    } else {
      sourceObject = context.get(sourceSpec);
    }

    // create sourceObject generator

    Generator<Entity> generator = null;
    if (sourceObject != null) {
      generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
    } else {
      String segment = descriptor.getSegment();
      for (FileFormat format : FileFormats.all()) {
        if (format.matchesUri(sourceSpec)) {
          generator = createProtocolSourceGenerator(sourceSpec, format, descriptor, context);
          break;
        }
      }
      if (generator == null) {
        if (DataFileUtil.isXmlDocument(sourceSpec)) {
          generator = new DataSourceGenerator<>(new DbUnitEntitySource(sourceSpec, context));
        } else if (DataFileUtil.isCsvDocument(sourceSpec)) {
          generator = createCSVSourceGenerator(descriptor, context, sourceSpec);
        } else if (DataFileUtil.isFixedColumnWidthFile(sourceSpec)) {
          generator = createFixedColumnWidthSourceGenerator(descriptor, context, sourceSpec);
        } else if (DataFileUtil.isExcelDocument(sourceSpec)) {
          generator = createXLSSourceGenerator(descriptor, context, sourceSpec, segment);
        } else {
          try {
            BeanSpec sourceBeanSpec = DatabeneScriptParser.resolveBeanSpec(sourceSpec, context);
            sourceObject = sourceBeanSpec.getBean();
            generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
            if (sourceBeanSpec.isReference() && !(sourceObject instanceof StorageSystem)) {
              generator = WrapperFactory.preventClosing(generator);
            }
          } catch (Exception e) {
            throw new UnsupportedOperationException("Error resolving source: " + sourceSpec, e);
          }
        }
      }
    }
    if (generator.getGeneratedType() != Entity.class) {
      generator = new SimpleTypeEntityGenerator(generator, descriptor);
    }
    generator = applyFilter(descriptor, generator);
    generator = applyDistribution(descriptor, uniqueness, context, generator);
    return generator;
  }

  protected Generator<Entity> applyFilter(ComplexTypeDescriptor descriptor, Generator<Entity> generator) {
    return generator;
  }

  private Generator<Entity> applyDistribution(ComplexTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context,
                                              Generator<Entity> generator) {
    Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
    if (distribution != null) {
      generator = new DistributingGenerator<>(generator, distribution, uniqueness.isUnique());
    }
    return generator;
  }

  @Override
  protected Generator<?> createSpecificGenerator(ComplexTypeDescriptor descriptor, String instanceName,
                                                 boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
    return null;
  }

  @Override
  protected Generator<?> createHeuristicGenerator(ComplexTypeDescriptor type, String instanceName,
                                                  Uniqueness uniqueness, BeneratorContext context) {
    if (DescriptorUtil.isWrappedSimpleType(type)) {
      return createSimpleTypeEntityGenerator(type, uniqueness, context);
    } else {
      return new BlankEntityGenerator(type);
    }
  }

  @Override
  protected Class<?> getGeneratedType(ComplexTypeDescriptor descriptor) {
    return Entity.class;
  }

  @SuppressWarnings("unchecked")
  public static Generator<Entity> createMutatingEntityGenerator(String name, ComplexTypeDescriptor descriptor,
                                                           Uniqueness ownerUniqueness, BeneratorContext context, Generator<?> source) {
    List<GenerationStep<Entity>> generatorComponent =
        createMutatingGeneratorComponents(descriptor, ownerUniqueness, context);
    return new CompositeEntityGenerator(name, (Generator<Entity>) source, generatorComponent, context);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Generator<Entity> createSourceGeneratorFromObject(ComplexTypeDescriptor descriptor,
                                                                   BeneratorContext context, Object sourceObject) {
    Generator<Entity> generator;
    if (sourceObject instanceof StorageSystem) {
      StorageSystem storage = (StorageSystem) sourceObject;
      String selector = descriptor.getSelector();
      String subSelector = descriptor.getSubSelector();
      if (!StringUtil.isEmpty(subSelector)) {
        DataSource<Entity> dataSource = storage.queryEntities(descriptor.getName(), subSelector, context);
        generator = WrapperFactory.applyHeadCycler(new DataSourceGenerator<>(dataSource));
      } else {
        generator = new DataSourceGenerator<>(storage.queryEntities(descriptor.getName(), selector, context));
      }
    } else if (sourceObject instanceof Generator) {
      generator = (Generator<Entity>) sourceObject;
    } else if (sourceObject instanceof TypedEntitySource) {
      TypedEntitySource dataSource = (TypedEntitySource) sourceObject;
      generator = new DataSourceGenerator<>(new TypedEntitySourceAdapter(dataSource, descriptor));
    } else if (sourceObject instanceof EntitySource) {
      generator = new DataSourceGenerator<>((EntitySource) sourceObject);
    } else if (sourceObject instanceof DataSource) {
      DataSource dataSource = (DataSource) sourceObject;
      if (!Entity.class.isAssignableFrom(dataSource.getType())) {
        throw new UnsupportedOperationException("Not a supported data type to iterate: " + dataSource.getType());
      }
      generator = new DataSourceGenerator<Entity>(dataSource);
    } else {
      throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
    }
    return generator;
  }

  private static Generator<Entity> createProtocolSourceGenerator(String url, FileFormat format,
      ComplexTypeDescriptor complexType, BeneratorContext context) {
    DataSourceProvider<Entity> fileProvider = format.provider(url, null, complexType, context);
    return createEntitySourceGenerator(complexType, context, url, fileProvider);
  }

  private static Generator<Entity> createFixedColumnWidthSourceGenerator(
      ComplexTypeDescriptor descriptor, BeneratorContext context, String sourceName) {
    Generator<Entity> generator;
    String encoding = descriptor.getEncoding();
    if (encoding == null) {
      encoding = context.getDefaultEncoding();
    }
    String pattern = descriptor.getPattern();
    if (pattern == null) {
      throw new ConfigurationError("No pattern specified for FCW file import: " + sourceName);
    }
    try {
      FixedWidthRowTypeDescriptor rowDescriptor = FixedWidthUtil.parseBeanColumnsSpec(
          pattern, descriptor.getName(), null, context.getDefaultLocale());
      FixedWidthColumnDescriptor[] ffcd = rowDescriptor.getColumnDescriptors();
      Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
      FixedWidthEntitySource iterable = new FixedWidthEntitySource(sourceName, descriptor, scriptConverter, encoding, null, ffcd);
      iterable.setContext(context);
      generator = new DataSourceGenerator<>(iterable);
      return generator;
    } catch (ParseException e) {
      throw new ConfigurationError("Error parsing fixed-width pattern: " + pattern, e);
    }
  }

  private static Generator<Entity> createCSVSourceGenerator(
      ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
    String encoding = complexType.getEncoding();
    if (encoding == null) {
      encoding = context.getDefaultEncoding();
    }
    Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
    char separator = DescriptorUtil.getSeparator(complexType, context);
    DataSourceProvider<Entity> fileProvider = new CSVEntitySourceProvider(complexType, scriptConverter,
        separator, encoding);
    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
  }

  private static Generator<Entity> createXLSSourceGenerator(
      ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName, String segment) {
    ScriptConverterForStrings converter = new ScriptConverterForStrings(context);
    boolean formatted = isFormatted(complexType);
    XLSEntitySourceProvider fileProvider = new XLSEntitySourceProvider(complexType, formatted, converter);
    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
  }

  private static Generator<Entity> createSimpleTypeEntityGenerator(ComplexTypeDescriptor complexType,
                                                                   Uniqueness ownerUniqueness, BeneratorContext context) {
    TypeDescriptor contentType = complexType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getTypeDescriptor();
    Generator<?> generator = MetaGeneratorFactory.createTypeGenerator(
        contentType, complexType.getName(), false, ownerUniqueness, context);
    return new SimpleTypeEntityGenerator(generator, complexType);
  }

  @SuppressWarnings("unchecked")
  public static List<GenerationStep<Entity>> createMutatingGeneratorComponents(ComplexTypeDescriptor descriptor,
                                                                               Uniqueness ownerUniqueness, BeneratorContext context) {
    List<GenerationStep<Entity>> generatorComponents = new ArrayList<>();
    for (InstanceDescriptor part : descriptor.getDeclaredParts()) {
      if (!(part instanceof ComponentDescriptor) ||
          part.getMode() != Mode.ignored && !ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(part.getName())) {
        try {
          GenerationStep<Entity> generatorComponent =
              (GenerationStep<Entity>) GenerationStepFactory.createGeneratorComponent(part, ownerUniqueness, false, context);
          generatorComponents.add(generatorComponent);
        } catch (Exception e) {
          throw new ConfigurationError("Error creating component builder for " + part, e);
        }
      }
    }
    return generatorComponents;
  }

  private static Generator<Entity> createEntitySourceGenerator(ComplexTypeDescriptor complexType,
                                                               BeneratorContext context, String sourceName, DataSourceProvider<Entity> factory) {
    Generator<Entity> generator =
        SourceFactory.createRawSourceGenerator(complexType.getNesting(), complexType.getDataset(), sourceName, factory, Entity.class, context);
    generator = WrapperFactory.applyConverter(generator, new ComponentTypeConverter(complexType));
    return generator;
  }

}
