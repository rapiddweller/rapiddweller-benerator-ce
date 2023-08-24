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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.composite.AlternativeComponentBuilder;
import com.rapiddweller.benerator.composite.ArrayElementBuilder;
import com.rapiddweller.benerator.composite.AttributeProcessor;
import com.rapiddweller.benerator.composite.ComponentBuilder;
import com.rapiddweller.benerator.composite.ConditionalComponentBuilder;
import com.rapiddweller.benerator.composite.GenerationStep;
import com.rapiddweller.benerator.composite.PartModifier;
import com.rapiddweller.benerator.composite.PlainEntityComponentBuilder;
import com.rapiddweller.benerator.composite.SimplifyingSingleSourceArrayGenerator;
import com.rapiddweller.benerator.distribution.DistributingGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.distribution.sequence.ExpandSequence;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.wrapper.AsIntegerGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.wrapper.SingleSourceArrayGenerator;
import com.rapiddweller.benerator.wrapper.SingleSourceCollectionGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.benerator.wrapper.ItemListGenerator;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.ItemElementDescriptor;
import com.rapiddweller.model.data.ItemListDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.common.Expression;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Creates {@link ComponentBuilder}s.<br/><br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory extends InstanceGeneratorFactory {

  protected ComponentBuilderFactory() {
  }

  private static final Logger logger = LoggerFactory.getLogger(ComponentBuilderFactory.class);

  // factory methods for component generators ------------------------------------------------------------------------

  @Nullable
  public static ComponentBuilder<?> createComponentBuilder(
      ComponentDescriptor descriptor, Uniqueness ownerUniqueness, boolean iterationMode, BeneratorContext context) {
    logger.debug("createComponentBuilder({})", descriptor.getName());
    ComponentBuilder<?> result;
    if (descriptor instanceof ArrayElementDescriptor) {
      result = createPartBuilder(descriptor, ownerUniqueness, iterationMode, context);
    } else if (descriptor instanceof PartDescriptor) {
      TypeDescriptor type = descriptor.getTypeDescriptor();
      if (type instanceof AlternativeGroupDescriptor) {
        result = createAlternativeGroupBuilder((AlternativeGroupDescriptor) type, ownerUniqueness, iterationMode, context);
      } else {
        result = createPartBuilder(descriptor, ownerUniqueness, iterationMode, context);
      }
    } else if (descriptor instanceof ReferenceDescriptor) {
      result = createReferenceBuilder((ReferenceDescriptor) descriptor, context);
    } else if (descriptor instanceof IdDescriptor) {
      result = createIdBuilder((IdDescriptor) descriptor, ownerUniqueness, context);
    } else if (descriptor instanceof ItemElementDescriptor) {
      result = createItemElementBuilder(descriptor, ownerUniqueness, context);
    } else if (descriptor instanceof ItemListDescriptor) {
      result = createItemListBuilder(descriptor, ownerUniqueness, context);
    } else {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "Not a supported element: " + descriptor.getClass());
    }
    result = wrapWithCondition(descriptor, result);
    return result;
  }

  protected static ComponentBuilder<?> createScriptBuilder(ComponentDescriptor component, BeneratorContext context) {
    TypeDescriptor type = component.getTypeDescriptor();
    if (type == null) {
      return null;
    }
    String scriptText = type.getScript();
    if (scriptText == null) {
      return null;
    }
    Generator<?> generator = FactoryUtil.createScriptGenerator(scriptText);
    generator = DescriptorUtil.createConvertingGenerator(component.getTypeDescriptor(), generator, context);
    return builderFromGenerator(generator, component, context);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static ComponentBuilder<?> createAlternativeGroupBuilder(
      AlternativeGroupDescriptor type, Uniqueness ownerUniqueness, boolean iterationMode, BeneratorContext context) {
    int i = 0;
    Collection<ComponentDescriptor> components = type.getComponents();
    ComponentBuilder<?>[] builders = new ComponentBuilder[components.size()];
    for (ComponentDescriptor component : components) {
      builders[i++] = createComponentBuilder(component, ownerUniqueness, iterationMode, context);
    }
    return new AlternativeComponentBuilder(builders, type.getScope());
  }

  private static ComponentBuilder<?> createPartBuilder(
      ComponentDescriptor part, Uniqueness ownerUniqueness, boolean iterationMode, BeneratorContext context) {
    var containerValue = part.getDetailValue("container");
    if (iterationMode &&  containerValue == null) {
      if (part.getTypeDescriptor() instanceof ComplexTypeDescriptor) {
        return createPartModifier(part, context);
      } else {
        SimpleTypeDescriptor type = (SimpleTypeDescriptor) part.getTypeDescriptor();
        boolean processing = (type != null && type.getConverter() != null && type.getSource() == null
            && type.getGenerator() == null);
        if (processing) {
          return createAttributeProcessor(part, ownerUniqueness, context);
        }
      }
    }
    Generator<?> generator = createSingleInstanceGenerator(part, ownerUniqueness, context);
    generator = createMultiplicityWrapper(part, generator, context);
    return builderFromGenerator(generator, part, context);
  }

  private static ComponentBuilder<?> createAttributeProcessor(
      ComponentDescriptor component, Uniqueness ownerUniqueness, BeneratorContext context) {
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) component.getTypeDescriptor();
    Converter converter = DescriptorUtil.getConverter(type.getConverter(), context);
    return new AttributeProcessor(component.getName(), converter, type.getScope());
  }

  private static PartModifier createPartModifier(ComponentDescriptor part, BeneratorContext context) {
    ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) part.getTypeDescriptor();
    List<GenerationStep<Entity>> components =
        GenerationStepFactory.createMutatingGenerationSteps(typeDescriptor, true, Uniqueness.NONE, context);
    Converter<?, ?> converter = DescriptorUtil.getConverter(typeDescriptor.getConverter(), context);
    return new PartModifier(part.getName(), components, typeDescriptor.getScope(), converter);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static ComponentBuilder<?> createReferenceBuilder(ReferenceDescriptor descriptor, BeneratorContext context) {

    // check uniqueness
    boolean unique = DescriptorUtil.isUnique(descriptor, context);
    Uniqueness uniqueness = (unique ? Uniqueness.SIMPLE : Uniqueness.NONE);

    // do I only need to generate nulls?
    if (DescriptorUtil.isNullable(descriptor, context) && DescriptorUtil.shouldNullifyEachNullable(descriptor, context)) {
      return builderFromGenerator(createNullGenerator(descriptor, context), descriptor, context);
    }

    // checking explicit ref configuration
    SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();
    Generator<?> generator = createExplicitRefBuilder(context, uniqueness, typeDescriptor);

    // if no explicit ref config has been found yet, it must be an implicit one, using a 'source' spec...

    // get distribution
    Distribution distribution = FactoryUtil.getDistribution(
        typeDescriptor.getDistribution(), descriptor.getUniqueness(), false, context);

    // check source
    if (generator == null) {
      if (typeDescriptor.getSource() != null) {
        generator = createRefBuilderFromSource(descriptor, distribution, context);
      } else {
        throw BeneratorExceptionFactory.getInstance().configurationError("No source or explicit configuration for ref " + descriptor);
      }
    }

    // apply distribution if necessary
    if (distribution != null) {
      generator = new DistributingGenerator(generator, distribution, unique);
    }

    // check multiplicity
    generator = ComponentBuilderFactory.createMultiplicityWrapper(descriptor, generator, context);
    logger.debug("Created  reference builder {}", generator);

    // check 'cyclic' config
    generator = DescriptorUtil.wrapWithProxy(generator, typeDescriptor);
    return builderFromGenerator(generator, descriptor, context);
  }

  @Nullable
  private static Generator<?> createExplicitRefBuilder(BeneratorContext context, Uniqueness uniqueness, SimpleTypeDescriptor typeDescriptor) {
    Generator<?> generator = DescriptorUtil.getGeneratorByName(typeDescriptor, context);
    if (generator == null) {
      generator = TypeGeneratorFactory.createScriptGenerator(typeDescriptor);
    }
    if (generator == null) {
      generator = SimpleTypeGeneratorFactory.createConstantGenerator(typeDescriptor, context);
    }
    if (generator == null) {
      generator = SimpleTypeGeneratorFactory.createValuesGenerator(typeDescriptor, uniqueness, context);
    }
    return generator;
  }

  private static Generator<?> createRefBuilderFromSource(
      ReferenceDescriptor descriptor, Distribution distribution, BeneratorContext context) {

    // check target type
    String targetTypeName = descriptor.getTargetType();
    ComplexTypeDescriptor targetType = (ComplexTypeDescriptor) context.getDataModel().getTypeDescriptor(targetTypeName);
    if (targetType == null) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Type not defined: " + targetTypeName);
    }

    // check source
    SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();
    String sourceName = typeDescriptor.getSource();
    if (sourceName == null) {
      throw BeneratorExceptionFactory.getInstance().configurationError("'source' is not set for " + descriptor);
    }

    // analyse source object
    Object sourceObject = context.get(sourceName);
    if (sourceObject instanceof StorageSystem) {
      return createRefBuilderForStorageSystem(descriptor, (StorageSystem) sourceObject, distribution, context);
    } else {
      throw BeneratorExceptionFactory.getInstance().configurationError("Not a supported source type: " + sourceName);
    }
  }

  @NotNull @SuppressWarnings({"rawtypes", "unchecked"})
  private static Generator<?> createRefBuilderForStorageSystem(
      ReferenceDescriptor descriptor, StorageSystem sourceSystem, Distribution distribution,
      BeneratorContext context) {
    Generator<?> generator;
    Distribution fallbackDist = null;
    SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();
    String selector = typeDescriptor.getSelector();
    String subSelector = typeDescriptor.getSubSelector();
    boolean subSelect = !StringUtil.isEmpty(subSelector);
    String selectorToUse = (subSelect ? subSelector : selector);
    if (isIndividualSelector(selectorToUse)) {
      generator = new DataSourceGenerator(sourceSystem.query(selectorToUse, true, context));
    } else {
      String targetTypeName = descriptor.getTargetType();
      generator = new DataSourceGenerator(sourceSystem.queryEntityIds(
          targetTypeName, selectorToUse, context)); // TODO query by targetComponent
      if (selectorToUse == null && distribution == null) {
        // if no explicit distribution was specified, then choose a default
        if (context.isDefaultOneToOne()) {
          fallbackDist = new ExpandSequence();
        } else {
          fallbackDist = SequenceManager.RANDOM_SEQUENCE;
        }
      }
    }

    // apply wrappers
    if (subSelect) {
      generator = WrapperFactory.applyHeadCycler(generator);
    }
    if (fallbackDist != null) {
      boolean unique = DescriptorUtil.isUnique(descriptor, context);
      generator = new DistributingGenerator(generator, fallbackDist, unique);
    }
    return generator;
  }

  /** Helper method to check for selectors of individual fields like "select x from y" or
   *  "{'select x from y where id=' + z}". For such selectors it returns true, otherwise false
   *  @param selector the selector
   *  @return the boolean */
  protected static boolean isIndividualSelector(String selector) {
    if (selector == null) {
      return false;
    }
    StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(selector));
    tokenizer.ordinaryChar('\'');
    tokenizer.ordinaryChar('"');
    int token;
    try {
      while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
        if (token == StreamTokenizer.TT_WORD) {
          return StringUtil.startsWithIgnoreCase(tokenizer.sval.trim(), "select");
        }
      }
    } catch (IOException e) {
      throw BeneratorExceptionFactory.getInstance().internalError("Unexpected error", e);
    }
    return false;
  }

  // non-public helpers ----------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  static ComponentBuilder<?> wrapWithCondition(ComponentDescriptor descriptor, ComponentBuilder<?> builder) {
    TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    if (builder == null || typeDescriptor == null) {
      return builder;
    }
    String conditionText = typeDescriptor.getCondition();
    if (!StringUtil.isEmpty(conditionText)) {
      Expression<Boolean> condition = new ScriptExpression<>(conditionText);
      return new ConditionalComponentBuilder(builder, condition);
    } else {
      return builder;
    }
  }

  static ComponentBuilder<?> createIdBuilder(IdDescriptor id, Uniqueness ownerUniqueness, BeneratorContext context) {
    Generator<?> generator = createSingleInstanceGenerator(id, Uniqueness.ORDERED, context);
    if (generator != null) {
      logger.debug("Created id builder {}", generator);
    }
    return builderFromGenerator(generator, id, context);
  }

  private static ComponentBuilder<?> builderFromGenerator(
      Generator<?> source, ComponentDescriptor descriptor, BeneratorContext context) {
    if (source == null) {
      return null;
    }
    boolean nullability = DescriptorUtil.isNullable(descriptor, context);
    Double nullQuota = descriptor.getNullQuota();
    if (nullQuota != null && nullQuota != 0) {
      source = context.getGeneratorFactory().applyNullSettings(source, nullability, nullQuota);
    }
    TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    String scope = (typeDescriptor != null ? typeDescriptor.getScope() : null);
    if (descriptor instanceof ArrayElementDescriptor) {
      int index = ((ArrayElementDescriptor) descriptor).getIndex();
      return new ArrayElementBuilder(index, source, scope, descriptor.getFileLocation());
    } else {
      return new PlainEntityComponentBuilder(descriptor.getName(), source, scope, descriptor.getFileLocation());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static Generator<?> createMultiplicityWrapper(
      ComponentDescriptor instance, Generator<?> generator, BeneratorContext context) {
    if (generator == null) {
      return null;
    }
    String container = instance.getContainer();
    if (container == null || container.equals("map")) {
      long defaultMinCount = 1;
      Generator<Long> longCountGenerator = DescriptorUtil.createDynamicCountGenerator(instance, defaultMinCount, 1L, true, context);
      if (longCountGenerator instanceof ConstantGenerator
          && longCountGenerator.generate(new ProductWrapper<>()).unwrap() == 1L) {
        return generator;
      } else {
        NonNullGenerator<Integer> countGenerator = WrapperFactory.asNonNullGenerator(
            new AsIntegerGeneratorWrapper<Number>((Generator) longCountGenerator));
        return new SimplifyingSingleSourceArrayGenerator(generator, countGenerator);
      }
    }
    // handle container
    Generator<Long> longCountGenerator = DescriptorUtil.createDynamicCountGenerator(instance, null, null, true, context);
    NonNullGenerator<Integer> countGenerator = WrapperFactory.asNonNullGenerator(
        new AsIntegerGeneratorWrapper<Number>((Generator) longCountGenerator));
    switch (container) {
      case "array":
        return new SingleSourceArrayGenerator(generator, generator.getGeneratedType(), countGenerator);
      case "list":
        return new SingleSourceCollectionGenerator(generator, ArrayList.class, countGenerator);
      case "set":
        return new SingleSourceCollectionGenerator(generator, HashSet.class, countGenerator);
      default:
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForText(container, "Not a supported container");
    }
  }

  private static ComponentBuilder<?> createItemListBuilder(
          ComponentDescriptor list, Uniqueness ownerUniqueness, BeneratorContext context) {
    Generator<?> generator = createSingleInstanceGenerator(list, ownerUniqueness, context);
    // wrap product within array list
    generator = new ItemListGenerator(generator);
    return builderFromGenerator(generator, list, context);
  }

  private static ComponentBuilder<?> createItemElementBuilder(
          ComponentDescriptor list, Uniqueness ownerUniqueness, BeneratorContext context) {
    Generator<?> generator = createSingleInstanceGenerator(list, ownerUniqueness, context);
    return builderFromGenerator(generator, list, context);
  }
}
