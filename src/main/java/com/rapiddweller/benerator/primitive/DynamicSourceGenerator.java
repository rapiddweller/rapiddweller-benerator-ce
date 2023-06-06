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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.*;
import com.rapiddweller.benerator.composite.ComponentTypeConverter;
import com.rapiddweller.benerator.composite.SimpleTypeEntityGenerator;
import com.rapiddweller.benerator.distribution.DistributingGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.TypedEntitySourceAdapter;
import com.rapiddweller.benerator.factory.*;
import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.*;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.fixedwidth.FixedWidthColumnDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthRowTypeDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthUtil;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.format.util.ThreadLocalDataContainer;
import com.rapiddweller.model.data.*;
import com.rapiddweller.platform.csv.CSVEntitySourceProvider;
import com.rapiddweller.platform.dbunit.DbUnitEntitySource;
import com.rapiddweller.platform.fixedwidth.FixedWidthEntitySource;
import com.rapiddweller.platform.xls.PlatformDescriptor;
import com.rapiddweller.platform.xls.XLSEntitySourceProvider;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;

import java.text.ParseException;

/**
 * Creates {@link Object}s based on a Script.<br/><br/>
 * Created: 29.01.2008 17:19:24
 * @author Volker Bergmann
 * @since 0.4.0
 */
public class DynamicSourceGenerator extends ThreadSafeGenerator<Entity> {

  private final Script script;
  private final Uniqueness uniqueness;
  private final BeneratorContext context;
  private final ComplexTypeDescriptor descriptor;
  private final ComplexTypeGeneratorFactory factory;
  private Generator<Entity> source;
  private DataIterator<Entity> iterator;
  private final ThreadLocalDataContainer<Entity> container = new ThreadLocalDataContainer<>();

  

  public DynamicSourceGenerator(Script script,
                                Uniqueness uniqueness,
                                BeneratorContext context,
                                ComplexTypeDescriptor descriptor
          , ComplexTypeGeneratorFactory factory
  ) {
    this.script = script;
    this.uniqueness = uniqueness;
    this.context = context;
    this.descriptor = descriptor;

    this.factory = factory;
  }

  public Class<Entity> getGeneratedType() {
    return Entity.class;
  }

  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    source = factory.resolveDynamicSourceGenerator(descriptor, uniqueness, context);
    iterator = ((DataSourceGenerator) ((ConvertingGenerator) source).getSource()).getSource().iterator();
    DataContainer<Entity> tmp = iterator.next(container.get());
    if (tmp == null) {
      IOUtil.close(iterator);
      return null;
    }
    return wrapper.wrap(tmp.getData());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + script + ']';
  }


//  protected Generator<Entity> applyFilter(ComplexTypeDescriptor descriptor, Generator<Entity> generator) {
//    return generator;
//  }

//  private Generator<Entity> applyDistribution(ComplexTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context,
//                                              Generator<Entity> generator) {
//    Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
//    if (distribution != null) {
//      generator = new DistributingGenerator<>(generator, distribution, uniqueness.isUnique());
//    }
//    return generator;
//  }
//
//  private static Generator<Entity> createSourceGeneratorFromObject(ComplexTypeDescriptor descriptor,
//                                                                   BeneratorContext context, Object sourceObject) {
//    Generator<Entity> generator;
//    if (sourceObject instanceof StorageSystem) {
//      StorageSystem storage = (StorageSystem) sourceObject;
//      String selector = descriptor.getSelector();
//      String subSelector = descriptor.getSubSelector();
//      if (!StringUtil.isEmpty(subSelector)) {
//        DataSource<Entity> dataSource = storage.queryEntities(descriptor.getName(), subSelector, context);
//        generator = WrapperFactory.applyHeadCycler(new DataSourceGenerator<>(dataSource));
//      } else {
//        generator = new DataSourceGenerator<>(storage.queryEntities(descriptor.getName(), selector, context));
//      }
//    } else if (sourceObject instanceof Generator) {
//      generator = (Generator<Entity>) sourceObject;
//    } else if (sourceObject instanceof TypedEntitySource) {
//      TypedEntitySource dataSource = (TypedEntitySource) sourceObject;
//      generator = new DataSourceGenerator<>(new TypedEntitySourceAdapter(dataSource, descriptor));
//    } else if (sourceObject instanceof EntitySource) {
//      generator = new DataSourceGenerator<>((EntitySource) sourceObject);
//    } else if (sourceObject instanceof DataSource) {
//      DataSource dataSource = (DataSource) sourceObject;
//      if (!Entity.class.isAssignableFrom(dataSource.getType())) {
//        throw BeneratorExceptionFactory.getInstance().illegalArgument(
//                "Not a supported data type to iterate: " + dataSource.getType());
//      }
//      generator = new DataSourceGenerator<>(dataSource);
//    } else {
//      throw BeneratorExceptionFactory.getInstance().illegalArgument(
//              "Source type not supported: " + sourceObject.getClass());
//    }
//    return generator;
//  }
//
//  private static Generator<Entity> createProtocolSourceGenerator(String url, FileFormat format,
//                                                                 ComplexTypeDescriptor complexType, BeneratorContext context) {
//    DataSourceProvider<Entity> fileProvider = format.provider(url, null, complexType, context);
//    return createEntitySourceGenerator(complexType, context, url, fileProvider);
//  }
//
//  private static Generator<Entity> createCSVSourceGenerator(
//          ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
//    String encoding = DescriptorUtil.getEncoding(complexType, context);
//    Converter<String, String> preprocessor = createSourcePreprocessor(complexType, context);
//    char separator = DescriptorUtil.getSeparator(complexType, context);
//    DataSourceProvider<Entity> fileProvider = new CSVEntitySourceProvider(complexType, preprocessor,
//            separator, encoding);
//    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
//  }
//
//  private static Generator<Entity> createFixedColumnWidthSourceGenerator(
//          ComplexTypeDescriptor descriptor, BeneratorContext context, String sourceName) {
//    String encoding = DescriptorUtil.getEncoding(descriptor, context);
//    String pattern = descriptor.getPattern();
//    if (pattern == null) {
//      throw ExceptionFactory.getInstance().configurationError("No pattern specified for FCW file import: " + sourceName);
//    }
//    try {
//      FixedWidthRowTypeDescriptor rowDescriptor = FixedWidthUtil.parseBeanColumnsSpec(
//              pattern, descriptor.getName(), null, context.getDefaultLocale());
//      FixedWidthColumnDescriptor[] ffcd = rowDescriptor.getColumnDescriptors();
//      Converter<String, String> preprocessor = createSourcePreprocessor(descriptor, context);
//      FixedWidthEntitySource iterable = new FixedWidthEntitySource(sourceName, descriptor, preprocessor, encoding, null, ffcd);
//      iterable.setContext(context);
//      return new DataSourceGenerator<>(iterable);
//    } catch (ParseException e) {
//      throw ExceptionFactory.getInstance().configurationError("Error parsing fixed-width pattern: " + pattern, e);
//    }
//  }
//
//  private static Generator<Entity> createXLSSourceGenerator(
//          ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName, String segment) {
//    Converter<String, String> preprocessor = createSourcePreprocessor(complexType, context);
//    boolean formatted = isFormatted(complexType);
//    XLSEntitySourceProvider fileProvider = new XLSEntitySourceProvider(complexType, segment, formatted, preprocessor);
//    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
//  }
//
//  private static Converter<String, String> createSourcePreprocessor(
//          ComplexTypeDescriptor complexType, BeneratorContext context) {
//    if (DescriptorUtil.isSourceScripted(complexType, context)) {
//      return DescriptorUtil.createStringScriptConverter(context);
//    } else {
//      return null;
//    }
//  }
//
//  private static Generator<Entity> createEntitySourceGenerator(
//          ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName,
//          DataSourceProvider<Entity> factory) {
//    Generator<Entity> generator = SourceFactory.createRawSourceGenerator(
//            complexType.getNesting(), complexType.getDataset(), sourceName, factory, Entity.class, context);
//    return WrapperFactory.applyConverter(generator, new ComponentTypeConverter(complexType));
//  }
//
//  protected static boolean isFormatted(TypeDescriptor type) {
//    Format format = type.getFormat();
//    if (format == Format.formatted) {
//      return true;
//    } else if (format == Format.raw) {
//      return false;
//    } else if (!DataFileUtil.isExcelDocument(type.getSource())) {
//      return false;
//    } else {
//      return PlatformDescriptor.isFormattedByDefault();
//    }
//  }
}
