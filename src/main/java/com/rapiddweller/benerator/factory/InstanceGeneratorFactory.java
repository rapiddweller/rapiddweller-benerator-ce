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
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nullable;

/**
 * Creates entity generators from entity metadata.<br/><br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactory {

  private static final Logger logger = LoggerFactory.getLogger(InstanceGeneratorFactory.class);

  // protected constructor for preventing instantiation --------------------------------------------------------------

  protected InstanceGeneratorFactory() {
  }

  @Nullable
  public static Generator<?> createSingleInstanceGenerator(
      InstanceDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {

    // check 'ignored' setting
    if (descriptor.getMode() == Mode.ignored) {
      logger.debug("Ignoring descriptor {}", descriptor);
      return null;
    }

    // check if nullQuota is 1
    Generator<?> generator = DescriptorUtil.createNullQuotaOneGenerator(descriptor, context);
    if (generator != null) {
      return generator;
    }

    // check uniqueness setting
    Uniqueness uniqueness = DescriptorUtil.getUniqueness(descriptor, context);
    if (!uniqueness.isUnique()) {
      uniqueness = ownerUniqueness;
    }

    // check nullability
    boolean nullable = DescriptorUtil.isNullable(descriptor, context);

    // create an appropriate generator
    TypeDescriptor type = descriptor.getTypeDescriptor();
    String instanceName = descriptor.getName();
    if (type != null) {
      generator = MetaGeneratorFactory.createTypeGenerator(type, instanceName, nullable, uniqueness, context);
    } else {
      ComponentDescriptor defaultConfig = context.getDefaultComponentConfig(instanceName);
      if (defaultConfig != null) {
        return createSingleInstanceGenerator(defaultConfig, ownerUniqueness, context);
      }
      if (nullable && DescriptorUtil.shouldNullifyEachNullable(descriptor, context)) {
        return createNullGenerator(descriptor, context);
      }
      if (descriptor instanceof IdDescriptor) {
        generator = new IncrementGenerator(1);
      } else {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForNothing(
            "Type of " + instanceName + " is not defined", null);
      }
    }
    GeneratorFactory generatorFactory = context.getGeneratorFactory();
    generator = generatorFactory.applyNullSettings(generator, nullable, descriptor.getNullQuota());
    return generator;
  }

  @Nullable
  public static Generator<?> createConfiguredDefaultGenerator(String componentName, Uniqueness ownerUniqueness, BeneratorContext context) {
    ComponentDescriptor defaultConfig = context.getDefaultComponentConfig(componentName);
    if (defaultConfig != null) {
      return createSingleInstanceGenerator(defaultConfig, ownerUniqueness, context);
    }
    return null;
  }

  protected static Generator<?> createNullGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
    Class<?> generatedType;
    TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    if (typeDescriptor instanceof SimpleTypeDescriptor) {
      generatedType = ((SimpleTypeDescriptor) typeDescriptor).getPrimitiveType().getJavaType();
    } else {
      generatedType = String.class;
    }
    return context.getGeneratorFactory().createNullGenerator(generatedType);
  }

}
