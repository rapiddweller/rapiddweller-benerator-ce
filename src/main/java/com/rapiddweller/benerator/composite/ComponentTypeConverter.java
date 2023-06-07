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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.factory.DescriptorUtil;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Converts an Entity's components to the type specified by the EntityDescriptor.
 * This is used for e.g. importing Entities from file with String component values and
 * converting them to the correct target type.<br/><br/>
 * Created at 06.05.2008 11:34:46
 * @author Volker Bergmann
 * @since 0.5.3
 */
public class ComponentTypeConverter extends AbstractConverter<Entity, Entity> {

  private final ComplexTypeDescriptor type;

  public ComponentTypeConverter(ComplexTypeDescriptor type) {
    super(Entity.class, Entity.class);
    this.type = type;
  }

  public static Entity convert(Entity entity, ComplexTypeDescriptor type) throws ConversionException {
    if (entity == null) {
      return null;
    }
    Map<String, Object> components = entity.getComponents();
    for (Map.Entry<String, Object> entry : components.entrySet()) {
      String componentName = entry.getKey();
      ComponentDescriptor componentDescriptor = type.getComponent(componentName);
      if (componentDescriptor != null) {
        TypeDescriptor componentType = componentDescriptor.getTypeDescriptor();
        Object componentValue = entry.getValue();
        // Ignore CurrentProduct converter when iterateMode has container=list
        if (componentDescriptor.getContainer() != null) {
          components.put(componentName, null);
        }
        else if (componentType instanceof SimpleTypeDescriptor) {
          Object javaValue = DescriptorUtil.convertType(componentValue, (SimpleTypeDescriptor) componentType);
          components.put(componentName, javaValue);
        } else if (componentValue instanceof Entity) {
          components.put(componentName, convert((Entity) componentValue, (ComplexTypeDescriptor) componentType));
        } else if (componentValue.getClass().isArray()) {
          int n = Array.getLength(componentValue);
          ArrayBuilder<Entity> builder = new ArrayBuilder<>(Entity.class, n);
          for (int i = 0; i < n; i++) {
            Entity item = (Entity) Array.get(componentValue, i);
            builder.add(convert(item, (ComplexTypeDescriptor) componentType));
          }
          components.put(componentName, builder.toArray());
        } else if (componentValue instanceof Collection) {
          Collection<Entity> collection = (Collection<Entity>) componentValue;
          ArrayBuilder<Entity> builder = new ArrayBuilder<>(Entity.class, collection.size());
          for (Entity item : collection) {
            builder.add(convert(item, (ComplexTypeDescriptor) componentType));
          }
          components.put(componentName, builder.toArray());
        } else {
          throw BeneratorExceptionFactory.getInstance().configurationError("Expected complex data type for '" + componentName + "' but got " + componentValue.getClass());
        }
      }
    }
    return entity;
  }

  @Override
  public Entity convert(Entity entity) throws ConversionException {
    return convert(entity, type);
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + type + "]";
  }

}
