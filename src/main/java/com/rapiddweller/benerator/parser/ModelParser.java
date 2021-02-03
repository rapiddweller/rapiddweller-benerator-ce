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

package com.rapiddweller.benerator.parser;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SyntaxError;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.VariableDescriptor;
import com.rapiddweller.model.data.VariableHolder;
import com.rapiddweller.script.expression.ConstantExpression;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.Map;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ATTRIBUTE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_PART;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_REFERENCE;
import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;
import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.resolveScript;

/**
 * Parses databene model files.<br/><br/>
 * Created: 04.03.2008 16:43:09
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class ModelParser {

  private static final Set<String> SIMPLE_TYPE_COMPONENTS = CollectionUtil.toSet(EL_ATTRIBUTE, EL_REFERENCE, EL_ID);

  private final BeneratorContext context;
  private final DescriptorProvider descriptorProvider;

  /**
   * Instantiates a new Model parser.
   *
   * @param context the context
   */
  public ModelParser(BeneratorContext context) {
    this.context = context;
    this.descriptorProvider = context.getLocalDescriptorProvider();
  }

  /**
   * Parse component component descriptor.
   *
   * @param element the element
   * @param owner   the owner
   * @return the component descriptor
   */
  public ComponentDescriptor parseComponent(Element element, ComplexTypeDescriptor owner) {
    String elementName = XMLUtil.localName(element);
    if (EL_PART.equals(elementName)) {
      return parsePart(element, owner, null);
    } else if (SIMPLE_TYPE_COMPONENTS.contains(elementName)) {
      return parseSimpleTypeComponent(element, owner, null);
    } else {
      throw new ConfigurationError("Expected one of these element names: " +
          EL_ATTRIBUTE + ", " + EL_ID + ", " + EL_REFERENCE + ", or " + EL_PART + ". Found: " + elementName);
    }
  }

  /**
   * Parse simple type component component descriptor.
   *
   * @param element   the element
   * @param owner     the owner
   * @param component the component
   * @return the component descriptor
   */
  public ComponentDescriptor parseSimpleTypeComponent(
      Element element, ComplexTypeDescriptor owner, ComponentDescriptor component) {
    String name = XMLUtil.localName(element);
    if (EL_ATTRIBUTE.equals(name)) {
      return parseAttribute(element, owner, component);
    } else if (EL_ID.equals(name)) {
      return parseId(element, owner, component);
    } else if (EL_REFERENCE.equals(name)) {
      return parseReference(element, owner, component);
    } else {
      throw new ConfigurationError("Expected one of these element names: " +
          EL_ATTRIBUTE + ", " + EL_ID + " or " + EL_REFERENCE + ". Found: " + name);
    }
  }

  /**
   * Parse complex type complex type descriptor.
   *
   * @param ctElement  the ct element
   * @param descriptor the descriptor
   * @return the complex type descriptor
   */
  public ComplexTypeDescriptor parseComplexType(Element ctElement, ComplexTypeDescriptor descriptor) {
    assertElementName(ctElement, "entity", "type");
    descriptor = new ComplexTypeDescriptor(descriptor.getName(), descriptorProvider, descriptor);
    mapTypeDetails(ctElement, descriptor);
    for (Element child : XMLUtil.getChildElements(ctElement)) {
      parseComplexTypeChild(child, descriptor);
    }
    return descriptor;
  }

  /**
   * Parse complex type child.
   *
   * @param element    the element
   * @param descriptor the descriptor
   */
  public void parseComplexTypeChild(Element element, ComplexTypeDescriptor descriptor) {
    String childName = XMLUtil.localName(element);
    if ("variable".equals(childName)) {
      parseVariable(element, descriptor);
    } else {
      throw new UnsupportedOperationException("element type not supported here: " + childName);
    }
  }

  /**
   * Parse attribute part descriptor.
   *
   * @param element    the element
   * @param owner      the owner
   * @param descriptor the descriptor
   * @return the part descriptor
   */
  public PartDescriptor parseAttribute(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    assertElementName(element, "attribute");
    PartDescriptor result;
    if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String typeName = StringUtil.emptyToNull(element.getAttribute("type"));
      result = new PartDescriptor(element.getAttribute("name"), descriptorProvider, typeName);
    }
    mapInstanceDetails(element, false, result);
    applyDefaultCounts(result);
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.addComponent(result);
    }
    return result;
  }

  /**
   * Parse part part descriptor.
   *
   * @param element    the element
   * @param owner      the owner
   * @param descriptor the descriptor
   * @return the part descriptor
   */
  public PartDescriptor parsePart(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    assertElementName(element, "part");
    PartDescriptor result;
    if (descriptor instanceof PartDescriptor) {
      result = (PartDescriptor) descriptor;
    } else if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String typeName = StringUtil.emptyToNull(element.getAttribute("type"));
      String partName = element.getAttribute("name");
      String localTypeName = owner.getName() + "." + partName;
      if (typeName != null) {
        result = new PartDescriptor(partName, descriptorProvider, typeName);
      } else if (element.getNodeName().equals("part")) {
        result = new PartDescriptor(partName, descriptorProvider,
            new ComplexTypeDescriptor(localTypeName, descriptorProvider, (ComplexTypeDescriptor) null));
      } else {
        result = new PartDescriptor(partName, descriptorProvider,
            new SimpleTypeDescriptor(localTypeName, descriptorProvider, (SimpleTypeDescriptor) null));
      }
    }
    mapInstanceDetails(element, true, result);
    if (result.getLocalType().getSource() == null) {
      applyDefaultCounts(result);
    }
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.addComponent(result);
    }
    for (Element childElement : XMLUtil.getChildElements(element)) {
      parseComponent(childElement, (ComplexTypeDescriptor) result.getLocalType(true));
    }
    return result;
  }

  /**
   * Apply default counts.
   *
   * @param descriptor the descriptor
   */
  public void applyDefaultCounts(PartDescriptor descriptor) {
    if (descriptor.getDeclaredDetailValue("minCount") == null) {
      descriptor.setMinCount(new ConstantExpression<>(1L));
    }
    if (descriptor.getDeclaredDetailValue("maxCount") == null) {
      descriptor.setMaxCount(new ConstantExpression<>(1L));
    }
  }

//    public SimpleTypeDescriptor parseSimpleType(Element element) {
//        assertElementName(element, "type");
//        return parseSimpleType(element, new SimpleTypeDescriptor(null, descriptorProvider, (String) null));
//    }

  /**
   * Parse simple type simple type descriptor.
   *
   * @param element    the element
   * @param descriptor the descriptor
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor parseSimpleType(Element element, SimpleTypeDescriptor descriptor) {
    assertElementName(element, "type");
    return mapTypeDetails(element, descriptor);
  }

  /**
   * Parse variable instance descriptor.
   *
   * @param varElement the var element
   * @param owner      the owner
   * @return the instance descriptor
   */
  public InstanceDescriptor parseVariable(Element varElement, VariableHolder owner) {
    assertElementName(varElement, "variable");
    String type = StringUtil.emptyToNull(varElement.getAttribute("type"));
    VariableDescriptor descriptor = new VariableDescriptor(varElement.getAttribute("name"), descriptorProvider, type);
    VariableDescriptor variable = mapInstanceDetails(varElement, false, descriptor);
    owner.addVariable(variable);
    return variable;
  }

  /**
   * Parse simple type array element array element descriptor.
   *
   * @param element the element
   * @param owner   the owner
   * @param index   the index
   * @return the array element descriptor
   */
  public ArrayElementDescriptor parseSimpleTypeArrayElement(Element element, ArrayTypeDescriptor owner, int index) {
    ArrayElementDescriptor descriptor = new ArrayElementDescriptor(index, descriptorProvider, element.getAttribute("name"));
    mapInstanceDetails(element, false, descriptor);
    owner.addElement(descriptor);
    return descriptor;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private <T extends TypeDescriptor> T mapTypeDetails(Element element, T descriptor) {
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Attr attr = (Attr) attributes.item(i);
      String detailValue = parseStringAttribute(attr, context);
      descriptor.setDetailValue(attr.getName(), detailValue);
    }
    return descriptor;
  }

  private <T extends InstanceDescriptor> T mapInstanceDetails(
      Element element, boolean complexType, T descriptor) {
    TypeDescriptor localType = descriptor.getLocalType();
    Map<String, String> attributes = XMLUtil.getAttributes(element);
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      String detailName = entry.getKey();
      if (detailName.equals("type")) {
        continue;
      }
      Object tmp = resolveScript(detailName, entry.getValue(), context);
      String detailString = ToStringConverter.convert(tmp, null);
      if (descriptor.supportsDetail(detailName)) {
        try {
          descriptor.setDetailValue(detailName, detailString);
        } catch (IllegalArgumentException e) {
          throw new SyntaxError("Error parsing '" + detailName + "'", e, String.valueOf(detailString), -1, -1);
        }
      } else {
        if (localType == null) {
          String partType = attributes.get("type");
          if (partType == null) {
            partType = descriptor.getType();
          }
          if (partType == null) {
            String sourceSpec = attributes.get("source");
            if (sourceSpec != null) {
              Object source = context.get(sourceSpec);
              if (source != null) {
                if (source instanceof Generator) {
                  if (((Generator<?>) source).getGeneratedType() == Entity.class) {
                    partType = "entity";
                  }
                } else if (source instanceof EntitySource) {
                  partType = "entity";
                }
              } else {
                String lcSourceSpec = sourceSpec.toLowerCase();
                if (lcSourceSpec.endsWith(".ent.csv")
                    || lcSourceSpec.endsWith(".ent.fcw")
                    || lcSourceSpec.endsWith(".dbunit.xml")) {
                  partType = "entity";
                }
              }
            }
          }
          if (partType != null) {
            TypeDescriptor localTypeParent = context.getDataModel().getTypeDescriptor(partType);
            localType = (localTypeParent instanceof ComplexTypeDescriptor ?
                new ComplexTypeDescriptor(partType, descriptorProvider, partType) :
                new SimpleTypeDescriptor(partType, descriptorProvider, partType));
          }
          descriptor.setLocalType(localType);
        }
        if (localType == null) {
          localType = descriptor.getLocalType(complexType); // create new local type
        }
        localType.setDetailValue(detailName, detailString);
      }
    }
    return descriptor;
  }

  private static void assertElementName(Element element, String... expectedNames) {
    String elementName = XMLUtil.localName(element);
    for (String expectedName : expectedNames) {
      if (elementName.equals(expectedName)) {
        return;
      }
    }
    String message;
    if (expectedNames.length == 1) {
      message = "Expected element '" + expectedNames[0] + "', found: " + elementName;
    } else {
      message = "Expected one of these element names: '" + ArrayFormat.format(expectedNames) + "', " +
          "found: " + elementName;
    }
    throw new IllegalArgumentException(message);
  }

  private IdDescriptor parseId(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    assertElementName(element, "id");
    IdDescriptor result;
    IdDescriptor resultTmp;
    if (descriptor instanceof IdDescriptor) {
      resultTmp = (IdDescriptor) descriptor;
    } else if (descriptor != null) {
      resultTmp = new IdDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      resultTmp = new IdDescriptor(element.getAttribute("name"), descriptorProvider, element.getAttribute("type"));
    }
    result = mapInstanceDetails(element, false, resultTmp);
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.addComponent(result);
    }
    return result;
  }

  private ReferenceDescriptor parseReference(Element element, ComplexTypeDescriptor owner, ComponentDescriptor component) {
    assertElementName(element, "reference");
    ReferenceDescriptor result;
    if (component instanceof ReferenceDescriptor) {
      result = (ReferenceDescriptor) component;
    } else if (component != null) {
      result = new ReferenceDescriptor(component.getName(), descriptorProvider, component.getType());
    } else {
      result = new ReferenceDescriptor(element.getAttribute("name"), descriptorProvider, StringUtil.emptyToNull(element.getAttribute("type")));
    }
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.addComponent(result);
    }
    return mapInstanceDetails(element, false, result);
  }


}
