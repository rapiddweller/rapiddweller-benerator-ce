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

package com.rapiddweller.model.data;

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Composite;
import com.rapiddweller.common.CompositeFormatter;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.platform.java.BeanDescriptorProvider;
import com.rapiddweller.script.PrimitiveType;
import com.rapiddweller.common.ArrayUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Instance of a composite data type as described by a {@link ComplexTypeDescriptor}.<br/><br/>
 * Created: 20.08.2007 19:20:22
 * @author Volker Bergmann
 * @since 0.3
 */
public class Entity implements Composite {

  public final ComplexTypeDescriptor descriptor;
  private OrderedNameMap<Object> components;

  // constructors ----------------------------------------------------------------------------------------------------

  public Entity(String name, DescriptorProvider descriptorProvider) {
    this(new ComplexTypeDescriptor(name, descriptorProvider));
  }

  public Entity(String name, DescriptorProvider descriptorProvider,
                Object... componentKeyValuePairs) {
    this(new ComplexTypeDescriptor(name, descriptorProvider),
        componentKeyValuePairs);
  }

  public Entity(ComplexTypeDescriptor descriptor,
                Object... componentKeyValuePairs) {
    this.descriptor = descriptor;
    this.components = OrderedNameMap.createCaseInsensitiveMap();
    for (int i = 0; i < componentKeyValuePairs.length; i += 2) {
      setComponent((String) componentKeyValuePairs[i],
          componentKeyValuePairs[i + 1]);
    }
  }

  public Entity(Entity prototype) {
    this.descriptor = prototype.descriptor;
    copyComponentsFrom(prototype);
  }

  // interface -------------------------------------------------------------------------------------------------------

  public String type() {
    return (descriptor != null ? descriptor.getName() : null);
  }

  public ComplexTypeDescriptor descriptor() {
    return descriptor;
  }

  /** Allows for generic 'map-like' access to component values, e.g. by FreeMarker.
   *  @param componentName the name of the component whose value to return.
   *  @return the value of the specified component.
   *  @since 0.4.0 */
  public Object get(String componentName) {
    return getComponent(componentName);
  }

  @Override
  public Object getComponent(String componentName) {
    return components.get(componentName);
  }

  public boolean componentIsSet(String componentName) {
    return components.containsKey(componentName);
  }

  @Override
  public OrderedNameMap<Object> getComponents() {
    return components;
  }

  public void setComponents(OrderedNameMap<Object> components) {
    this.components = components;
  }

  public void set(String componentName, Object component) {
    setComponent(componentName, component);
  }

  @Override
  public void setComponent(String componentName, Object component) {
    ComponentDescriptor componentDescriptor = null;
    if (descriptor != null) {
      componentDescriptor = descriptor.getComponent(componentName);
    }
    if (componentDescriptor != null && componentDescriptor.getTypeDescriptor() instanceof SimpleTypeDescriptor) {
      SimpleTypeDescriptor componentType = (SimpleTypeDescriptor) componentDescriptor.getTypeDescriptor();
      PrimitiveType primitiveType = componentType.getPrimitiveType();
      if (primitiveType == null) {
        primitiveType = PrimitiveType.STRING;
      }
      BeanDescriptorProvider beanProvider = descriptor.getDataModel().getBeanDescriptorProvider();
      Class<?> javaType = beanProvider.concreteType(primitiveType.getName());
      component = AnyConverter.convert(component, javaType);
    }
    String internalComponentName = componentDescriptor != null ? componentDescriptor.getName() : componentName;
    components.put(internalComponentName, component);
  }

  public void remove(String componentName) {
    removeComponent(componentName);
  }

  public void removeComponent(String componentName) {
    components.remove(componentName);
  }

  public Object idComponentValues() {
    ComplexTypeDescriptor entityDescriptor = descriptor;
    if (entityDescriptor == null) {
      throw new ConfigurationError("Unknown type: " + this);
    }
    String[] idComponentNames = entityDescriptor.getIdComponentNames();
    if (idComponentNames.length == 1) {
      return get(idComponentNames[0]);
    } else if (idComponentNames.length == 0) {
      return null;
    } else {
      return componentValues(idComponentNames);
    }
  }

  public Object componentValues(String[] idComponentNames) {
    Object[] result = new Object[idComponentNames.length];
    for (int i = 0; i < idComponentNames.length; i++) {
      result[i] = get(idComponentNames[i]);
    }
    return result;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void copyComponentsFrom(Entity prototype) {
    this.components = new OrderedNameMap<>(prototype.components);
    for (Map.Entry<String, Object> component : this.components.entrySet()) {
      Object value = component.getValue();
      component.setValue(copyOrSelf(value));
    }
  }

  private Object copyOrSelf(Object value) {
    if (value == null|| BeanUtil.isImmutable(value.getClass())) {
      return value;
    }
    Class<?> valueClass = value.getClass();
    if (value instanceof Entity) {
      return new Entity((Entity) value);
    } else if (valueClass.isArray()) {
      return copyArray(value);
    } else if (value instanceof List) {
      return copyList((List<?>) value);
    } else {
      throw new RuntimeException("Don't know how to handle " + valueClass);
    }
  }

  private Object copyArray(Object array) {
    if (array == null)
      return null;
    Class<Object> componentType = ArrayUtil.componentType(array);
    int length = Array.getLength(array);
    Object result = Array.newInstance(componentType, length);
    for (int i = 0; i < length; i++){
      Array.set(result, i, copyOrSelf(Array.get(array, i)));
    }
    return result;
  }

  private Object copyList(List<?> list) {
    List result = new ArrayList(list.size());
    for (Object element : list) {
      result.add(copyOrSelf(element));
    }
    return result;
  }

  public boolean equalsIgnoringDescriptor(Entity that) {
    if (this == that) {
      return true;
    }
    if (this.getComponents().size() != that.getComponents().size()) {
      return false;
    }
    for (Map.Entry<String, Object> entry : this.getComponents().entrySet()) {
      Object thisValue = entry.getValue();
      Object thatValue = that.getComponent(entry.getKey());
      if (thisValue instanceof Entity) {
        if (!((Entity) thisValue).equalsIgnoringDescriptor((Entity) thatValue)) {
          return false;
        }
      } else if (!NullSafeComparator.equals(entry.getValue(), thatValue)) {
        return false;
      }
    }
    return true;
  }

  // java.lang.overrides ---------------------------------------------------------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entity)) {
      return false;
    }
    final Entity that = (Entity) o;
    // the following comparison looks strange, but is needed before I can compare the descriptor names
    // in order to exclude that any of both descriptors is null
    if (this.descriptor == null) {
      if (that.descriptor != null) {
        return false;
      }
    } else if (that.descriptor == null) {
      return false; // we can only be here if (this.descriptor != null)
    } else if (!NullSafeComparator.equals(this.descriptor.getName(), that.descriptor.getName())) {
      // both descriptors are not null, so I can compare their names
      return false;
    }
    // NOTE: the contents of the descriptors are not compared
    return this.components.equalsIgnoreOrder(that.components);
  }

  @Override
  public int hashCode() {
    int typeHash = (descriptor != null ? descriptor.getName().hashCode() : 0);
    return typeHash * 29 + components.hashCode();
  }

  @Override
  public String toString() {
    return new CompositeFormatter(true, true)
        .render(type() + '[', this, "]");
  }

}
