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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.composite.BlankEntityGenerator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.primitive.ValueMapper;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.converter.FormatFormatConverter;
import com.rapiddweller.common.converter.ParseFormatConverter;
import com.rapiddweller.common.converter.String2DateConverter;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.model.data.Format;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.xls.PlatformDescriptor;
import com.rapiddweller.script.PrimitiveType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.rapiddweller.model.data.TypeDescriptor.PATTERN;

/**
 * Creates generators of type instances.<br/><br/>
 * Created: 05.03.2008 16:51:44
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.5.0
 */
public abstract class TypeGeneratorFactory<E extends TypeDescriptor> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public Generator<?> createGenerator(E descriptor, String instanceName,
                                      boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
    logger.debug("createGenerator({})", descriptor.getName());
    Generator<?> generator = createRootGenerator(descriptor, instanceName, nullable, uniqueness, context);
    boolean iterationMode = !(generator instanceof BlankEntityGenerator);
    generator = applyComponentBuilders(generator, iterationMode, descriptor, instanceName, uniqueness, context);
    generator = wrapWithPostprocessors(generator, descriptor, context);
    generator = applyOffsetAndCyclic(generator, descriptor, instanceName, uniqueness, context);
    logger.debug("Created {}", generator);
    return generator;
  }

  public Generator<?> createRootGenerator(E descriptor, String instanceName,
                                          boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
    Generator<?> generator = createExplicitGenerator(descriptor, uniqueness, context);
    if (generator == null) {
      generator = createSpecificGenerator(descriptor, instanceName, nullable, uniqueness, context);
    }
    if (generator == null) {
      generator = createInheritedGenerator(descriptor, uniqueness, context);
    }
    if (generator == null) {
      generator = createHeuristicGenerator(descriptor, instanceName, uniqueness, context);
    }
    if (generator == null) { // by now, we must have created a generator
      throw BeneratorExceptionFactory.getInstance().configurationError("Failed to create root generator for descriptor: " + descriptor);
    }
    return generator;
  }

  protected Generator<?> createExplicitGenerator(
      E type, Uniqueness uniqueness, BeneratorContext context) {
    Generator<?> generator = DescriptorUtil.getGeneratorByName(type, context);
    if (generator == null) {
      generator = createSourceGenerator(type, uniqueness, context);
    }
    if (generator == null) {
      generator = createScriptGenerator(type);
    }
    return generator;
  }

  protected abstract Class<?> getGeneratedType(E descriptor);

  protected abstract Generator<?> createSourceGenerator(
      E descriptor, Uniqueness uniqueness, BeneratorContext context);

  protected abstract Generator<?> createSpecificGenerator(E descriptor, String instanceName,
                                                          boolean nullable, Uniqueness uniqueness, BeneratorContext context);

  @SuppressWarnings("unchecked")
  protected Generator<?> createInheritedGenerator(
      E type, Uniqueness uniqueness, BeneratorContext context) {
    while (type.getParent() != null) {
      type = (E) type.getParent();
      Generator<?> generator = createExplicitGenerator(type, uniqueness, context);
      if (generator != null) {
        return generator;
      }
    }
    return null;
  }

  protected abstract Generator<?> createHeuristicGenerator(E descriptor, String instanceName,
                                                           Uniqueness uniqueness, BeneratorContext context);

  protected Generator<?> applyOffsetAndCyclic(Generator<?> generator, E descriptor, String instanceName,
                                              Uniqueness uniqueness, BeneratorContext context) {
    generator = DescriptorUtil.processOffset(generator, descriptor);
    generator = DescriptorUtil.processCyclic(generator, descriptor);
    return generator;
  }

  protected Generator<?> applyComponentBuilders(Generator<?> generator, boolean iterationMode, E descriptor, String instanceName,
                                                Uniqueness uniqueness, BeneratorContext context) {
    return generator;
  }

  @Nullable
  protected static Generator<?> createScriptGenerator(TypeDescriptor descriptor) {
    String scriptText = descriptor.getScript();
    if (scriptText != null) {
      return FactoryUtil.createScriptGenerator(scriptText);
    }
    return null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected static Generator<?> createValidatingGenerator(
      TypeDescriptor descriptor, Generator<?> generator, BeneratorContext context) {
    Validator validator = DescriptorUtil.getValidator(descriptor.getValidator(), context);
    if (validator != null) {
      generator = WrapperFactory.applyValidator(validator, generator);
    }
    return generator;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor, Generator generator, BeneratorContext context) {
    Converter<?, ?> converter = DescriptorUtil.getConverter(descriptor.getConverter(), context);
    if (converter != null) {
      if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
        BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
      }
      generator = WrapperFactory.applyConverter(generator, converter);
    }
    return generator;
  }

  @SuppressWarnings("unchecked")
  static <E> Generator<E> wrapWithPostprocessors(Generator<E> generator, TypeDescriptor descriptor, BeneratorContext context) {
    generator = (Generator<E>) createConvertingGenerator(descriptor, generator, context);
    if (descriptor instanceof SimpleTypeDescriptor) {
      SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) descriptor;
      generator = (Generator<E>) createMappingGenerator(simpleType, generator);
      generator = (Generator<E>) createTypeConvertingGenerator(simpleType, generator);
    }
    generator = (Generator<E>) createValidatingGenerator(descriptor, generator, context);
    return generator;
  }

  static Generator<?> createMappingGenerator(
      SimpleTypeDescriptor descriptor, Generator<?> generator) {
    if (descriptor == null || descriptor.getMap() == null) {
      return generator;
    }
    String mappingSpec = descriptor.getMap();
    ValueMapper mapper = new ValueMapper(mappingSpec);
    return WrapperFactory.applyConverter(generator, mapper);
  }

  static Generator<?> createTypeConvertingGenerator(
      SimpleTypeDescriptor descriptor, Generator<?> generator) {
    if (descriptor == null || descriptor.getPrimitiveType() == null) {
      return generator;
    }
    Converter<?, ?> converter = createConverter(descriptor, generator.getGeneratedType());
    return (converter != null ? WrapperFactory.applyConverter(generator, converter) : generator);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Converter<?, ?> createConverter(SimpleTypeDescriptor descriptor, Class<?> sourceType) {
    PrimitiveType primitiveType = descriptor.getPrimitiveType();
    Class<?> targetType = primitiveType.getJavaType();
    Converter<?, ?> converter = null;
    if (Date.class.equals(targetType) && sourceType == String.class) {
      // String needs to be converted to Date
      if (descriptor.getPattern() != null) {
        // We can use the SimpleDateFormat with a pattern
        String pattern = descriptor.getPattern();
        converter = new ParseFormatConverter<>(Date.class, new SimpleDateFormat(pattern), false);
      } else {
        // we need to expect the standard date format
        converter = new String2DateConverter<>();
      }
    } else if (String.class.equals(targetType) && sourceType == Date.class) {
      // String needs to be converted to Date
      if (descriptor.getPattern() != null) {
        // We can use the SimpleDateFormat with a pattern
        String pattern = descriptor.getPattern();
        converter = new FormatFormatConverter<>(Date.class, new SimpleDateFormat(pattern), false);
      } else {
        // we need to expect the standard date format
        converter = new FormatFormatConverter<>(Date.class, TimeUtil.createDefaultDateFormat(), false);
      }
    } else if (targetType != sourceType) {
      converter = new AnyConverter(targetType, descriptor.getPattern());
    }
    return converter;
  }

  protected boolean shouldNullifyEachNullable(BeneratorContext context) {
    return (context.getGeneratorFactory().getDefaultsProvider().defaultNullQuota() == 1.);
  }

  protected static boolean isFormatted(TypeDescriptor type) {
    Format format = type.getFormat();
    if (format == Format.formatted) {
      return true;
    } else if (format == Format.raw) {
      return false;
    } else if (!DataFileUtil.isExcelDocument(type.getSource())) {
      return false;
    } else {
      return PlatformDescriptor.isFormattedByDefault();
    }
  }

}
