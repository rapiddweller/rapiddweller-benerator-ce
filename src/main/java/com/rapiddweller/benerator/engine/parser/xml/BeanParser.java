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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.statement.BeanStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.parser.FullyQualifiedClassNameParser;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import com.rapiddweller.script.Assignment;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.BeanConstruction;
import com.rapiddweller.script.expression.DefaultConstruction;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CLASS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_REF;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SPEC;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_VALUE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_BEAN;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_PROPERTY;

/**
 * Parses a &lt;bean&gt; element.<br/><br/>
 * Created: 25.10.2009 01:09:59
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class BeanParser extends AbstractBeneratorDescriptorParser {

  // <bean> format spec ----------------------------------------------------------------------------------------------

  // Named staticLogger to avoid name clash with logger from parent class
  private static final Logger staticLogger = LoggerFactory.getLogger(BeanParser.class);

  /** 'id' attribute info for &lt;bean&gt; elements */
  private static final AttributeInfo<String> ID_REQ = new AttributeInfo<>(
      ATT_ID, true, BeneratorErrorIds.SYN_BEAN_ID, new IdParser(), null);

  /** 'id' attribute info for &lt;consumer&gt; and &lt;runtask&gt; elements and others */
  private static final AttributeInfo<String> ID_OPT = new AttributeInfo<>(
      ATT_ID, false, BeneratorErrorIds.SYN_BEAN_ID, new IdParser(), null);

  private static final AttributeInfo<String> CLASS = new AttributeInfo<>(
      ATT_CLASS, false, BeneratorErrorIds.SYN_BEAN_CLASS,
      new FullyQualifiedClassNameParser(false), null
  );

  private static final AttributeInfo<String> SPEC = new AttributeInfo<>(
      ATT_SPEC, false, BeneratorErrorIds.SYN_BEAN_SPEC, null, null);

  private static final AttrInfoSupport BEAN_ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_BEAN_ILLEGAL_ATTR, new BeanElementValidator(), ID_REQ, CLASS, SPEC);

  // <property> format spec ------------------------------------------------------------------------------------------

  private static final AttributeInfo<String> PROP_NAME = new AttributeInfo<>(
      ATT_NAME, true, BeneratorErrorIds.SYN_BEAN_PROP_NAME, new IdParser(), null);

  private static final AttributeInfo<String> PROP_VALUE = new AttributeInfo<>(
      ATT_VALUE, false, BeneratorErrorIds.SYN_BEAN_PROP_VALUE, null, null);

  private static final AttributeInfo<String> PROP_DEFAULT = new AttributeInfo<>(
      ATT_DEFAULT, false, BeneratorErrorIds.SYN_BEAN_PROP_DEFAULT, null, null);

  private static final AttributeInfo<String> PROP_REF = new AttributeInfo<>(
      ATT_REF, false, BeneratorErrorIds.SYN_BEAN_PROP_REF, null, null);

  private static final AttributeInfo<String> PROP_SOURCE = new AttributeInfo<>(
      ATT_SOURCE, false, BeneratorErrorIds.SYN_BEAN_PROP_SOURCE, null, null);

  private static final AttrInfoSupport PROP_ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_BEAN_PROP_ELEMENT, new BeanPropertyValidator(),
      PROP_NAME, PROP_VALUE, PROP_DEFAULT, PROP_REF, PROP_SOURCE);

  // constructor & interface -----------------------------------------------------------------------------------------

  public BeanParser() {
    // only allowed in non-loop statements in order to avoid leaks
    super(EL_BEAN, BEAN_ATTR_INFO, BeneratorRootStatement.class, IfStatement.class);
  }

  @Override
  public BeanStatement doParse(Element element, Element[] parentXmlPath, Statement[] parentPath, BeneratorParseContext context) {
    attrSupport.validate(element);
    try {
      String id = ID_REQ.parse(element);
      Expression<?> bean = parseBeanExpression(element, true);
      return new BeanStatement(id, bean, context.getResourceManager());
    } catch (ConversionException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing bean element", e);
    }
  }

  // public static utility methods -----------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Expression<?> parseBeanExpression(Element element, boolean idRequired) {
    String id = (idRequired ? ID_REQ : ID_OPT).parse(element);
    String beanSpec = SPEC.parse(element);
    String beanClass = CLASS.parse(element);
    Expression<?> instantiation;
    if (beanSpec != null) {
      try {
        instantiation = DatabeneScriptParser.parseBeanSpec(beanSpec);
      } catch (ParseException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing bean spec: " + beanSpec, e);
      }
    } else if (beanClass != null) {
      staticLogger.debug("Instantiating bean of class {} (id={})", beanClass, id);
      instantiation = new DefaultConstruction(beanClass);
    } else {
      throw ExceptionFactory.getInstance().syntaxErrorForXmlElement("bean definition is missing 'class' or 'spec' attribute", element);
    }
    Element[] propertyElements = XMLUtil.getChildElements(element, false, EL_PROPERTY);
    Assignment[] propertyInitializers = mapPropertyDefinitions(propertyElements);
    return new BeanConstruction(instantiation, propertyInitializers);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static BeanSpec resolveLocalBeanExpression(Element element, BeneratorContext context) {
    String id = ID_OPT.parse(element);
    String beanClass = CLASS.parse(element);
    String beanSpec = SPEC.parse(element);
    Expression<?> instantiation;
    boolean ref = false;
    if (beanSpec != null) {
      try {
        BeanSpec specObject = DatabeneScriptParser.resolveBeanSpec(beanSpec, context);
        instantiation = ExpressionUtil.constant(specObject.getBean());
        ref = specObject.isReference();
      } catch (ParseException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing bean spec: " + beanSpec, e);
      }
    } else if (beanClass != null) {
      staticLogger.debug("Instantiating bean of class {} (id={})", beanClass, id);
      instantiation = new DefaultConstruction<>(beanClass);
    } else {
      throw BeneratorExceptionFactory.getInstance().configurationError("Syntax error in definition of bean " + id);
    }
    Element[] propertyElements = XMLUtil.getChildElements(element);
    for (Element propertyElement : propertyElements) {
      if (!EL_PROPERTY.equals(propertyElement.getNodeName())) {
        throw ExceptionFactory.getInstance().syntaxErrorForXmlElement("not a supported bean child element", propertyElement);
      }
      PROP_ATTR_INFO.validate(propertyElement);
    }
    Assignment[] propertyInitializers = mapPropertyDefinitions(propertyElements);
    Object result = new BeanConstruction(instantiation, propertyInitializers).evaluate(context);
    return new BeanSpec(result, ref);
  }

  public static Assignment[] mapPropertyDefinitions(Element[] propertyElements) {
    Assignment[] assignments = new Assignment[propertyElements.length];
    for (int i = 0; i < propertyElements.length; i++) {
      assignments[i] = parseProperty(propertyElements[i]);
    }
    return assignments;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private static Assignment parseProperty(Element propertyElement) {
    String propertyName = PROP_NAME.parse(propertyElement);
    Expression<?> value = SettingParser.parseValue(propertyElement);
    return new Assignment(propertyName, value);
  }

  static class BeanElementValidator implements Validator<Element> {
    @Override
    public boolean valid(Element element) {
      XMLAssert.assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_BEAN, ATT_CLASS, ATT_SPEC);
      XMLAssert.mutuallyExcludeAttributes(element, ATT_CLASS, ATT_SPEC);
      return true;
    }
  }

  static class BeanPropertyValidator implements Validator<Element> {
    @Override
    public boolean valid(Element element) {
      XMLAssert.assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_BEAN, ATT_VALUE, ATT_REF, ATT_DEFAULT, ATT_SOURCE);
      XMLAssert.mutuallyExcludeAttributes(element, ATT_VALUE, ATT_REF, ATT_DEFAULT, ATT_SOURCE);
      return true;
    }
  }

}
