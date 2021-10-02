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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.composite.AlternativeComponentBuilder;
import com.rapiddweller.benerator.composite.ArrayElementBuilder;
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
import com.rapiddweller.benerator.primitive.ScriptGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.wrapper.AsIntegerGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.wrapper.SingleSourceArrayGenerator;
import com.rapiddweller.benerator.wrapper.SingleSourceCollectionGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SyntaxError;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.Expression;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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

  public static ComponentBuilder<?> createComponentBuilder(
      ComponentDescriptor descriptor, Uniqueness ownerUniqueness, boolean iterationMode, BeneratorContext context) {
    logger.debug("createComponentBuilder({})", descriptor.getName());
    ComponentBuilder<?> result = null;
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
    } else {
      throw new ConfigurationError("Not a supported element: " + descriptor.getClass());
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
    Script script = ScriptUtil.parseScriptText(scriptText);
    Generator<?> generator = new ScriptGenerator(script);
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
    ComponentBuilder<?> result = null;
    if (iterationMode && part.getTypeDescriptor() instanceof ComplexTypeDescriptor) {
      result = createPartModifier(part, context);
    } else {
      Generator<?> generator = createSingleInstanceGenerator(part, ownerUniqueness, context);
      generator = createMultiplicityWrapper(part, generator, context);
      result = builderFromGenerator(generator, part, context);
    }
    logger.debug("Created part {}", result);
    return result;
  }

  private static PartModifier createPartModifier(ComponentDescriptor part, BeneratorContext context) {
    ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) part.getTypeDescriptor();
    List<GenerationStep<Entity>> components =
        GenerationStepFactory.createMutatingGenerationSteps(typeDescriptor, true, Uniqueness.NONE, context);
    return new PartModifier(part.getName(), components, typeDescriptor.getScope());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static ComponentBuilder<?> createReferenceBuilder(ReferenceDescriptor descriptor, BeneratorContext context) {
    SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();

    // check uniqueness
    boolean unique = DescriptorUtil.isUnique(descriptor, context);
    Uniqueness uniqueness = (unique ? Uniqueness.SIMPLE : Uniqueness.NONE);

    // do I only need to generate nulls?
    if (DescriptorUtil.isNullable(descriptor, context) && DescriptorUtil.shouldNullifyEachNullable(descriptor, context)) {
      return builderFromGenerator(createNullGenerator(descriptor, context), descriptor, context);
    }

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

    // get distribution
    Distribution distribution = FactoryUtil.getDistribution(
        typeDescriptor.getDistribution(), descriptor.getUniqueness(), false, context);

    // check source
    if (generator == null) {
      // check target type
      String targetTypeName = descriptor.getTargetType();
      ComplexTypeDescriptor targetType = (ComplexTypeDescriptor) context.getDataModel().getTypeDescriptor(targetTypeName);
      if (targetType == null) {
        throw new ConfigurationError("Type not defined: " + targetTypeName);
      }

      // check targetComponent
      String targetComponent = descriptor.getTargetComponent();

      // check source
      String sourceName = typeDescriptor.getSource();
      if (sourceName == null) {
        throw new ConfigurationError("'source' is not set for " + descriptor);
      }
      Object sourceObject = context.get(sourceName);
      if (sourceObject instanceof StorageSystem) {
        StorageSystem sourceSystem = (StorageSystem) sourceObject;
        String selector = typeDescriptor.getSelector();
        String subSelector = typeDescriptor.getSubSelector();
        boolean subSelect = !StringUtil.isEmpty(subSelector);
        String selectorToUse = (subSelect ? subSelector : selector);
        if (isIndividualSelector(selectorToUse)) {
          generator = new DataSourceGenerator(sourceSystem.query(selectorToUse, true, context));
        } else {
          generator = new DataSourceGenerator(sourceSystem.queryEntityIds(
              targetTypeName, selectorToUse, context)); // TODO v0.7.2 query by targetComponent
          if (selectorToUse == null && distribution == null) {
            if (context.isDefaultOneToOne()) {
              distribution = new ExpandSequence();
            } else {
              distribution = SequenceManager.RANDOM_SEQUENCE;
            }
          }
        }
        if (subSelect) {
          generator = WrapperFactory.applyHeadCycler(generator);
        }
      } else {
        throw new ConfigurationError("Not a supported source type: " + sourceName);
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
      throw new RuntimeException("Unexpected error", e);
    }
    return false;
  }

  // non-public helpers ----------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  static ComponentBuilder<?> wrapWithCondition(ComponentDescriptor descriptor, ComponentBuilder<?> builder) {
    if (builder == null) {
      return null;
    }
    TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    if (typeDescriptor == null) {
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
      return new ArrayElementBuilder(index, source, scope);
    } else {
      return new PlainEntityComponentBuilder(descriptor.getName(), source, scope);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static Generator<?> createMultiplicityWrapper(
      ComponentDescriptor instance, Generator<?> generator, BeneratorContext context) {
    if (generator == null) {
      return null;
    }
    String container = instance.getContainer();
    if (container == null) {
      long defaultMinCount = (instance.getTypeDescriptor() instanceof ComplexTypeDescriptor ? 0 : 1);
      Generator<Long> longCountGenerator = DescriptorUtil.createDynamicCountGenerator(instance, defaultMinCount, 1L, true, context);
      if (longCountGenerator instanceof ConstantGenerator
          && longCountGenerator.generate(new ProductWrapper<Long>()).unwrap() == 1L) {
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
        throw new SyntaxError("Not a supported container", container);
    }
  }

}
