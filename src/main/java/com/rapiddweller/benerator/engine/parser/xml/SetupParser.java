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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.XMLElementParser;
import com.rapiddweller.script.DatabeneScriptParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_DATASET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_ENCODING;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_ERR_HANDLER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_IMPORTS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_LINE_SEPARATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_LOCALE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_NULL;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_ONE_TO_ONE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_PAGE_SIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_SCRIPT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_SEPARATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_SOURCE_SCRIPTED;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_TIME_ZONE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_GENERATOR_FACTORY;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_MAX_COUNT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_SETUP;

/**
 * {@link XMLElementParser} implementation for parsing a Benerator descriptor file's root XML element.<br/><br/>
 * Created: 14.12.2010 19:48:00
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class SetupParser extends AbstractBeneratorDescriptorParser {

  private static final Set<String> BENERATOR_PROPERTIES = CollectionUtil.toSet(
      ATT_DEFAULT_SCRIPT,
      ATT_DEFAULT_NULL,
      ATT_DEFAULT_ENCODING,
      ATT_DEFAULT_LINE_SEPARATOR,
      ATT_DEFAULT_TIME_ZONE,
      ATT_DEFAULT_LOCALE,
      ATT_DEFAULT_DATASET,
      ATT_DEFAULT_PAGE_SIZE,
      ATT_DEFAULT_SEPARATOR,
      ATT_DEFAULT_ONE_TO_ONE,
      ATT_DEFAULT_ERR_HANDLER,
      ATT_DEFAULT_SOURCE_SCRIPTED,
      ATT_MAX_COUNT,
      ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES,
      ATT_GENERATOR_FACTORY,
      ATT_DEFAULT_IMPORTS
  );

  private static final Set<String> XML_ATTRIBUTES = CollectionUtil.toSet(
      "xmlns", "xmlns:xsi", "xsi:schemaLocation"
  );

  private static final Set<String> OPTIONAL_ATTRIBUTES;

  static {
    OPTIONAL_ATTRIBUTES = new HashSet<>(BENERATOR_PROPERTIES);
    OPTIONAL_ATTRIBUTES.addAll(XML_ATTRIBUTES);
  }

  public SetupParser() {
    super(EL_SETUP, null, OPTIONAL_ATTRIBUTES);
  }

  @Override
  public boolean supports(Element element, Element[] parentXmlPath, Statement[] parentComponentPath) {
    return (supportsElementName(element.getNodeName()) && ArrayUtil.isEmpty(parentXmlPath));
  }

  @Override
  public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentPath, BeneratorParseContext context) {
    NamedNodeMap attrMap = element.getAttributes();
    // remove standard XML root attributes and verify that the remaining ones are legal
    Map<String, String> map = new HashMap<>(attrMap.getLength());
    BeneratorContext test = new DefaultBeneratorContext(); // test object to verify correctness of setup
    for (int i = 0; i < attrMap.getLength(); i++) {
      Attr attr = (Attr) attrMap.item(i);
      if (BENERATOR_PROPERTIES.contains(attr.getName())) {
        try {
          map(attr, test);
          map.put(attr.getName(), attr.getValue());
        } catch (Exception e) {
          throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(null, e, attr);
        }
      } else if (!isStandardXmlRootAttribute(attr.getName())) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlAttribute(
            "Illegal attribute", element.getAttributeNode(attr.getName()));
      }
    }
    // create root statement and configure its children
    BeneratorRootStatement rootStatement = new BeneratorRootStatement(map);
    Statement[] currentComponentPath = new Statement[] { rootStatement };
    Element[] currentXmlPath = new Element[] { element };
    List<Statement> subStatements = context.parseChildElementsOf(element, currentXmlPath, currentComponentPath);
    rootStatement.setSubStatements(subStatements);
    return rootStatement;
  }

  /** Checks if a configuratzion setting can be applied to a BeneratorContext */
  private void map(Attr attr, BeneratorContext test) {
    String name = attr.getName();
    Object value = attr.getValue();
    if ("generatorFactory".equals(name)) {
      value = DatabeneScriptParser.parseBeanSpec(attr.getValue()).evaluate(test);
    }
    BeanUtil.setPropertyValue(test, name, value, true, true);
  }

  private static boolean isStandardXmlRootAttribute(String key) {
    return XML_ATTRIBUTES.contains(key) || key.contains(":");
  }

}
