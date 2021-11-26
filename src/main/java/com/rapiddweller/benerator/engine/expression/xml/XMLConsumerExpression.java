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

package com.rapiddweller.benerator.engine.expression.xml;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.consumer.ConsumerChain;
import com.rapiddweller.benerator.consumer.NonClosingConsumerProxy;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.parser.xml.BeanParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.storage.StorageSystemInserter;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Escalator;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.expression.DynamicExpression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CLASS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_REF;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SPEC;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_CONSUMER;
import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

/**
 * Parses a {@link Consumer} specification in an XML element in a descriptor file.<br/><br/>
 * Created at 24.07.2009 07:21:16
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class XMLConsumerExpression extends DynamicExpression<Consumer> {

  private final Escalator escalator;

  private final Element entityElement;
  private final boolean consumersExpected;
  private final ResourceManager resourceManager;

  public XMLConsumerExpression(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
    this.entityElement = entityElement;
    this.consumersExpected = consumersExpected;
    this.escalator = new LoggerEscalator();
    this.resourceManager = resourceManager;
  }

  @Override
  public Consumer evaluate(Context context) {
    BeneratorContext beneratorContext = (BeneratorContext) context;
    ConsumerChain consumerChain = new ConsumerChain();

    // parse consumer attribute
    if (entityElement.hasAttribute(ATT_CONSUMER)) {
      String consumerSpec = parseStringAttribute(entityElement, ATT_CONSUMER, context);
      BeanSpec[] beanSpecs = DatabeneScriptParser.resolveBeanSpecList(consumerSpec, beneratorContext);
      if (beanSpecs != null) {
        for (BeanSpec beanSpec : beanSpecs) {
          addConsumer(beanSpec, beneratorContext, consumerChain);
        }
      }
    }

    // parse consumer sub elements
    Element[] consumerElements = XMLUtil.getChildElements(entityElement, true, EL_CONSUMER);
    for (Element consumerElement : consumerElements) {
      BeanSpec beanSpec;
      if (consumerElement.hasAttribute(ATT_REF)) {
        String ref = parseStringAttribute(consumerElement, ATT_REF, context);
        beanSpec = BeanSpec.createReference(beneratorContext.get(ref));
      } else if (consumerElement.hasAttribute(ATT_CLASS) || consumerElement.hasAttribute(ATT_SPEC)) {
        beanSpec = BeanParser.resolveBeanExpression(consumerElement, beneratorContext);
      } else {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "Can't handle this", consumerElement);
      }
      addConsumer(beanSpec, beneratorContext, consumerChain);
    }

    if (consumerChain.componentCount() == 0 && consumersExpected) {
      String entityName = parseStringAttribute(entityElement, ATT_NAME, context, false);
      escalator.escalate("No consumers defined for " + entityName, this, null);
    }
    for (Consumer consumer : consumerChain.getComponents()) {
      resourceManager.addResource(consumer);
    }
    return (consumerChain.componentCount() == 1 ? consumerChain.getComponent(0) : consumerChain);
  }

  public static void addConsumer(BeanSpec beanSpec, BeneratorContext context, ConsumerChain chain) {
    Consumer consumer;
    Object bean = beanSpec.getBean();
    // check consumer type
    if (bean instanceof Consumer) {
      consumer = (Consumer) bean;
    } else if (bean instanceof StorageSystem) {
      consumer = new StorageSystemInserter((StorageSystem) bean);
    } else {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(
          "Consumer type not supported: " + BeanUtil.simpleClassName(bean));
    }
    consumer = BeneratorFactory.getInstance().configureConsumer(consumer, context);
    if (beanSpec.isReference()) {
      consumer = new NonClosingConsumerProxy(consumer);
    }
    chain.addComponent(consumer);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '(' + XMLUtil.formatShort(entityElement) + ')';
  }

}
