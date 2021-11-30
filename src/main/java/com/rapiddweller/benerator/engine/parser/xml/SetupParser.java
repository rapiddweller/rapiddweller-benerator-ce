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
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.XMLElementParser;
import com.rapiddweller.script.DatabeneScriptParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * {@link XMLElementParser} implementation for parsing a Benerator descriptor file's root XML element.<br/><br/>
 * Created: 14.12.2010 19:48:00
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class SetupParser extends AbstractBeneratorDescriptorParser {

  protected final static AttrInfoSupport ATTR_CONSTR;

  static {
    ATTR_CONSTR = new AttrInfoSupport(SYN_SETUP_ILLEGAL_ATTRIBUTE);
    ATTR_CONSTR.add(ATT_MAX_COUNT, false, SYN_SETUP_MAX_COUNT);
    ATTR_CONSTR.add(ATT_DEFAULT_SCRIPT, false, SYN_SETUP_DEF_SCRIPT);
    ATTR_CONSTR.add(ATT_DEFAULT_NULL, false, SYN_SETUP_DEF_NULL);
    ATTR_CONSTR.add(ATT_DEFAULT_ENCODING, false, SYN_SETUP_DEF_ENCODING);
    ATTR_CONSTR.add(ATT_DEFAULT_LINE_SEPARATOR, false, SYN_SETUP_DEF_LINE_SEPARATOR);
    ATTR_CONSTR.add(ATT_DEFAULT_TIME_ZONE, false, SYN_SETUP_DEF_TIME_ZONE);
    ATTR_CONSTR.add(ATT_DEFAULT_LOCALE, false, SYN_SETUP_DEF_LOCALE);
    ATTR_CONSTR.add(ATT_DEFAULT_DATASET, false, SYN_SETUP_DEF_DATASET);
    ATTR_CONSTR.add(ATT_DEFAULT_PAGE_SIZE, false, SYN_SETUP_DEF_PAGE_SIZE);
    ATTR_CONSTR.add(ATT_DEFAULT_SEPARATOR, false, SYN_SETUP_DEF_SEPARATOR);
    ATTR_CONSTR.add(ATT_DEFAULT_ONE_TO_ONE, false, SYN_SETUP_DEF_ONE_TO_ONE);
    ATTR_CONSTR.add(ATT_DEFAULT_ERR_HANDLER, false, SYN_SETUP_DEF_ERR_HANDLER);
    ATTR_CONSTR.add(ATT_DEFAULT_IMPORTS, false, SYN_SETUP_DEF_IMPORTS);
    ATTR_CONSTR.add(ATT_DEFAULT_SOURCE_SCRIPTED, false, SYN_SETUP_DEF_SOURCE_SCRIPTED);
    ATTR_CONSTR.add(ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES, false, SYN_SETUP_ACCEPT_UNK_SIMPLE_TYPES);
    ATTR_CONSTR.add(ATT_GENERATOR_FACTORY, false, SYN_SETUP_GENERATOR_FACTORY);
  }

  public SetupParser() {
    super(EL_SETUP, ATTR_CONSTR);
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
    try (BeneratorContext test = new DefaultBeneratorContext()) { // test object to verify correctness of setup
      for (int i = 0; i < attrMap.getLength(); i++) {
        Attr attr = (Attr) attrMap.item(i);
        if (ATTR_CONSTR.get(attr.getName()) != null) {
          try {
            map(attr, test);
            map.put(attr.getName(), attr.getValue());
          } catch (Exception e) {
            throw illegalAttributeValue(attr);
          }
        } else if (!isStandardXmlRootAttribute(attr.getName())) {
          throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeName(
              null, null, attrSupport.getErrorIdForIllegalAttribute(), attr, attrSupport);
        }
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

  /** Checks if a configuration setting can be applied to a BeneratorContext */
  private void map(Attr attr, BeneratorContext test) {
    String name = attr.getName();
    String valueString = attr.getValue();
    Object valueObject = valueString;
    if (ATT_GENERATOR_FACTORY.equals(name)) {
      Assert.isFalse(StringUtil.isEmpty(valueString), ATT_GENERATOR_FACTORY + " is empty");
      valueObject = DatabeneScriptParser.parseBeanSpec(valueString).evaluate(test);
    }
    BeanUtil.setPropertyValue(test, name, valueObject, true, true);
  }

  private SyntaxError illegalAttributeValue(Attr attr) {
    String errorId = attrSupport.getErrorId(attr.getName());
    return BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(null, null, errorId, attr);
  }

}
