/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.expression.ConstantExpression;
import org.w3c.dom.Element;

import java.util.Map;

import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.resolveScript;

/**
 * Parent class for component parsers.<br/><br/>
 * Created: 14.12.2021 05:13:36
 * @author Volker Bergmann
 * @since 2.1.0
 */
public abstract class AbstractComponentParser {

  protected final BeneratorContext context;
  protected final DescriptorProvider descriptorProvider;

  protected AbstractComponentParser(BeneratorContext context) {
    this.context = context;
    this.descriptorProvider = context.getLocalDescriptorProvider();
  }

  protected  <T extends InstanceDescriptor> T mapInstanceDetails(
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
          throw BeneratorExceptionFactory.getInstance().syntaxErrorForText(
              detailString, "Error parsing '" + detailName + "'");
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

  public void applyDefaultCounts(PartDescriptor descriptor) {
    if (descriptor.getDeclaredDetailValue("minCount") == null
        && descriptor.getTypeDescriptor() instanceof SimpleTypeDescriptor) {
      descriptor.setMinCount(new ConstantExpression<>(1L));
    }
    if (descriptor.getDeclaredDetailValue("maxCount") == null) {
      descriptor.setMaxCount(new ConstantExpression<>(1L));
    }
  }

}
