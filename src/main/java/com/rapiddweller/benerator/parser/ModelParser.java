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

package com.rapiddweller.benerator.parser;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.parser.xml.AttributeParser;
import com.rapiddweller.benerator.parser.xml.IdParser;
import com.rapiddweller.benerator.parser.xml.ItemListParser;
import com.rapiddweller.benerator.parser.xml.PartParser;
import com.rapiddweller.benerator.parser.xml.ReferenceParser;
import com.rapiddweller.benerator.parser.xml.SimpleTypeArrayElementParser;
import com.rapiddweller.benerator.parser.xml.VariableParser;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.VariableHolder;
import org.w3c.dom.Element;

import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ATTRIBUTE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_REFERENCE;

/**
 * Parses databene model files.<br/><br/>
 * Created: 04.03.2008 16:43:09
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class ModelParser {

  private static final Set<String> SIMPLE_TYPE_COMPONENTS = CollectionUtil.toSet(EL_ATTRIBUTE, EL_REFERENCE, EL_ID);

  private final BeneratorContext context;
  private final AttributeParser attributeParser;
  private final PartParser partParser;
  private final VariableParser variableParser;
  private final IdParser idParser;
  private final ReferenceParser referenceParser;
  private final SimpleTypeArrayElementParser simpleTypeArrayElementParser;
  private final ItemListParser itemListParser;

  public ModelParser(BeneratorContext context, boolean nameRequired) {
    this.context = context;
    this.attributeParser = new AttributeParser(context, nameRequired);
    this.partParser = new PartParser(this, nameRequired);
    this.variableParser = new VariableParser(context);
    this.idParser = new IdParser(context);
    this.referenceParser = new ReferenceParser(context);
    this.simpleTypeArrayElementParser = new SimpleTypeArrayElementParser(context);
    this.itemListParser = new ItemListParser(this);
  }

  public BeneratorContext getContext() {
    return context;
  }

  public PartParser getPartParser() {
    return partParser;
  }

  public ItemListParser getItemListParser() {
    return itemListParser;
  }

  public static boolean isSimpleTypeComponent(String elementName) {
    return SIMPLE_TYPE_COMPONENTS.contains(elementName);
  }

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
      throw BeneratorExceptionFactory.getInstance().configurationError("Expected one of these element names: " +
          EL_ATTRIBUTE + ", " + EL_ID + " or " + EL_REFERENCE + ". Found: " + name);
    }
  }

  public PartDescriptor parseAttribute(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    return attributeParser.parse(element, owner, descriptor);
  }

  public PartDescriptor parsePart(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    return partParser.parse(element, owner, descriptor);
  }

  public InstanceDescriptor parseVariable(Element varElement) {
    return variableParser.parse(varElement);
  }

  private IdDescriptor parseId(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    return idParser.parse(element, owner, descriptor);
  }

  private ReferenceDescriptor parseReference(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    return referenceParser.parse(element, owner, descriptor);
  }

  public ArrayElementDescriptor parseSimpleTypeArrayElement(Element element, ArrayTypeDescriptor owner, int index) {
    return simpleTypeArrayElementParser.parse(element, owner, index);
  }

}
