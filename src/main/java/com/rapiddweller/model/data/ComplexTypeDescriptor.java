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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.collection.ListBasedSet;
import com.rapiddweller.common.collection.NamedValueList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Describes a type that aggregates {@link ComponentDescriptor}s.<br/>
 * <br/>
 * Created: 03.03.2008 10:56:16
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class ComplexTypeDescriptor extends TypeDescriptor implements VariableHolder { // TODO don't implement VariableHolder

  public static final String __SIMPLE_CONTENT = "__SIMPLE_CONTENT";
  public static final String DYNAMIC_SOURCE = "dynamicSource";


  private NamedValueList<InstanceDescriptor> parts; // TODO use only ComponentDescriptors

  // constructors ----------------------------------------------------------------------------------------------------

  public ComplexTypeDescriptor(String name, DescriptorProvider provider) {
    this(name, provider, (String) null);
  }

  public ComplexTypeDescriptor(String name, DescriptorProvider provider, ComplexTypeDescriptor parent) {
    super(name, provider, parent);
    init();
  }

  public ComplexTypeDescriptor(String name, DescriptorProvider provider, String parentName) {
    super(name, provider, parentName);
    init();
  }


  // component handling ----------------------------------------------------------------------------------------------

  public void addPart(InstanceDescriptor part) {
    if (part instanceof ComponentDescriptor) {
      addComponent((ComponentDescriptor) part);
    } else {
      addVariable((VariableDescriptor) part);
    }
  }

  public void addComponent(ComponentDescriptor component) {
    linkToParentTypeComponent(component);
    parts.add(component.getName(), component);
  }

  public void setComponent(ComponentDescriptor component) {
    linkToParentTypeComponent(component);
    parts.set(component.getName(), component);
  }

  /** Searches the parent type descriptor for a component of the same name and,
   *  if one exists, assigns that one as this component's parent descriptor. */
  private void linkToParentTypeComponent(ComponentDescriptor component) {
    if (parent != null) {
      ComponentDescriptor parentComp = ((ComplexTypeDescriptor) this.parent).getComponent(component.getName());
      if (parentComp != null) {
        component.setParent(parentComp);
      }
    }
  }

  public ComponentDescriptor getComponent(String name) {
    return BeneratorFactory.getInstance().getComponent(name, parts, (ComplexTypeDescriptor) getParent());
  }

  public List<InstanceDescriptor> getParts() {
    NamedValueList<InstanceDescriptor> result =
        NamedValueList.createCaseInsensitiveList();

    for (InstanceDescriptor ccd : parts.values()) {
      result.add(ccd.getName(), ccd);
    }
    if (getParent() != null) {
      List<InstanceDescriptor> parentParts =
          ((ComplexTypeDescriptor) getParent()).getParts();
      for (InstanceDescriptor pcd : parentParts) {
        String name = pcd.getName();
        if (pcd instanceof ComponentDescriptor &&
            !parts.containsName(name)) {
          InstanceDescriptor ccd = parts.someValueOfName(name);
          result.add(name, Objects.requireNonNullElse(ccd, pcd));
        }
      }
    }
    return result.values();
  }

  public List<ComponentDescriptor> getComponents() {
    List<ComponentDescriptor> result = new ArrayList<>();
    for (InstanceDescriptor instance : getParts()) {
      if (instance instanceof ComponentDescriptor) {
        result.add((ComponentDescriptor) instance);
      }
    }
    return result;
  }

  public Collection<InstanceDescriptor> getDeclaredParts() {
    Set<InstanceDescriptor> declaredDescriptors = new ListBasedSet<>(parts.size());
    declaredDescriptors.addAll(parts.values());
    return declaredDescriptors;
  }

  public boolean isDeclaredComponent(String componentName) {
    return parts.containsName(componentName);
  }

  public String[] getIdComponentNames() {
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    for (ComponentDescriptor descriptor : getComponents()) {
      if (descriptor instanceof IdDescriptor) {
        builder.add(descriptor.getName());
      }
    }
    return builder.toArray();
  }

  public List<ReferenceDescriptor> getReferenceComponents() {
    return CollectionUtil.extractItemsOfExactType(ReferenceDescriptor.class,
        getComponents());
  }

  @Override
  public void addVariable(VariableDescriptor variable) {
    parts.add(variable.getName(), variable);
  }

  public String getDynamicSource() {
    return (String) getDetailValue(DYNAMIC_SOURCE);
  }

  // construction helper methods -------------------------------------------------------------------------------------

  public ComplexTypeDescriptor withComponent(ComponentDescriptor componentDescriptor) {
    setComponent(componentDescriptor);
    return this;
  }

  @Override
  protected void init() {
    super.init();
    addConfig(DYNAMIC_SOURCE, String.class);
    this.parts = new NamedValueList<>(NamedValueList.INSENSITIVE);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    if (parts.size() == 0) {
      return super.toString();
    }
    return getName() + getParts();
  }

}